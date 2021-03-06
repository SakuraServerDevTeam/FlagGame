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
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.util.DashboardBuilder;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.stage.area.StageAreaInfo;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author SakuraServerDev
 */
public class AreaDashboardCommand extends AreaCommand {

    public AreaDashboardCommand(FlagGameAPI api) {
        super(
                api,
                1,
                "<id> <- show information about areas",
                Perms.AREA_DASHBOARD,
                "dashboard", "d"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        String id = args.get(0);
        Cuboid area = stage.getAreas().getArea(id);
        StageAreaInfo info = stage.getAreas().getAreaInfo(id);
        if (area == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
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
