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

import jp.llv.flaggame.reception.GameReception;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class ListCommand extends BaseCommand {

    public ListCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "list";
        argLength = 0;
        usage = " <- show a list of receptions";
    }
    
    @Override
    public void execute() throws CommandException {
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        
        if (this.plugin.getReceptions().getReceptions().isEmpty()) {
            gPlayer.sendMessage("&c現在有効な参加受付はありません");
            return;
        }
        for (GameReception r : this.plugin.getReceptions()) {
            gPlayer.sendMessage("&"+getColorCodeOf(r.getState())+r.getName()+"&e("+r.getID()+")");
        }
    }
    
    private static char getColorCodeOf(GameReception.State state) {
        switch(state) {
            case READY:
                return '7';
            case OPENED:
                return 'a';
            case STARTED:
                return '6';
            case FINISHED:
                return 'b';
            case CLOSED:
                return 'c';
            default:
                return 'f';
        }
    }

    @Override
    public boolean permission() {
        return Perms.LIST.has(sender);
    }
    
}
