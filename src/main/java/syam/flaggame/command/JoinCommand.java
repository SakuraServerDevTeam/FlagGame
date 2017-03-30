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
import jp.llv.flaggame.events.ReceptionJoinEvent;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.reception.GameReception;
import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;

import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

public class JoinCommand extends BaseCommand {

    public JoinCommand(FlagGame plugin) {
        super(
                plugin,
                true,
                0,
                "[game] <- join the game",
                "join",
                "j"
        );
    }

    @Override
    public void execute() throws CommandException {
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        GameReception reception = null;
        List<String> joinArgs;

        GameReception currentReception = null;
        if (gPlayer.getEntry().isPresent()) {
            currentReception = gPlayer.getEntry().get();
            if (currentReception.getState().toGameState() != Game.State.FINISHED) {
                throw new CommandException("&cあなたは既に参加中のゲームがあります!");
            }
        }

        if (args.size() >= 1) {// 引数があれば指定したステージに参加
            reception = this.plugin.getReceptions().getReception(args.get(0))
                    .orElseThrow(() -> new CommandException("&cステージ'" + args.get(0) + "'が見つかりません"));
            if (reception.getState() != GameReception.State.OPENED) {
                throw new CommandException("&cそのゲームは受付中ではありません!");
            }
            joinArgs = new ArrayList<>(args);
            joinArgs.remove(0);
        } else {// 引数がなければ自動補完
            Collection<GameReception> openedReceptions = this.plugin.getReceptions()
                    .getReceptions(GameReception.State.OPENED);
            if (openedReceptions.size() <= 0) {
                throw new CommandException("&c現在、参加受付中のゲームはありません！");
            } else if (openedReceptions.size() >= 2) {
                throw new CommandException("&c複数のゲームが受付中です！参加するステージを指定してください!");
            } else {// 受付中が1つのみなら自動補完
                reception = openedReceptions.iterator().next();
            }
            joinArgs = Collections.emptyList();
        }

        // Call event
        ReceptionJoinEvent joinEvent = new ReceptionJoinEvent(gPlayer, reception, reception.getEntryFee());
        plugin.getServer().getPluginManager().callEvent(joinEvent);
        if (joinEvent.isCancelled()) {
            return;
        }

        if (currentReception != null) {
            currentReception.leave(gPlayer);
        }
        reception.join(gPlayer, joinArgs);

        // 参加料チェック
        double cost = joinEvent.getEntryFee();
        if (cost > 0) {
            if (!Actions.checkMoney(player.getUniqueId(), cost)) {// 所持金確認
                throw new CommandException("&c参加するためには参加料 " + cost + "Coin が必要です！");
            }
            if (!Actions.takeMoney(player.getUniqueId(), cost)) {// 引き落とし
                throw new CommandException("&c参加料の引き落としにエラーが発生しました。管理人までご連絡ください。");
            } else {
                Actions.message(player, "&c参加料として " + cost + "Coin を支払いました！");
            }
        }
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.JOIN.has(target);
    }
}
