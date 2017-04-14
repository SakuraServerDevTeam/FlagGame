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
package syam.flaggame.command.fest;

import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalCommand extends BaseCommand {
    
    public FestivalCommand(FlagGame plugin, boolean bePlayer, int argLength, String usage, Perms permission, String name, String... aliases) {
        super(plugin, bePlayer, argLength, usage, permission, name, aliases);
    }
    
}
