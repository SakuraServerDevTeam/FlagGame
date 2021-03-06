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
import jp.llv.flaggame.database.DatabaseException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.api.queue.Queueable;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.stage.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class StageDeleteCommand extends BaseCommand {

    public StageDeleteCommand(FlagGameAPI api) {
        super(
                api,
                true,
                1,
                "<stage> <- delete a specified stage",
                Perms.STAGE_DELETE,
                "delete",
                "del"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        this.api.getStages().getStage(args.get(0))
                .orElseThrow(() -> new CommandException("&cその名前のステージは存在しません！"));

        // confirmキュー追加
        api.getConfirmQueue().addQueue(player, new QueuedStageDeletion(), args, 10);
        Actions.message(player, "&dステージ'&7" + args.get(0) + "&d'を削除しようとしています！");
        Actions.message(player, "&d続行するには &a/flag confirm &dコマンドを入力してください！");
        Actions.message(player, "&a/flag confirm &dコマンドは10秒間のみ有効です。");
    }

    private class QueuedStageDeletion implements Queueable {

        @Override
        public void executeQueue(List<String> args, CommandSender sender, Player player) throws FlagGameException {
            if (args.size() <= 1) {
                Actions.message(player, "&cステージ名が不正です");
                return;
            }
            Stage stage = api.getStages().getStage(args.get(0))
                    .orElseThrow(() -> new CommandException("&cその名前のステージは存在しません！"));
            stage.reserve(null);
            api.getDatabase()
                    .orElseThrow(() -> new CommandException("&cデータベースへの接続に失敗しました！"))
                    .deleteStage(stage, result -> {
                        try {
                            result.test();
                            api.getStages().removeStage(stage);
                            Actions.message(player, "&aステージ'" + args.get(0) + "'を削除しました！");
                        } catch (DatabaseException ex) {
                            Actions.message(player, "&cステージ'" + args.get(0) + "'の削除に失敗しました！");
                            api.getLogger().warn("Failed to delete stage", ex);
                        }
                    });
        }

    }
}
