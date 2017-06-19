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
package syam.flaggame.command.fest.set;

import java.util.Collection;
import java.util.List;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import jp.llv.flaggame.util.FlagTabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public abstract class FestivalSetCommand extends BaseCommand {

    public FestivalSetCommand(FlagGameAPI api, int argLength, String usage, Perms permission, FlagTabCompleter completer, String name, String... aliases) {
        super(api, true, argLength, usage, permission, completer, name, aliases);
    }

    @Override
    protected final void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        this.execute(
                args,
                api.getPlayers().getPlayer(player)
                .getSetupSession().map(s -> s.getSelected(FestivalSchedule.class))
                .orElseThrow(() -> new CommandException("&cフェスを選択してください！")),
                player
        );
    }

    protected abstract void execute(List<String> args, FestivalSchedule fest, Player sender) throws FlagGameException;

    @Override
    protected final Collection<String> complete(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        return this.complete(
                args,
                api.getPlayers().getPlayer(player)
                .getSetupSession().map(s -> s.getSelected(FestivalSchedule.class))
                .orElseThrow(() -> new FlagGameException()),
                player
        );
    }

    protected Collection<String> complete(List<String> args, FestivalSchedule fest, Player sender) throws FlagGameException {
        return super.complete(args, sender, sender);
    }

}
