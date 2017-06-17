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
package syam.flaggame.command.stage;

import java.util.List;
import jp.llv.flaggame.game.Game;
import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;
import jp.llv.flaggame.api.reception.Reception;

public class StageInfoCommand extends BaseCommand {

    public StageInfoCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                0,
                "[stage] <- show stage info",
                Perms.STAGE_INFO,
                "info"
        );
    
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        // 引数が無ければすべてのステージデータを表示する
        if (args.isEmpty()) {
            int stagecount = this.plugin.getStages().getStages().size();

            Actions.message(sender, "&a ===============&b StageList(" + stagecount + ") &a===============");
            if (stagecount == 0) {
                Actions.message(sender, " &7読み込まれているステージがありません");
            } else {
                for (Stage stage : this.plugin.getStages()) {
                    // ゲームステータス取得
                    String status = stage.getReception().map(Reception::getState)
                            .map(Reception.State::toGameState)
                            .map(s -> s == Game.State.PREPARATION ? "&6受付中" : "&c開始中").orElse("&7待機中");

                    String s = "&6" + stage.getName() + "&b: 状態=&f" + status;
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
            String status = stage.getReception().map(Reception::getState).map(Reception.State::toGameState)
                    .map(s -> s == Game.State.PREPARATION ? "&6受付中" : "&c開始中").orElse("&7待機中");

            String s1 = "&6 " + stage.getName()
                        + "&b: 状態=&f" + status
                        + "&b 制限時間=&6" + Actions.getTimeString(stage.getGameTimeInSec());
            String s2 = "&b 製作者=&6" + stage.getAuthor()
                        + "&b 説明=&6" + stage.getDescription();
            String s7 = "&b 参加料=&6" + Actions.formatMoney(stage.getEntryFee())
                    + "&b 賞金=&6" + Actions.formatMoney(stage.getPrize());

            // メッセージ送信
            Actions.message(sender, s1);
            Actions.message(sender, s2);
            Actions.message(sender, s7);

            Actions.message(sender, "&a ================================================");
        }
    }
}
