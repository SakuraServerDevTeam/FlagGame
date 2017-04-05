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
package syam.flaggame.command.area.data;

import java.util.List;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.area.AreaCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class AreaDataDeleteCommand extends AreaCommand {

    public AreaDataDeleteCommand(FlagGame plugin) {
        super(
                plugin,
                2, 
                "<id> <name>", 
                Perms.STAGE_CONFIG_SET, 
                "delete"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        String id = args.get(0);
        AreaInfo info = stage.getAreas().getAreaInfo(id);
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }

        String savename = args.get(1);
        AreaInfo.RollbackData data = info.getRollback(savename);
        if (data == null) {
            throw new CommandException("&cその名前のロールバックデータは存在しません！");
        }
        
        info.removeRollback(savename);
        sendMessage(player, "&aロールバックデータを削除しました！");
    }
    
}
