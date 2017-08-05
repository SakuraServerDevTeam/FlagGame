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
import org.bukkit.permissions.Permissible;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagCommandRegistry;
import syam.flaggame.util.Actions;

public class HelpCommand extends BaseCommand {

    public HelpCommand(FlagGameAPI api) {
        super(
                api,
                false,
                0,
                "<- show command help matches specified arguments",
                "help"
        );
    }

    @Override
    public void execute(List<String> args, String label, CommandSender sender, Player player) {
        FlagCommandRegistry registry;
        String prefix;
        if (label.toLowerCase().endsWith("help")) {
            registry = FlagCommandRegistry.GENERAL;
            prefix = label.substring(0, label.indexOf(" "));
        } else {
            registry = FlagCommandRegistry.getCategory(label);
            prefix = label;
        }
        Actions.message(sender, "&c===================================");
        Actions.message(sender, "&bFlagGame version &3" + api.getPlugin().getDescription().getVersion() + " &bby " + String.join(", ", api.getPlugin().getDescription().getAuthors()));
        Actions.message(sender, " &b<>&f = required, &b[]&f = optional");
        if (registry == null) {
            Actions.message(sender, "&cNo command starts with " + prefix);
        } else {
            // 全コマンドをループで表示
            for (BaseCommand command : registry.getCommands()) {
                command.sendUsage(sender, prefix);
            }
            for (FlagCommandRegistry subcategory : registry.getSubcategories()) {
                subcategory.sendUsage(sender, prefix);
            }
        }
        Actions.message(sender, "&c===================================");
    }

}
