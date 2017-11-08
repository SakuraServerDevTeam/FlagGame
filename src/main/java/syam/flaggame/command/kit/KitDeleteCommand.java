/*
 * Copyright (C) 2017 toyblocks
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
package syam.flaggame.command.kit;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.kit.Kit;
import jp.llv.flaggame.api.queue.Queueable;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.util.OnelineBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class KitDeleteCommand extends BaseCommand {

    public KitDeleteCommand(FlagGameAPI api) {
        super(
                api,
                true,
                1,
                "<kit> <- delete a specified kit",
                Perms.KIT_DELETE,
                "delete",
                "del"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        api.getKits().getKit(args.get(0))
                .orElseThrow(() -> new CommandException("&cキット'" + args.get(0) + "'が見つかりません！"));

        // confirmキュー追加
        api.getConfirmQueue().addQueue(player, new QueuedKitDeletion(), args, 10);
        OnelineBuilder.newBuilder().warn("キット").value(args.get(0))
                .warn("を削除しようとしています！");
        OnelineBuilder.newBuilder().warn("よろしければ10秒以内に")
                .buttonRun("続行").append("confirm").create()
                .warn("をクリックして下さい！").sendTo(player);
    }

    @Override
    protected Collection<String> complete(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        return args.isEmpty() ? null : api.getKits().getKits().keySet().stream()
                .filter(s -> s.toLowerCase().startsWith(args.get(0).toLowerCase()))
                .collect(Collectors.toList());
    }

    private class QueuedKitDeletion implements Queueable {

        @Override
        public void executeQueue(List<String> args, CommandSender sender, Player player) throws FlagGameException {
            Kit kit = api.getKits().getKit(args.get(0))
                    .orElseThrow(() -> new CommandException("&cキット'" + args.get(0) + "'が見つかりません！"));
            api.getDatabase()
                    .orElseThrow(() -> new CommandException("&cデータベースへの接続に失敗しました！"))
                    .deleteKit(kit, result -> {
                        try {
                            result.test();
                            api.getKits().removeKit(kit);
                            OnelineBuilder.newBuilder()
                                    .info("キット").value(args.get(0))
                                    .info("を削除しました！").sendTo(player);
                        } catch (DatabaseException ex) {
                            OnelineBuilder.newBuilder()
                                    .warn("キット").value(args.get(0))
                                    .warn("の削除に失敗しました！").sendTo(player);
                            api.getLogger().warn("Failed to delete stage", ex);
                        }
                    });
        }

    }

}
