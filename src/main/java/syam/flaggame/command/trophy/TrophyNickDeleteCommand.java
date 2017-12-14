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
package syam.flaggame.command.trophy;

import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.NickPosition;
import jp.llv.flaggame.api.trophy.Trophy;
import jp.llv.flaggame.util.FlagTabCompleter;
import jp.llv.flaggame.util.OnelineBuilder;
import jp.llv.flaggame.util.Parser;
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 */
public class TrophyNickDeleteCommand extends TrophyEditCommand<Trophy> {

    public TrophyNickDeleteCommand(FlagGameAPI api) {
        super(
                api,
                2,
                "<position> <nick> <- delete selected trophy's reward nick",
                FlagTabCompleter.builder()
                        .forArg(0).suggestEnum(NickPosition.class)
                        .create(),
                Trophy.class,
                "nick delete",
                "nick del"
        );
    }

    @Override
    protected void execute(List<String> args, GamePlayer gamePlayer, Player player, Trophy trophy) throws CommandException {
        NickPosition position = Parser.asEnum(args.get(0), NickPosition.class);
        String nick = String.join(" ", args.subList(1, args.size()));
        if (!trophy.getRewardNicks(position).contains(nick)) {
            throw new CommandException("&c'"+nick+"'は未設定のNickです");
        }
        trophy.removeRewardNick(position, nick);
        OnelineBuilder.newBuilder()
                .info("トロフィー").value(trophy.getName())
                .info("の報酬Nickに").value(nick)
                .info("(").value(position).info(")")
                .info("が追加されました！")
                .sendTo(player);
    }

}
