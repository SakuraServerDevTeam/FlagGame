/*
 * Copyright (C) 2016 toyblocks
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
import syam.flaggame.exception.CommandException;

/**
 *
 * @author toyblocks
 */
public class StageConfigCommand extends BaseCommand {

    public StageConfigCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "scfg";
        argLength = 1;
        usage = "<stage> [key [value]...]";
    }

    @Override
    public void execute() throws CommandException {
        
    }

    @Override
    public boolean permission() {
        return true;
    }
    
}
