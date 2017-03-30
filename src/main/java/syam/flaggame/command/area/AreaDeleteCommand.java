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

import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author toyblocks
 */
public class AreaDeleteCommand extends AreaCommand {

    public AreaDeleteCommand(FlagGame plugin) {
        super(
                plugin,
                1,
               "<id> <- delete region",
                "area delete",
                "ad"
        );
    }

    @Override
    public void execute(Stage stage) throws CommandException {
        String id = args.get(0);
        Cuboid area = stage.getAreas().getArea(id);
        if (area == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        stage.getAreas().removeArea(id);
        sendMessage("&aエリアを削除しました！");
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.STAGE_CONFIG_SET.has(target);
    }
    
}
