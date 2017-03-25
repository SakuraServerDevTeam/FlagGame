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

import jp.llv.flaggame.reception.GameReception;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class CloseCommand extends BaseCommand {

    public CloseCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "close";
        argLength = 1;
        usage = "<reception> [reason] <- close the reception";
    }

    @Override
    public void execute() throws CommandException {
        GameReception reception = this.plugin.getReceptions().getReception(this.args.get(0))
                .orElseThrow(() -> new CommandException("&c受付'" + args.get(0) + "'が見つかりません！"));
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        
        if (reception.getState()==GameReception.State.CLOSED) {
            throw new CommandException("&cその受付は既に破棄されています!");
        }
        reception.close(args.size() < 2 ? sender.getName()+"Closed":args.get(1));
        gPlayer.sendMessage("&a成功しました!");
    }

    @Override
    public boolean permission() {
        return Perms.CLOSE.has(sender);
    }
    
}
