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
import org.bukkit.permissions.Permissible;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Cuboid;
import syam.flaggame.util.WorldEditHandler;

/**
 *
 * @author toyblocks
 */
public class AreaSetCommand extends AreaCommand {

    public AreaSetCommand(FlagGame plugin) {
        super(
                plugin,
                1,
                "<id> <- set region",
                "area set",
                "aset"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        String id = args.get(0);
        Cuboid region;
        try {
            region = WorldEditHandler.getSelectedArea(player);
        } catch (IllegalStateException ex) {
            throw new CommandException("&c" + ex.getMessage());
        }
        stage.getAreas().setArea(id, region);
        sendMessage(player, "&a'&6" + stage.getName() + "&a'の'&6" + id + "&a'エリアを設定しました！");
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.STAGE_CONFIG_SET.has(target);
    }

}
