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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import jp.llv.flaggame.events.ReceptionJoinEvent;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.api.FlagGameAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;

import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.util.OptionSet;

public class GameJoinCommand extends BaseCommand {

    public GameJoinCommand(FlagGameAPI api) {
        super(
                api,
                true,
                0,
                "[game] <- join the game",
                Perms.GAME_JOIN,
                "join",
                "j"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        Reception reception = null;
        OptionSet options;

        Reception currentReception = null;
        if (gPlayer.getEntry().isPresent()) {
            currentReception = gPlayer.getEntry().get();
            if (currentReception.getState().toGameState() != Game.State.FINISHED) {
                throw new CommandException("&cあなたは既に参加中のゲームがあります!");
            }
        }

        if (args.size() >= 1) {// 引数があれば指定したステージに参加
            reception = this.api.getReceptions().getReception(args.get(0))
                    .orElseThrow(() -> new CommandException("&cステージ'" + args.get(0) + "'が見つかりません"));
            if (reception.getState() != Reception.State.OPENED) {
                throw new CommandException("&cそのゲームは受付中ではありません!");
            }
            options = new OptionSet(args.subList(1, args.size()));
        } else {// 引数がなければ自動補完
            Collection<Reception> openedReceptions = this.api.getReceptions()
                    .getReceptions(Reception.State.OPENED);
            if (openedReceptions.size() <= 0) {
                throw new CommandException("&c現在、参加受付中のゲームはありません！");
            } else if (openedReceptions.size() >= 2) {
                throw new CommandException("&c複数のゲームが受付中です！参加するステージを指定してください!");
            } else {// 受付中が1つのみなら自動補完
                reception = openedReceptions.iterator().next();
            }
            options = new OptionSet();
        }

        // Call event
        ReceptionJoinEvent joinEvent = new ReceptionJoinEvent(gPlayer, reception);
        api.getServer().getPluginManager().callEvent(joinEvent);
        if (joinEvent.isCancelled()) {
            return;
        }

        if (currentReception != null) {
            currentReception.leave(gPlayer);
        }
        reception.join(gPlayer, options);
    }
}
