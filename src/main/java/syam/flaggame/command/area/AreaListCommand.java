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
package syam.flaggame.command.area;

import java.util.List;
import org.bukkit.entity.Player;
import java.util.Set;
import syam.flaggame.FlagGame;
import jp.llv.flaggame.util.DashboardBuilder;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author toyblocks
 */
public class AreaListCommand extends AreaCommand {

    public AreaListCommand(FlagGame plugin) {
        super(
                plugin,
                0,
                "<- show a list of areas",
                Perms.AREA_LIST,
                "list",
                "l"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        Set<String> areas = stage.getAreas().getAreas();
        DashboardBuilder.newBuilder("Areas", areas.size())
                .appendList(areas, (d, id) -> {
                    Cuboid area = stage.getAreas().getArea(id);
                    d.key(id).value("size").value(area.getArea())
                            .buttonRun("show").append("area dashboard").append(id).create()
                            .buttonRun("select").append("area select").append(id).create()
                            .buttonRun("init").append("area init").append(id).create()
                            .buttonRun("set").append("area set").append(id).create()
                            .buttonRun("delete").append("area delete").append(id).create();
                }).buttonSuggest("set new").append("area set").create()
                .sendTo(player);
    }

}
