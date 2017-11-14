/*
 * Copyright (C) 2017 toyblocks
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
package syam.flaggame.command.player.bit;

import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.util.FlagTabCompleter;
import jp.llv.flaggame.util.OnelineBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class BitPayCommand extends BaseCommand {

    public BitPayCommand(FlagGameAPI api) {
        super(
                api,
                true,
                2,
                "<player> <amount> <- pay specified amount of bits to a player",
                Perms.BIT_PAY,
                FlagTabCompleter.builder()
                        .forArg(0).suggestPlayers().create(),
                "pay"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer from = api.getPlayers().getPlayer(player);
        GamePlayer to = api.getPlayers().getPlayer(args.get(0));
        if (to == null || to == from) {
            throw new CommandException("&cオンラインプレイヤーを指定してください！");
        }

        double amount;
        try {
            amount = Double.parseDouble(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("&c第二引数に適切な数値を指定してください！");
        }
        if (!Double.isFinite(amount) || amount <= 0d) {
            throw new CommandException("&c第二引数に0以上の数値を指定してください！");
        }
        
        double oldBalance = from.getAccount().getBalance().get();
        if (oldBalance < amount) {
            throw  new CommandException("&c所持金が不足しています！");
        }
        if (!from.getAccount().getBalance().compareAndSet(oldBalance, oldBalance - amount)) {
            throw  new CommandException("&cトランザクションが同時発生しました！再度お試しください。");
        }
        
        double result = to.getAccount().getBalance().addAndGet(amount);
        if (Double.isInfinite(result)) {
            to.getAccount().getBalance().set(Double.MAX_VALUE);
        }
        
        OnelineBuilder.newBuilder()
                .value(to.getNickname()).info("に")
                .value(amount+"bits").info("送金しました！").sendTo(from.getPlayer());
        OnelineBuilder.newBuilder()
                .value(from.getNickname()).info("から")
                .value(amount+"bits").info("送金されました！").sendTo(to.getPlayer());
    }

}
