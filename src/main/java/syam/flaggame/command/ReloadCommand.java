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
import java.util.logging.Level;
import syam.flaggame.FlagGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                0,
                "<- reload config.yml",
                Perms.RELOAD,
                "reload"
        );
    
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) {
        try {
            plugin.getConfigs().loadConfig();
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "an error occured while trying to load the config file.", ex);
            return;
        }

        Actions.message(sender, "&aConfiguration reloaded!");
    }
    
}
