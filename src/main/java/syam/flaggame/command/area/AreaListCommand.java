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
import org.bukkit.permissions.Permissible;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

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
                "area list",
                "ali"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        Set<String> areas = stage.getAreas().getAreas();
        Actions.message(player, "&a ===============&b AreaList(" + areas.size() + ") &a===============");
        if (areas.isEmpty()) {
            Actions.message(player, " &7定義済みのエリアがありません");
        } else {
            for (String id : areas) {
                int size = stage.getAreas().getArea(id).getArea();
                Actions.message(player, "&6" + id + "&7(サイズ:" + size + ")");
            }
        }
        Actions.message(player, "&a ============================================");
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.STAGE_CONFIG_CHECK.has(target);
    }

}
