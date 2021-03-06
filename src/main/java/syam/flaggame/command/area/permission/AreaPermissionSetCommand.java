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

import java.util.List;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.stage.permission.GamePermission;
import jp.llv.flaggame.api.stage.permission.GamePermissionState;
import jp.llv.flaggame.api.reception.TeamColor;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.area.AreaCommand;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.stage.area.StageAreaInfo;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class AreaPermissionSetCommand extends AreaCommand {

    public AreaPermissionSetCommand(FlagGameAPI api) {
        super(
                api,
                3,
                "<id> <permission> <teamcolor> <state> <- load region",
                Perms.AREA_PERMISSION_SET,
                "set"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        String id = args.get(0);
        StageAreaInfo info = stage.getAreas().getAreaInfo(id);
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }

        GamePermission permission;
        try {
            permission = GamePermission.of(args.get(1));
        } catch (IllegalArgumentException ex) {
            String values = Arrays.stream(GamePermissionState.values()).map(e -> e.toString().toLowerCase()).collect(Collectors.joining("/"));
            throw new CommandException("&cその権限は存在しません！\n&c" + values, ex);
        }

        TeamColor target;
        try {
            target = TeamColor.of(args.get(2));
        } catch (IllegalArgumentException ex) {
            throw new CommandException("&cそのチーム色は存在しません！", ex);
        }
        GamePermissionState state;
        try {
            state = GamePermissionState.of(args.get(3));
        } catch (IllegalArgumentException ex) {
            String values = Arrays.stream(GamePermissionState.values()).map(e -> e.toString().toLowerCase()).collect(Collectors.joining("/"));
            throw new CommandException("&cその状態は存在しません！\n&c" + values, ex);
        }
        info.getPermission(permission).setState(target, state);
        sendMessage(player, "&aステージ'&6" + stage.getName() + "&a'のエリア'&6" + id + "&a'での権限'&6" + permission.toString() + "&a'を状態'" + state.format() + "&a'に変更しました！");
    }

}
