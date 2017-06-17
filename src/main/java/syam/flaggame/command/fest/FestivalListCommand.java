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

import java.util.Collection;
import java.util.List;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import jp.llv.flaggame.util.DashboardBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalListCommand extends BaseCommand {
    
    public FestivalListCommand(FlagGameAPI api) {
        super(
                api,
                true,
                0, 
                "<- show a list of festivals", 
                Perms.FESTIVAL_LIST,
                "list"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        Collection<FestivalSchedule> festivals = api.getFestivals().getFestivals().values();
        DashboardBuilder.newBuilder("Festivals", festivals.size())
                .appendList(festivals, (d, festival) -> {
                    d.key(festival.getName());
                    d.buttonRun("show").append("festival dashboard").append(festival.getName()).create()
                            .buttonRun("select").append("festival select").append(festival.getName()).create()
                            .buttonRun("delete").append("festival delete").append(festival.getName()).create();
                }).buttonSuggest("create").append("festival create").create()
                .sendTo(sender);
    }
    
}
