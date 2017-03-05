/* 
 * Copyright (C) 2017 Syamn, SakuraServerDev
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

public class ReloadCommand extends BaseCommand {

    public ReloadCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "reload";
        argLength = 0;
        usage = "<- reload config.yml";
    }

    @Override
    public void execute() {
        try {
            plugin.getConfigs().loadConfig(false);
        } catch (Exception ex) {
            log.warning(logPrefix + "an error occured while trying to load the config file.");
            ex.printStackTrace();
            return;
        }

        // 権限管理プラグイン再設定
        Perms.setupPermissionHandler();

        Actions.message(sender, "&aConfiguration reloaded!");
    }

    @Override
    public boolean permission() {
        return Perms.RELOAD.has(sender);
    }
}
