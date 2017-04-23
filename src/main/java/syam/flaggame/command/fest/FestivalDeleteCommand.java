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
package syam.flaggame.command.fest;

import java.util.List;
import java.util.logging.Level;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import jp.llv.flaggame.util.FlagTabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.permission.Perms;
import syam.flaggame.queue.Queueable;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalDeleteCommand extends BaseCommand {

    public FestivalDeleteCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                0,
                "<festival> <- delete a festival",
                Perms.FESTIVAL_DELETE,
                FlagTabCompleter.builder()
                .forArg(1).suggestList((p, s, m) -> p.getFestivals().getFestivals().keySet())
                .create(),
                "delete",
                "del"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        plugin.getFestivals().getFestival(args.get(0))
                .orElseThrow(() -> new CommandException("&cその名前のフェスは存在しません！"));
        plugin.getConfirmQueue().addQueue(player, new QueuedFestivalDeletion(), args, 10);
        Actions.message(player, "&フェス'&7" + args.get(0) + "&d'を削除しようとしています！");
        Actions.message(player, "&d続行するには &a/flag confirm &dコマンドを入力してください！");
        Actions.message(player, "&a/flag confirm &dコマンドは10秒間のみ有効です。");
    }

    private class QueuedFestivalDeletion implements Queueable {

        @Override
        public void executeQueue(List<String> args, CommandSender sender, Player player) throws FlagGameException {
            FestivalSchedule festival = plugin.getFestivals().getFestival(args.get(0))
                .orElseThrow(() -> new CommandException("&cその名前のフェスは存在しません！"));
            festival.reserve(null);
            plugin.getDatabases()
                    .orElseThrow(() -> new CommandException("&cデータベースへの接続に失敗しました！"))
                    .deleteFestival(festival, result -> {
                        try {
                            result.test();
                            plugin.getFestivals().removeFestival(festival);
                            Actions.message(player, "&aフェス'" + args.get(0) + "'を削除しました！");
                        } catch (DatabaseException ex) {
                            Actions.message(player, "&aフェス'" + args.get(0) + "'の削除に失敗しました！");
                            plugin.getLogger().log(Level.WARNING, "Failed to delete festival", ex);
                        }
                    });
        }

    }

}
