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
package syam.flaggame.command.fest.set;

import java.util.List;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import jp.llv.flaggame.util.FlagTabCompleter;
import jp.llv.flaggame.util.OnelineBuilder;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalSetEntryfeeCommand extends FestivalSetCommand {

    public FestivalSetEntryfeeCommand(FlagGame plugin) {
        super(
                plugin,
                1,
                "<entryfee> <- set entryfee",
                Perms.FESTIVAL_SET_ENTRYFEE,
                FlagTabCompleter.empty(),
                "entryfee"
        );
    }

    @Override
    protected void execute(List<String> args, FestivalSchedule fest, Player sender) throws FlagGameException {
        double entryFee;
        try {
            entryFee = Double.parseDouble(args.get(0));
        } catch (NumberFormatException ex) {
            throw new CommandException("&c数値が有効ではありません！", ex);
        }
        if (entryFee < 0) {
            throw new CommandException("&c値が不正です！負数は指定できません！");
        }
        fest.setEntryFee(entryFee);
        OnelineBuilder.newBuilder()
                .info("フェス").value(fest.getName())
                .info("の参加料は").value(Actions.formatMoney(entryFee))
                .info("に設定されました！")
                .sendTo(sender);
    }
    
}
