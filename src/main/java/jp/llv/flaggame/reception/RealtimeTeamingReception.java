/* 
 * Copyright (C) 2017 SakuraServerDev
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
import java.util.UUID;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.game.basic.BasicGame;
import jp.llv.flaggame.profile.GameRecordStream;
import jp.llv.flaggame.profile.RecordStream;
import jp.llv.flaggame.profile.record.PlayerEntryRecord;
import jp.llv.flaggame.profile.record.PlayerLeaveRecord;
import jp.llv.flaggame.profile.record.PlayerTeamRecord;
import jp.llv.flaggame.profile.record.ReceptionCloseRecord;
import jp.llv.flaggame.profile.record.ReceptionOpenRecord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import syam.flaggame.FlagGame;
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

    protected final FlagGame plugin;
    private final UUID id;
    protected final Map<TeamColor, Set<GamePlayer>> players = new EnumMap<>(TeamColor.class);
    protected final Stage stage;
    private Stage.Reservation stageReservation;
    private BasicGame game;
    private State state = State.READY;
    private final RecordStream records;

    public RealtimeTeamingReception(FlagGame plugin, UUID id, List<String> args) {
        this.plugin = plugin;
        this.id = id;
        if (args.size() < 1) {
            throw new IllegalArgumentException("The first argument must be a stage name");
        }
        this.stage = plugin.getStages().getStage(args.get(0))
                .orElseThrow(() -> new IllegalArgumentException("No such stage"));
        this.records = new GameRecordStream(id);
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
        this.records.push(new ReceptionOpenRecord(this.id));
        GamePlayer.sendMessage(this.plugin.getPlayers(), "&2フラッグゲーム'&6" + this.getName() + "&2'の参加受付が開始されました！");
        BaseComponent[] message = new ComponentBuilder("ここをクリック").bold(true).color(ChatColor.GOLD).bold(false)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("クリックして参加申し込みします").color(ChatColor.GOLD).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/flag join " + this.getID()))
                .append("して参加してください！").color(ChatColor.DARK_GREEN).create();
        GamePlayer.sendMessage(this.plugin.getPlayers(), message);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void close(String reason) {
        this.stop(reason);

        for (GamePlayer p : this.getPlayers()) {
            for (Set<GamePlayer> team : this.players.values()) {
                if (team.contains(p)) {
                    team.remove(p);
                    p.leave(this);
                }
            }
        }

        this.plugin.getReceptions().remove(this);
        this.state = State.CLOSED;
        this.records.push(new ReceptionCloseRecord(this.id));
    }

    @Override
    public void join(GamePlayer player, List<String> args) throws CommandException {
        if (this.getState() != State.OPENED) {
            throw new CommandException("&cこの募集は既に終了しました!");
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
        this.records.push(new PlayerEntryRecord(id, player.getPlayer()));
        this.records.push(new PlayerTeamRecord(id, player.getPlayer(), color));
        int count = this.players.entrySet().stream().map(Map.Entry::getValue).mapToInt(Set::size).sum();
        GamePlayer.sendMessage(this.plugin.getPlayers(), color.getColor() + player.getName() + "&aが'&6"
                                                         + this.getName() + "&a'で開催予定のゲームに参加しました(&6" + count + "人目&a)");
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
                this.records.push(new PlayerLeaveRecord(id, player.getPlayer()));
                GamePlayer.sendMessage(this.plugin.getPlayers(), player.getColoredName() + "&aが'" + this.getName() + "'で開催予定のゲームへのエントリーを取り消しました");
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
        try {
            this.game = new BasicGame(this.plugin, this, this.stage, teams);
            this.game.startLater(10000L);
        } catch (CommandException ex) {
            this.game = null;
            throw ex;
        }
        // successfully started
        this.state = State.STARTING;
        GamePlayer.sendMessage(this.plugin.getPlayers(), "&2フラッグゲーム'&6" + this.getName() + "&2'の参加受付が終了しました！");
    }

    @Override
    public void stop(String reason) throws IllegalStateException {
        if (this.getState() == State.STARTED) {
            this.game.stopForcibly(reason);
            this.state = State.FINISHED;
        }
        if (stageReservation != null) {
            stageReservation.release();
            stageReservation = null;
        }
    }

    @Override
    public Optional<Game> getGame() {
        return Optional.ofNullable(this.game);
    }

    @Override
    public UUID getID() {
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
        if (this.state == State.STARTING
                && this.game.getState() != Game.State.PREPARATION) {
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

    @Override
    public RecordStream getRecordStream() {
        return this.records;
    }

    @Override
    public Optional<Stage> getStage() {
        return Optional.of(stage);
    }

}
