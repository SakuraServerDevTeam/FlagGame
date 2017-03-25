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

import jp.llv.flaggame.profile.record.StageRateRecord;
import jp.llv.flaggame.reception.GameReception;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author SakuraServerDev
 */
public class RateCommand extends BaseCommand {
    
    public RateCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = true;
        name = "rate";
        argLength = 1;
        usage = "<rate> <- rate the stage";
    }

    @Override
    public void execute() throws CommandException {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
        if (!gplayer.getEntry().isPresent()) {
            throw new CommandException("&c評価対象に参加していません！");
        }
        GameReception reception = gplayer.getEntry().get();
        if (reception.getState() != GameReception.State.FINISHED) {
            throw new CommandException("&c評価対象が評価段階ではありません！");
        }
        int rate = -1;
        try {
            rate = Integer.parseInt(args.get(0));
        } catch(NumberFormatException ex) {
        }
        if (rate < 0 || 5 < rate) {
            throw new CommandException("&c0-5の整数値で評価してください！");
        }
        reception.getRecordStream().push(new StageRateRecord(reception.getID(), player, rate));
    }

    @Override
    public boolean permission() {
        return true;
    }
    
}
