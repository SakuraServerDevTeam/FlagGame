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
package syam.flaggame.command.trophy;

import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.queue.Queueable;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.util.FlagTabCompleter;
import jp.llv.flaggame.util.OnelineBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;
import jp.llv.flaggame.api.trophy.Trophy;

/**
 *
 * @author toyblocks
 */
public class TrophyDeleteCommand extends BaseCommand {

    public TrophyDeleteCommand(FlagGameAPI api) {
        super(api,
                true,
                1,
                "<name> <- delete a trophy",
                Perms.TROPHY_DELETE,
                FlagTabCompleter.builder()
                        .forArg(0).suggestList((a, s, r) -> a.getTrophies().getTrophy().keySet())
                        .create(),
                "delete",
                "del"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        this.api.getTrophies().getTrophy(args.get(0))
                .orElseThrow(() -> new CommandException("&cその名前のトロフィーは存在しません！"));

        // confirmキュー追加
        api.getConfirmQueue().addQueue(player, new QueuedTrophyDeletion(), args, 10);
        OnelineBuilder.newBuilder().warn("トロフィー").value(args.get(0))
                .warn("を削除しようとしています！");
        OnelineBuilder.newBuilder().warn("よろしければ10秒以内に")
                .buttonRun("続行").append("confirm").create()
                .warn("をクリックして下さい！").sendTo(player);
    }

    private class QueuedTrophyDeletion implements Queueable {

        @Override
        public void executeQueue(List<String> args, CommandSender sender, Player player) throws FlagGameException {
            if (args.size() <= 1) {
                Actions.message(player, "&cトロフィー名が不正です");
                return;
            }
            Trophy trophy = api.getTrophies().getTrophy(args.get(0))
                    .orElseThrow(() -> new CommandException("&cその名前のトロフィーは存在しません！"));
            trophy.reserve(null);
            api.getDatabase()
                    .orElseThrow(() -> new CommandException("&cデータベースへの接続に失敗しました！"))
                    .deleteTrophy(trophy, result -> {
                        try {
                            result.test();
                            api.getTrophies().removeTrophy(trophy);
                            Actions.message(player, "&aトロフィー'" + args.get(0) + "'を削除しました！");
                        } catch (DatabaseException ex) {
                            Actions.message(player, "&cトロフィー'" + args.get(0) + "'の削除に失敗しました！");
                            api.getLogger().warn("Failed to delete a trophy", ex);
                        }
                    });
        }

    }
    
}
