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
package syam.flaggame.command.player.nick;

import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.NickPosition;
import jp.llv.flaggame.util.OnelineBuilder;
import org.bukkit.command.CommandSender;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class NickLockCommand extends NickCommand {

    public NickLockCommand(FlagGameAPI api) {
        super(
                api,
                "<player> <color|adj|noun> <nick> <- lock specified player's nick",
                Perms.NICK_LOCK_SELF, Perms.NICK_LOCK_OTHER,
                "lock"
        );
    }

    @Override
    void execute(CommandSender sender, GamePlayer target, NickPosition index, String nick) throws FlagGameException {
        if (!target.getAccount().getUnlockedNicks(index).contains(nick)) {
            throw new CommandException("&cそのニックネームはアンロックされていません！");
        }
        
        target.getAccount().lockNick(index, nick);
        OnelineBuilder.newBuilder()
                .info("ニックネーム").value(target.getNickname())
                .info("をロックしました！").sendTo(sender);
    }
    
}
