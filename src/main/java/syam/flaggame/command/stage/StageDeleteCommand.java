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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.command.queue.Queueable;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class StageDeleteCommand extends BaseCommand {

    public StageDeleteCommand(FlagGame plugin) {
        super(
                plugin,
                true,
                1,
                "<stage> <- management stages",
                Perms.DELETE,
                "stage delete"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Stage stage = this.plugin.getStages().getStage(args.get(0))
                .orElseThrow(() -> new CommandException("&cその名前のステージは存在しません！"));

        if (stage.isReserved()) {
            throw new CommandException("&cそのステージは現在受付中または開始中のため削除できません");
        }

        // confirmキュー追加
        plugin.getConfirmQueue().addQueue(player, new QueuedStageDeletion(), args, 10);
        Actions.message(player, "&dステージ'&7" + args.get(0) + "&d'を削除しようとしています！");
        Actions.message(player, "&d続行するには &a/flag confirm &dコマンドを入力してください！");
        Actions.message(player, "&a/flag confirm &dコマンドは10秒間のみ有効です。");
    }

    private class QueuedStageDeletion implements Queueable {

        @Override
        public void executeQueue(List<String> args, CommandSender sender, Player player) throws CommandException {
            if (args.size() <= 1) {
                Actions.message(player, "&cステージ名が不正です");
                return;
            }
            Stage stage = plugin.getStages().getStage(args.get(0))
                    .orElseThrow(() -> new CommandException("&cその名前のステージは存在しません！"));

            if (stage.isReserved()) {
                Actions.message(player, "&cそのステージは現在受付中または開始中のため削除できません");
                return;
            }

            // ゲームリストから削除
            plugin.getStages().removeStage(args.get(0));
            Actions.message(player, "&aステージ'" + args.get(0) + "'を削除しました！");
            plugin.getDynmap().updateRegions();
        }

    }
}
