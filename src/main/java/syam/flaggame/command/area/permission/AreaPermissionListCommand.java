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
package syam.flaggame.command.area.permission;

import java.util.Arrays;
import java.util.List;
import jp.llv.flaggame.api.stage.permission.GamePermission;
import jp.llv.flaggame.util.StringUtil;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.area.AreaCommand;
import jp.llv.flaggame.util.DashboardBuilder;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.stage.area.StageAreaInfo;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class AreaPermissionListCommand extends AreaCommand {

    public AreaPermissionListCommand(FlagGameAPI api) {
        super(
                api,
                1,
                "<id> <- show a list of permissions",
                Perms.AREA_PERMISSION_LIST,
                "list",
                "l"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        String id = args.get(0);
        StageAreaInfo info = stage.getAreas().getAreaInfo(id);
        DashboardBuilder.newBuilder("Area Permissions")
                .key("ID").value(id)
                .appendList(Arrays.asList(GamePermission.values()), (d, perm) -> {
                    d.key(StringUtil.capitalize(perm.name()))
                            .value(info.getPermission(perm).isAllDefault() ? "modified" : "default")
                            .buttonRun("show").append("area permission dashboard").append(perm).create();
                }).sendTo(player);
    }

}
