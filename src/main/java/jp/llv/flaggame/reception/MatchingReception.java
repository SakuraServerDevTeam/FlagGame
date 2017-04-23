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
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.game.basic.BasicGame;
import jp.llv.flaggame.profile.PlayerProfile;
import jp.llv.flaggame.profile.record.PlayerEntryRecord;
import jp.llv.flaggame.profile.record.PlayerLeaveRecord;
import jp.llv.flaggame.profile.record.PlayerTeamRecord;
import jp.llv.flaggame.util.ValueSortedMap;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author Toyblocks
 */
@ReceptionFor(BasicGame.class)
public class MatchingReception extends SingleGameReception<BasicGame> implements GameReception {

    protected final Set<GamePlayer> players = new HashSet<>();

    public MatchingReception(FlagGame plugin, UUID id, List<String> args) {
        super(plugin, id, args);
    }

    @Override
    public Collection<GamePlayer> getPlayers() {
        return Collections.unmodifiableCollection(players);
    }

    @Override
    public void join(GamePlayer player, List<String> args) throws FlagGameException {
        if (this.getState() != State.OPENED) {
            throw new CommandException("&cこの募集は既に終了しました!");
        }

        double cost = stage.getEntryFee();
        // 参加料チェック
        if (cost > 0.0) {
            // 所持金確認
            if (!Actions.checkMoney(player.getUUID(), cost)) {
                throw new CommandException("&c参加するためには参加料 " + Actions.formatMoney(cost) + " が必要です！");
            }
            // 引き落とし
            if (!Actions.takeMoney(player.getUUID(), cost)) {
                throw new CommandException("&c参加料の引き落としにエラーが発生しました。管理人までご連絡ください。");
            } else {
                player.sendMessage("&c参加料として " + Actions.formatMoney(cost) + "Coin を支払いました！");
            }
        }

        this.players.add(player);
        player.join(this, args);
        getRecordStream().push(new PlayerEntryRecord(id, player.getPlayer()));
        int count = this.players.size();
        long level = plugin.getProfiles().getProfile(player.getUUID()).getLevel().orElse(0);
        GamePlayer.sendMessage(this.plugin.getPlayers(), player.getName() + "&7(Lv" + level + ")&aが'&6"
                                                         + this.getName() + "&a'で開催予定のゲームに参加しました(&6" + count + "人目&a)");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void leave(GamePlayer player) {
        if (this.getState() == State.STARTED) {
            throw new IllegalStateException();
        }
        if (!players.contains(player)) {
            throw new IllegalArgumentException("That player is not joined");
        }
        players.remove(player);
        player.leave(this);
        if (getState().toGameState() != Game.State.FINISHED) {
            getRecordStream().push(new PlayerLeaveRecord(id, player.getPlayer()));
            GamePlayer.sendMessage(this.plugin.getPlayers(), player.getColoredName() + "&aが'" + this.getName() + "'で開催予定のゲームへのエントリーを取り消しました");
        }
        if (players.isEmpty()) {
            this.close("All players has left");
        }
    }

    @Override
    public void start(List<String> args) throws FlagGameException {
        if (this.getState() != State.OPENED) {
            throw new IllegalStateException();
        }
        if (!isStageInitialized()) {
            throw new CommandException("&cステージの初期化が完了していません！");
        }

        //-stage1; get all players' vibe
        ValueSortedMap<GamePlayer, Double> vibes = ValueSortedMap.newInstance(Comparator.reverseOrder());
        for (GamePlayer player : players) {
            vibes.put(player, plugin.getProfiles().getProfile(player.getUUID()).getVibe().orElse(0.0));
        }

        //-stage2; prepare collections
        List<TeamColor> teamColors = new ArrayList<>(stage.getSpawns().keySet());
        // without this, the strongest player is always in the fixed team
        Collections.shuffle(teamColors);
        Map<TeamColor, List<GamePlayer>> teams = new EnumMap<>(TeamColor.class);
        ValueSortedMap<TeamColor, Double> teamVibes = ValueSortedMap.newInstance();
        ArrayList<PlayerTeamRecord> teamRecords = new ArrayList<>(players.size());
        for (TeamColor color : teamColors) {
            teams.put(color, new ArrayList<>(players.size() / teamColors.size() + 1));
            teamVibes.put(color, 0.0);
        }
        if (players.size() < teamColors.size()) {
            throw new CommandException("&c全チームにプレイヤーを配置できる人数が揃っていません！");
        }

        //-stage3; teaming most players
        Iterator<Map.Entry<GamePlayer, Double>> it = vibes.entrySet().iterator();
        int count = players.size();
        int remainder = count % teamColors.size();
        int teamIndex = 0;
        while (it.hasNext() && count-- > remainder) {
            Map.Entry<GamePlayer, Double> entry = it.next();
            TeamColor teamColor = teamColors.get(teamIndex++ % teamColors.size());
            teams.get(teamColor).add(entry.getKey());
            teamVibes.compute(teamColor, (k, v) -> v + entry.getValue());
        }

        //-stage4; teaming the others
        for (Map.Entry<TeamColor, Double> teamVibe : teamVibes.entrySet()) {
            if (!it.hasNext()) {
                break;
            }
            teams.get(teamVibe.getKey()).add(it.next().getKey());
        }

        //-stage5; build teams
        List<Team> bakedTeam = new ArrayList<>();
        for (Map.Entry<TeamColor, List<GamePlayer>> entry : teams.entrySet()) {
            // without this, the strongest player alyways comes at the top of a list
            Collections.shuffle(entry.getValue());
            bakedTeam.add(new Team(this, entry.getKey(), entry.getValue()));
        }

        //-stage6; try starting game
        try {
            this.game = new BasicGame(this.plugin, this, this.stage, bakedTeam);
            this.game.startLater(10000L);
        } catch (CommandException ex) {
            this.game = null;
            throw ex;
        }
        setState(State.STARTING);

        //-stage7; announce
        GamePlayer.sendMessage(plugin.getPlayers(), "&2フラッグゲーム'&6" + getName() + "&2'の参加受付が終了しました！");
        GamePlayer.sendMessage(plugin.getPlayers(),
                "&2参加者一覧: " + players.stream().map(p -> {
                    PlayerProfile profile = plugin.getProfiles().getProfile(p.getUUID());
                    return p.getColoredName() + "&7(Lv"+profile.getLevel().orElse(0) + ")";
                }).collect(Collectors.joining(", "))
        );

        //-stage8; push records
        for (PlayerTeamRecord record : teamRecords) {
            getRecordStream().push(record);
        }
    }

}
