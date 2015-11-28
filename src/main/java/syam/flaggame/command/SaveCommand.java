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
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

public class SaveCommand extends BaseCommand {

    public SaveCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "save";
        argLength = 0;
        usage = "<- save map data";
    }

    @Override
    public void execute() {
        // データ保存
        plugin.getFileManager().saveStages();

        Actions.message(sender, "&aStages Saved!");
    }

    @Override
    public boolean permission() {
        return Perms.SAVE.has(sender);
    }
}
