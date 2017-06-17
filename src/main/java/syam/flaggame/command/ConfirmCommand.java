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
package syam.flaggame.command;

import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;

/**
 * ConfirmCommand (ConfirmCommand.java)
 *
 * @author syam(syamn)
 */
public class ConfirmCommand extends BaseCommand {

    public ConfirmCommand(FlagGameAPI api) {
        super(
                api,
                false,
                0,
                "<- command confirm",
                "confirm"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        boolean ran = this.api.getConfirmQueue().confirmQueue(sender);
        if (!ran) {
            throw new CommandException("&cあなたの実行待ちコマンドはありません！");
        }
    }
    
}
