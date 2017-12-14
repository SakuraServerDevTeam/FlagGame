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

import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.NickPosition;
import jp.llv.flaggame.util.FlagTabCompleter;
import jp.llv.flaggame.util.Parser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public abstract class NickCommand extends BaseCommand {

    private static final FlagTabCompleter COMPLETER = FlagTabCompleter.builder()
            .forArg(0).suggestStream(
            (api, a, b)  -> api.getServer().getOnlinePlayers().stream().map(p -> p.getName()))
            .forArg(1).suggestEnum(NickPosition.class)
            .create();
    
    private final Perms selfPerm;
    private final Perms otherPerm;

    public NickCommand(FlagGameAPI plugin, String usage, Perms selfPerm, Perms otherPerm, String name, String... aliases) {
        super(plugin, false, 3, usage, null, COMPLETER, name, aliases);
        this.selfPerm = selfPerm;
        this.otherPerm = otherPerm;
    }

    @Override
    protected final void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        if (sender instanceof Player && args.get(0).equals(sender.getName())) {
            selfPerm.requireTo(sender);
        } else {
            otherPerm.requireTo(sender);
        }
        GamePlayer target = api.getPlayers().getPlayer(args.get(0));
        if (target == null) {
            throw new CommandException("&cプレイヤーを指定してください！");
        }

        NickPosition position = Parser.asEnum(args.get(1), NickPosition.class);
        String nick = String.join(" ", args.subList(2, args.size()));
        this.execute(sender, target, position, nick);
    }
    
    /*package*/ abstract void execute(CommandSender sender, GamePlayer target, NickPosition position, String nick) throws FlagGameException;
    
}
