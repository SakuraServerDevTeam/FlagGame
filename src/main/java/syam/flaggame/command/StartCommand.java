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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import jp.llv.flaggame.reception.GameReception;
import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;

import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;

public class StartCommand extends BaseCommand {

    public StartCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                0,
                "[stage] <- start game",
                "start"
        );
    
    }

    @Override
    public void execute() throws CommandException {
        GameReception reception = null;
        List<String> startArgs;

        if (args.size() >= 1) {// 引数があれば指定したステージに参加
            reception = this.plugin.getReceptions().getReception(args.get(0))
                    .orElseThrow(() -> new CommandException("&cステージ'" + args.get(0) + "'が見つかりません"));
            if (reception.getState() != GameReception.State.OPENED) {
                throw new CommandException("&cそのゲームは受付中ではありません!");
            }
            startArgs = new ArrayList<>(args);
            startArgs.remove(0);
        } else {// 引数がなければ自動補完
            Collection<GameReception> openedReceptions = this.plugin.getReceptions()
                    .getReceptions(GameReception.State.OPENED);
            if (openedReceptions.size() <= 0) {
                throw new CommandException("&c現在、参加受付中のゲームはありません！");
            } else if (openedReceptions.size() >= 2) {
                throw new CommandException("&c複数のゲームが受付中です！参加するステージを指定してください!");
            } else {// 受付中が1つのみなら自動補完
                reception = openedReceptions.iterator().next();
                startArgs = Collections.emptyList();
            }
        }

        reception.start(startArgs);
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.START.has(target);
    }

}
