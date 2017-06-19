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
package syam.flaggame.command.game;

import java.util.ArrayList;
import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import syam.flaggame.permission.Perms;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.util.OptionSet;

public class GameReadyCommand extends BaseCommand {

    public GameReadyCommand(FlagGameAPI api) {
        super(
                api,
                false,
                0,
                "[options...] <- ready game",
                Perms.GAME_READY,
                "ready"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        if (args.size() < 1) {
            throw new CommandException("&c募集方法を入力してください!");
        }
        List<String> readyArgs = new ArrayList<>(args);
        readyArgs.remove(0);

        OptionSet options = new OptionSet(args);
        Reception reception;
        try {
            reception = options.isPresent("r")
                    ? api.getReceptions().newReception(options.getString("r"))
                    : api.getReceptions().newReception();
        } catch (IllegalArgumentException ex) {
            throw new CommandException("&c" + ex.getMessage(), ex);
        }

        try {
            reception.open(options);
        } catch (CommandException ex) {
            reception.close("Failed to initialize");
            throw ex;
        }
    }
}
