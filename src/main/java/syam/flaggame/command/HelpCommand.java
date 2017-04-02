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
import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.util.Actions;

public class HelpCommand extends BaseCommand {

    public HelpCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                0,
                "<- show command help",
                "help"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) {
        Actions.message(sender, "&c===================================");
        Actions.message(sender, "&bFlagGame Plugin version &3" + plugin.getDescription().getVersion() + " &bby " + String.join(", ", plugin.getDescription().getAuthors()));
        Actions.message(sender, " &b<>&f = required, &b[]&f = optional");
        // 全コマンドをループで表示
        for (BaseCommand cmd : this.plugin.getCommands()) {
            if (cmd.hasPermission(sender)) {
                Actions.message(sender, "&8-&7 " + BaseCommand.COMMAND_PREFIX + "&c" + cmd.getName() + " &7" + cmd.getUsage());
            }
        }
        Actions.message(sender, "&c===================================");
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return true;
    }
}
