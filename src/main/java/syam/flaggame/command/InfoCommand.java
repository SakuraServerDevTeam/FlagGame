/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.command;

import java.util.Map;
import java.util.Set;

import syam.flaggame.enums.TeamColor;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Game;
import syam.flaggame.game.Stage;
import syam.flaggame.manager.StageManager;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

public class InfoCommand extends BaseCommand {

    public InfoCommand() {
        bePlayer = false;
        name = "info";
        argLength = 0;
        usage = "[stage] <- show stage info";
    }

    @Override
    public void execute() throws CommandException {
        // 引数が無ければすべてのステージデータを表示する
        if (args.isEmpty()) {
            int stagecount = StageManager.getStages().size();

            Actions.message(sender, "&a ===============&b StageList(" + stagecount + ") &a===============");
            if (stagecount == 0) {
                Actions.message(sender, " &7読み込まれているステージがありません");
            } else {
                for (Stage stage : StageManager.getStages().values()) {
                    // ゲームステータス取得
                    String status = "&7待機中";
                    if (stage.isUsing() && stage.getGame() != null) {
                        if (stage.getGame().getState() == Game.State.STARTED) {
                            // 開始中なら残り時間も表示
                            String time = Actions.getTimeString(stage.getGame().getRemainTime());
                            status = "&c開始中&7(あと:" + time + ")";
                        } else {
                            status = "&6受付中";
                        }
                    }

                    String s = "&6" + stage.getName() + "&b: 状態=&f" + status + "&b 制限時間=&6" + Actions.getTimeString(stage.getGameTime()) + "&b フラッグ数=&6" + stage.getFlags().size();

                    // メッセージ送信
                    Actions.message(sender, s);
                }
            }
            Actions.message(sender, "&a ============================================");
        } // 引数があれば指定したゲームについての詳細情報を表示する
        else {
            Stage stage = StageManager.getStage(args.get(0));
            if (stage == null) {
                throw new CommandException("&cそのステージは存在しません！");
            }

            Actions.message(sender, "&a ==================&b GameDetail &a==================");

            // ゲームステータス取得
            String status = "&7待機中";
            if (stage.isUsing() && stage.getGame() != null) {
                if (stage.getGame().getState() == Game.State.STARTED) {
                    // 開始中なら残り時間も表示
                    String time = Actions.getTimeString(stage.getGame().getRemainTime());
                    status = "&c開始中&7(あと:" + time + ")";
                } else {
                    status = "&6受付中";
                }
            }

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
            if (stage.isUsing() && stage.getGame() != null) {
                for (Map.Entry<TeamColor, Set<GamePlayer>> entry : stage.getGame().getPlayersMap().entrySet()) {
                    String color = entry.getKey().getColor();
                    for (GamePlayer n : entry.getValue()) {
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

            String s1 = "&6 " + stage.getName() + "&7(" + stage.getFileName() + ")" + "&b: 状態=&f" + status + "&b 制限時間=&6" + Actions.getTimeString(stage.getGameTime()) + "&b フラッグ数=&6" + stage.getFlags().size();
            String s2 = "&b 参加料=&6" + stage.getEntryFee() + "&b 賞金=&6" + stage.getAward() + "&b チェスト数=&6" + stage.getChests().size();
            String s3 = "&b チーム毎人数制限=&6" + stage.getTeamLimit() + "&b 赤チームスポーン=" + chksp_red + "&b 青チームスポーン=" + chksp_blue;
            String s4 = "&b プレイヤーリスト&7(" + cnt_players + "人)&b: " + players;

            // メッセージ送信
            Actions.message(sender, s1);
            Actions.message(sender, s2);
            Actions.message(sender, s3);
            Actions.message(sender, s4);

            Actions.message(sender, "&a ================================================");
        }
    }

    @Override
    public boolean permission() {
        return Perms.INFO.has(sender);
    }
}
