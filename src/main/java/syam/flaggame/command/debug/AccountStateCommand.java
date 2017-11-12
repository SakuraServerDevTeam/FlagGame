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
package syam.flaggame.command.debug;

import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.util.FlagTabCompleter;
import jp.llv.flaggame.util.OnelineBuilder;
import jp.llv.flaggame.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class AccountStateCommand extends BaseCommand {

    public AccountStateCommand(FlagGameAPI api) {
        super(
                api,
                false,
                0,
                "[player] <- show account loading state",
                Perms.DEBUG,
                FlagTabCompleter.builder()
                .forArg(0).suggestPlayers().create(),
                "account state"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer target;
        if (args.isEmpty()) {
            target = api.getPlayers().getPlayer(player);
        } else {
            target = api.getPlayers().getPlayer(args.get(0));
        }
        if (target == null) {
            throw new CommandException("&c対象のオンラインプレイヤーを指定してください！");
        }
        OnelineBuilder.newBuilder()
                .info("プレイヤー").value(target.getName())
                .info("のアカウントは").value(StringUtil.capitalize(target.getAccountState().toString().toLowerCase()))
                .info("です！").sendTo(sender);
    }
    
}
