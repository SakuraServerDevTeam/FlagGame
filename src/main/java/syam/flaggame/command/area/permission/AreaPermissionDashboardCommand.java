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
import java.util.stream.Collectors;
import jp.llv.flaggame.game.permission.GamePermission;
import jp.llv.flaggame.game.permission.GamePermissionState;
import jp.llv.flaggame.game.permission.GamePermissionStateSet;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.util.StringUtil;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.area.AreaCommand;
import jp.llv.flaggame.util.DashboardBuilder;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class AreaPermissionDashboardCommand extends AreaCommand {

    public AreaPermissionDashboardCommand(FlagGame plugin) {
        super(
                plugin, 
                2,
                "<id> <permission> <- show information about permissions", 
                Perms.AREA_PERMISSION_DASHBOARD, 
                "dashboard",
                "d"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        String id = args.get(0);
        GamePermission perm;
        try {
            perm = GamePermission.of(args.get(1));
        } catch (IllegalArgumentException ex) {
            String values = Arrays.stream(GamePermission.values()).map(e -> e.toString().toLowerCase()).collect(Collectors.joining("/"));
            throw new CommandException("&cその権限は存在しません！\n&c" + values, ex);
        }
        GamePermissionStateSet states = stage.getAreas().getAreaInfo(id).getPermission(perm);
        DashboardBuilder.newBuilder("Area Permissions", StringUtil.capitalize(perm.name()))
                .appendList(Arrays.asList(TeamColor.values()), (d, color) -> {
                    GamePermissionState state = states.getState(color);
                    d.key(color.getBungeeChatColor(), StringUtil.capitalize(color.name()))
                            .text(state.getColor(), state.format())
                            .buttonRun("deny").append("area permission set").append(id).append(perm.name())
                            .append(color.name()).append(GamePermissionState.DENY.name()).create()
                            .buttonRun("default").append("area permission set").append(id).append(perm.name())
                            .append(color.name()).append(GamePermissionState.DEFAULT.name()).create()
                            .buttonRun("allow").append("area permission set").append(id).append(perm.name())
                            .append(color.name()).append(GamePermissionState.ALLOW.name()).create();
                            
                }).sendTo(player);
    }
    
}
