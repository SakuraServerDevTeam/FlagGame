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

import java.util.Collection;
import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.trophy.Trophy;
import jp.llv.flaggame.util.DashboardBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class TrophyListCommand extends BaseCommand {

    public TrophyListCommand(FlagGameAPI api) {
        super(
                api,
                false,
                0,
                "<- list trophies",
                Perms.TROPHY_LIST,
                "list",
                "l"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Collection<Trophy> trophies = api.getTrophies().getTrophies().values();
        DashboardBuilder.newBuilder("Trophies", trophies.size())
                .appendList(trophies, (d, trophy) -> {
                    d.key(trophy.getName()).value(trophy.getType());
                    d.buttonRun("show").append("trophy dashboard").append(trophy.getName()).create()
                            .buttonRun("select").append("trophy select").append(trophy.getName()).create()
                            .buttonRun("delete").append("trophy delete").append(trophy.getName()).create();
                }).buttonSuggest("create").append("trophy create").create()
                .sendTo(sender);
    }
    
}
