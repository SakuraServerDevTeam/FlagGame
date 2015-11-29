/* 
 * Copyright (C) 2015 Toyblocks, SakuraServerDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jp.llv.flaggame.reception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.game.basic.BasicGame;
import syam.flaggame.FlagGame;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.StageReservedException;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
@ReceptionFor(BasicGame.class)
public class RealtimeTeamingReception implements GameReception {
    
    private final FlagGame plugin;
    private final String id;
    private final Map<TeamColor, Set<GamePlayer>> players = new EnumMap<>(TeamColor.class);
    private final Stage stage;
    private Stage.Reservation stageReservation;
    private BasicGame game;
    private State state = State.READY;
    
    public RealtimeTeamingReception(FlagGame plugin, String id, List<String> args) {
        this.plugin = plugin;
        this.id = id;
        if (args.size() < 1) {
            throw new IllegalArgumentException("The first argument must be a stage name");
        }
        this.stage = plugin.getStages().getStage(args.get(0))
                .orElseThrow(() -> new IllegalArgumentException("No such stage"));
    }
    
    @Override
    public Collection<GamePlayer> getPlayers() {
        Set<GamePlayer> result = new HashSet<>();
        this.players.values().stream().forEach(result::addAll);
        return Collections.unmodifiableSet(result);
    }
    
    @Override
    public void open(List<String> args) throws CommandException {
        if (this.getState() != State.READY) {
            throw new CommandException("&cこの募集は既に開始されました!");
        }
        
        try {
            this.stage.validate();
        } catch (NullPointerException ex) {
            throw new CommandException("&cそのステージは設定が無効です!");
        }
        
        try {
            this.stageReservation = stage.reserve(this);
        } catch (StageReservedException ex) {
            throw new CommandException("&cそのステージは既に使用中です!", ex);
        }
        
        for (TeamColor color : this.stage.getSpawns().keySet()) {
            this.players.put(color, new HashSet<>());
        }
        this.state = State.OPENED;
        GamePlayer.sendMessage(this.plugin.getPlayers(), "&2フラッグゲーム'&6" + this.getName() + "&2'の参加受付が開始されました！");
        GamePlayer.sendMessage(this.plugin.getPlayers(), "&2 '&6/flag join " + this.getID() + "&2' コマンドで参加してください！");
    }
    
    @Override
    public void close(String reason) {
        if (this.getState() == State.STARTED) {
            this.stop(reason);
        }
        
        if (this.stageReservation != null) {
            this.stageReservation.release();
        }
        this.state = State.CLOSED;
    }
    
    @Override
    public void join(GamePlayer player, List<String> args) throws CommandException {
        if (this.getState() != State.OPENED) {
            throw new CommandException("&cこの募集は既に開始されました!");
        }

        //人数でチームをマッピング
        Map<Integer, List<TeamColor>> m = new HashMap<>();
        for (Map.Entry<TeamColor, Set<GamePlayer>> e : this.players.entrySet()) {
            if (!m.containsKey(e.getValue().size())) {
                m.put(e.getValue().size(), new ArrayList<>());
            }
            m.get(e.getValue().size()).add(e.getKey());
        }
        int min = m.keySet().stream().mapToInt(i -> i).min()
                .orElseThrow(() -> new CommandException("&c参加可能チームがありません!"));
        List<TeamColor> can = m.get(min);
        TeamColor color = can.get((int) (Math.random() * can.size()));
        
        this.players.get(color).add(player);
        player.join(this, args);
        GamePlayer.sendMessage(this.plugin.getPlayers(), color.getColor() + player.getName() + "&aが'" + this.getID() + "'へエントリーしました");
    }
    
    @Override
    public void leave(GamePlayer player) {
        if (this.getState() == State.STARTED) {
            throw new IllegalStateException();
        }
        
        for (Set<GamePlayer> team : this.players.values()) {
            if (team.contains(player)) {
                team.remove(player);
                player.leave(this);
                GamePlayer.sendMessage(this.plugin.getPlayers(), player.getColoredName() + "&aが'" + this.getID() + "'へのエントリーを取り消しました");
                return;
            }
        }
        throw new IllegalArgumentException("That player is not joined");
    }
    
    @Override
    public void start(List<String> args) throws CommandException {
        if (this.getState() != State.OPENED) {
            throw new IllegalStateException();
        }
        //Build teams
        Set<Team> teams = new HashSet<>();
        for (Map.Entry<TeamColor, Set<GamePlayer>> e : this.players.entrySet()) {
            teams.add(new Team(this, e.getKey(), e.getValue()));
        }
        //start game
        this.game = new BasicGame(this.plugin, this, this.stage, teams);
        this.game.startLater(10000L);
    }
    
    @Override
    public void stop(String reason) throws IllegalStateException {
        if (this.getState() != State.STARTED) {
            throw new IllegalStateException();
        }
        
        this.game.stopForcibly(reason);
        this.state = State.FINISHED;
    }
    
    @Override
    public Optional<Game> getGame() {
        return Optional.ofNullable(this.game);
    }
    
    @Override
    public String getID() {
        return this.id;
    }
    
    @Override
    public String getName() {
        return this.stage.getName();
    }
    
    @Override
    public State getState() {
        //まずゲームと状態を同期
        if (this.state == State.OPENED 
                && this.getGame().map(Game::getState).map(Game.State.STARTED::equals).orElse(Boolean.FALSE)) {
            this.state = State.STARTED;
        }
        if (this.state == State.STARTED && this.game.getState() == Game.State.FINISHED) {
            this.state = State.FINISHED;
        }
        return this.state;
    }
    
    @Override
    public double getEntryFee() {
        return 0;
    }
    
    @Override
    public double getMaxAward() {
        return 0;
    }
    
    @Override
    public Iterator<GamePlayer> iterator() {
        return this.getPlayers().iterator();
    }
    
}
