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
package syam.flaggame.command;

import java.util.stream.Collectors;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.reception.GameReception;
import jp.llv.flaggame.reception.Team;
import syam.flaggame.FlagGame;

import jp.llv.flaggame.reception.TeamColor;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

public class SInfoCommand extends BaseCommand {

    public SInfoCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "sinfo";
        argLength = 0;
        usage = "[stage] <- show stage info";
    }

    @Override
    public void execute() throws CommandException {
        // 引数が無ければすべてのステージデータを表示する
        if (args.isEmpty()) {
            int stagecount = this.plugin.getStages().getStages().size();

            Actions.message(sender, "&a ===============&b StageList(" + stagecount + ") &a===============");
            if (stagecount == 0) {
                Actions.message(sender, " &7読み込まれているステージがありません");
            } else {
                for (Stage stage : this.plugin.getStages()) {
                    // ゲームステータス取得
                    String status = stage.getReception().map(GameReception::getState)
                            .map(GameReception.State::toGameState)
                            .map(s -> s == Game.State.PREPARATION ? "&6受付中" : "&c開始中").orElse("&7待機中");

                    String s = "&6" + stage.getName() + "&b: 状態=&f" + status + "&b 制限時間=&6" + Actions.getTimeString(stage.getGameTimeInSec()) + "&b チェスト数=&6" + stage.getChests().size();

                    // メッセージ送信
                    Actions.message(sender, s);
                }
            }
            Actions.message(sender, "&a ============================================");
        } // 引数があれば指定したゲームについての詳細情報を表示する
        else {
            Stage stage = this.plugin.getStages().getStage(args.get(0))
                    .orElseThrow(() -> new CommandException("&cそのステージは存在しません！"));

            Actions.message(sender, "&a ==================&b GameDetail &a==================");

            // ゲームステータス取得
            String status = stage.getReception().map(GameReception::getState).map(GameReception.State::toGameState)
                    .map(s -> s == Game.State.PREPARATION ? "&6受付中" : "&c開始中").orElse("&7待機中");

            String chksp_red = "&c未設定";
            String chksp_blue = "&c未設定";
            if (stage.getSpawn(TeamColor.RED) != null) {
                chksp_red = "&6設定済";
            }
            if (stage.getSpawn(TeamColor.BLUE) != null) {
                chksp_blue = "&6設定済";
            }

            // プレイヤーリスト構築
            String players = "";
            int cnt_players = 0;
            if (stage.isReserved() && stage.getReception().get().getGame().isPresent()) {
                for (Team entry : stage.getReception().get().getGame().get().getTeams()) {
                    String color = entry.getColor().getColor();
                    for (GamePlayer n : entry) {
                        players = players + color + n.getName() + "&f, ";
                        cnt_players++;
                    }
                }
            }
            if (!"".equals(players)) {
                players = players.substring(0, players.length() - 2);
            } else {
                players = "&7参加プレイヤーなし";
            }

            String s1 = "&6 " + stage.getName()
                        + "&b: 状態=&f" + status
                        + "&b 制限時間=&6" + Actions.getTimeString(stage.getGameTimeInSec())
                        + "&b フラッグ数=&6" + stage.getFlags().size();
            String s2 = "&b 製作者=&6" + stage.getAuthor()
                        + "&b 説明=&6" + stage.getDescription()
                        + " &bガイド=&6" + stage.getGuide();
            String s3 = "&b チーム毎人数制限=&6" + stage.getTeamLimit()
                    + "&b チーム=&6" + stage.getSpawns().keySet().stream().map(TeamColor::toString).collect(Collectors.joining("/"));
            String s5 = "&b キル得点=&6" + stage.getKillScore() + "&b デス得点=&6" + stage.getDeathScore();
            String s6 = "&b フラッグ数=&6" + stage.getFlags().size()
                        + "&b バナースポナー数=&6" + stage.getBannerSpawners().size()
                        + "&b バナースロット数=&6" + stage.getBannerSlots().size()
                        + "&b コア数=&6" + stage.getNexuses().size();
            String s4 = "&b プレイヤーリスト&7(" + cnt_players + "人)&b: " + players;

            // メッセージ送信
            Actions.message(sender, s1);
            Actions.message(sender, s2);
            Actions.message(sender, s3);
            Actions.message(sender, s4);
            Actions.message(sender, s5);
            Actions.message(sender, s6);

            Actions.message(sender, "&a ================================================");
        }
    }

    @Override
    public boolean permission() {
        return Perms.SINFO.has(sender);
    }
}
