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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalSaveCommand extends BaseCommand {

    public FestivalSaveCommand(FlagGameAPI api) {
        super(
                api,
                true,
                0,
                "[festival] <- save the festival",
                Perms.FESTIVAL_SAVE,
                "save"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        FestivalSchedule festival;
        if (args.size() < 1) {
            if (player == null) {
                throw new CommandException("&cフェスを指定してください！");
            } else {
                GamePlayer gplayer = api.getPlayers().getPlayer(player);
                festival = gplayer.getSetupSession()
                        .orElseThrow(() -> new CommandException("&cフェスを指定してください！"))
                        .getSelected(FestivalSchedule.class);
                if (festival == null) {
                    throw new CommandException("&cあなたはフェスを選択していません！");
                }
                gplayer.destroySetupSession();
                gplayer.sendMessage("&aフェスの選択を解除しました！");
            }
        } else {
            festival = api.getFestivals().getFestival(args.get(0))
                    .orElseThrow(() -> new CommandException("&cフェスが見つかりません！"));
        }
        api.getDatabase()
                .orElseThrow(() -> new CommandException("&cデータベースへの接続に失敗しました！"))
                .saveFestival(festival, result -> {
            try {
                result.get();
                Actions.sendPrefixedMessage(sender, "&aフェスを保存しました！");
            } catch (DatabaseException ex) {
                Actions.sendPrefixedMessage(sender, "&cフェスの保存に失敗しました！");
                api.getLogger().warn("Failed to save a festival", ex);
            }
        });
    }
}
