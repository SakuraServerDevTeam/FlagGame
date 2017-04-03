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
import syam.flaggame.FlagGame;
import syam.flaggame.command.dashboard.DashboardBuilder;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author SakuraServerDev
 */
public class AreaDashboardCommand extends AreaCommand {

    public AreaDashboardCommand(FlagGame plugin) {
        super(
                plugin,
                1,
                "<id> <- show information about areas",
                Perms.STAGE_CONFIG_CHECK,
                "area dashboard",
                "area d"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        String id = args.get(0);
        Cuboid area = stage.getAreas().getArea(id);
        AreaInfo info = stage.getAreas().getAreaInfo(id);
        DashboardBuilder.newBuilder("Area Overview", stage.getName())
                .key("ID").value(id)
                .buttonRun("show").append("area dashboard").append(id).create()
                .buttonRun("select").append("area select").append(id).create()
                .buttonRun("init").append("area init").append(id).create()
                .buttonRun("set").append("area set").append(id).create()
                .buttonRun("delete").append("area delete").append(id).create().br()
                .key("Size").value(area.getArea()).br()
                .key("Rollbacks").value(info.getRollbacks().size())
                .buttonRun("list").append("area data list").append(id).create().br()
                .key("Messages").value(info.getMessages().size())
                .buttonRun("list").append("area message list").append(id).create().br()
                .buttonRun("permissions").append("area permission list").append(id).create()
                .sendTo(player);
    }

}
