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

import java.util.Arrays;
import java.util.stream.Collectors;
import jp.llv.flaggame.game.permission.GamePermission;
import jp.llv.flaggame.game.permission.GamePermissionState;
import jp.llv.flaggame.reception.TeamColor;
import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class AreaPermitCommand extends AreaCommand {

    public AreaPermitCommand(FlagGame plugin) {
        super(
                plugin,
                3,
                "<id> <permission> <teamcolor> [state] <- load region",
                "area permit"
        );
    }

    @Override
    public void execute(Stage stage) throws CommandException {
        String id = args.get(0);
        AreaInfo info = stage.getAreas().getAreaInfo(id);
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

        if (args.size() == 3) {
            GamePermissionState state = info.getPermission(permission).getState(target);
            sendMessage("&aステージ'&6" + stage.getName() + "&a'のエリア'&6" + id + "&a'での権限'&6" + permission.toString() + "&a'は状態'" + state.format() + "&a'です！");
        } else {
            GamePermissionState state;
            try {
                state = GamePermissionState.of(args.get(3));
            } catch (IllegalArgumentException ex) {
                String values = Arrays.stream(GamePermissionState.values()).map(e -> e.toString().toLowerCase()).collect(Collectors.joining("/"));
                throw new CommandException("&cその状態は存在しません！\n&c" + values, ex);
            }
            info.getPermission(permission).setState(target, state);
            sendMessage("&aステージ'&6" + stage.getName() + "&a'のエリア'&6" + id + "&a'での権限'&6" + permission.toString() + "&a'を状態'" + state.format() + "&a'に変更しました！");
        }
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.STAGE_CONFIG_SET.has(target);
    }

}
