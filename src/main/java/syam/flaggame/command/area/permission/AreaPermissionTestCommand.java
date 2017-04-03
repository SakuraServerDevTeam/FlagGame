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
import jp.llv.flaggame.game.permission.GamePermission;
import jp.llv.flaggame.game.permission.GamePermissionState;
import jp.llv.flaggame.reception.TeamColor;
import org.bukkit.Location;
import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;
import syam.flaggame.command.area.AreaCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class AreaPermissionTestCommand extends AreaCommand {

    public AreaPermissionTestCommand(FlagGame plugin) {
        super(
                plugin,
                1,
                "<permission> [color] <- select region",
                "area test",
                "at"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        GamePermission permission;
        try {
            permission = GamePermission.of(args.get(0));
        } catch (IllegalArgumentException ex) {
            String values = Arrays.stream(GamePermission.values()).map(e -> e.toString().toLowerCase()).collect(Collectors.joining("/"));
            throw new CommandException("&cその権限は存在しません！\n&c" + values, ex);
        }
        TeamColor color;
        if (args.size() == 1) {
            color = TeamColor.WHITE;
        } else {
            try {
                color = TeamColor.of(args.get(1));
            } catch (IllegalArgumentException ex) {
                throw new CommandException("&cそのチーム色は存在しません！", ex);
            }
        }
        Location loc = player.getLocation();
        String cause = "定義されていませんでした";
        GamePermissionState state = GamePermissionState.DEFAULT;
        Actions.message(player, "&a =================&b AreaTest &a=================");
        for (String name : stage.getAreas().getAreas()) {
            if (!stage.getAreas().getArea(name).contains(loc)) {
                continue;
            }
            AreaInfo info = stage.getAreas().getAreaInfo(name);
            GamePermissionState s = info.getPermission(permission).getState(color);
            if (s.isForceful()) {
                cause = "&a'&6" + name + "&a'の定義が適用されています";
                state = s;
            }
            Actions.message(player, "&a'&6" + name + "&a': " + state.format());
        }
        Actions.message(player, "&a'&6"
                                + stage.getName() + "&a'の"
                                + color.getRichName() + "&aのここでの権限'&6"
                                + permission.toString().toLowerCase() + "&a'は'"
                                + state.format() + "&a'に設定されています。");
        Actions.message(player, "&aこの権限は" + cause);
        Actions.message(player, "&a ============================================");
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.STAGE_CONFIG_CHECK.has(target);
    }

}