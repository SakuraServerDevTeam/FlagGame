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

import java.util.Collection;
import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.NickPosition;
import jp.llv.flaggame.api.trophy.Trophy;
import jp.llv.flaggame.util.DashboardBuilder;
import jp.llv.flaggame.util.FlagTabCompleter;
import jp.llv.flaggame.util.Parser;
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 */
public class TrophyNickListCommand extends TrophyEditCommand<Trophy> {

    public TrophyNickListCommand(FlagGameAPI api) {
        super(
                api,
                1,
                "<position> <- list selected trophy's reward nicks",
                FlagTabCompleter.builder()
                        .forArg(0).suggestEnum(NickPosition.class)
                        .create(),
                Trophy.class,
                "nick list"
        );
    }

    @Override
    protected void execute(List<String> args, GamePlayer gamePlayer, Player player, Trophy trophy) throws CommandException {
        NickPosition position = Parser.asEnum(args.get(0), NickPosition.class);
        Collection<String> nicks = trophy.getRewardNicks(position);
        DashboardBuilder.newBuilder("Trophy reward nicks", position)
                .appendList(nicks, (builder, nick)
                        -> builder.gold(nick).buttonRun("delete")
                        .append("trophy nick delete").append(position).append(nick).create()
                ).buttonSuggest("add").append("trophy nick add").append(position).create()
                .sendTo(player);
    }

}
