/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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

import syam.flaggame.FlagGame;
import syam.flaggame.util.Actions;

public class HelpCommand extends BaseCommand {
    
    public HelpCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "help";
        argLength = 0;
        usage = "<- show command help";
    }

    @Override
    public void execute() {
        Actions.message(sender, "&c===================================");
        Actions.message(sender, "&bFlagGame Plugin version &3" + plugin.getDescription().getVersion() + " &bby "+String.join(", ", plugin.getDescription().getAuthors()));
        Actions.message(sender, " &b<>&f = required, &b[]&f = optional");
        // 全コマンドをループで表示
        for (BaseCommand cmd : this.plugin.getCommands()) {
            cmd.sender = this.sender;
            if (cmd.permission()) {
                Actions.message(sender, "&8-&7 /" + command + " &c" + cmd.name + " &7" + cmd.usage);
            }
        }
        Actions.message(sender, "&c===================================");
    }

    @Override
    public boolean permission() {
        return true;
    }
}
