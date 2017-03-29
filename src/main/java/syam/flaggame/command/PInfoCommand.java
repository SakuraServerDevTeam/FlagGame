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

import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class PInfoCommand extends BaseCommand {

    public PInfoCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = true;
        name = "pinfo";
        argLength = 0;
        usage = " <- show your internal information";
    }

    @Override
    public void execute() throws CommandException {
        GamePlayer gp = this.plugin.getPlayers().getPlayer(player);
        gp.sendMessage("&2あなたは現在ゲーム&6"+gp.getEntry().map(e -> e.getName()+"&2("+e.getName()+")に参加しています。").orElse("&2に参加していません"));
    }

    @Override
    public boolean permission() {
        return Perms.PINFO.has(sender);
    }
    
}
