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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jp.llv.flaggame.reception.GameReception;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

public class ReadyCommand extends BaseCommand {

    public ReadyCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "ready";
        argLength = 1;
        usage = "<reception-type> [optional args...] <- ready game";
    }

    @Override
    public void execute() throws CommandException {
        if (args.size() < 1) {
            throw new CommandException("&c募集方法を入力してください!");
        }
        List<String> readyArgs = new ArrayList<>(args);
        readyArgs.remove(0);

        GameReception reception;
        try {
            reception = this.plugin.getReceptions().newReception(args.get(0), readyArgs);
        } catch (IllegalArgumentException ex) {
            throw new CommandException("&c"+ex.getMessage(), ex);
        }
        reception.open(Collections.EMPTY_LIST);
        Actions.sendPrefixedMessage(sender, "&a受付'" + reception.getID() + "'の募集が開始されました");
    }

    @Override
    public boolean permission() {
        return Perms.READY.has(sender);
    }
}
