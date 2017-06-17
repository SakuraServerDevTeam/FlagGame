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
package syam.flaggame.command.fest.match;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalMatchDeleteCommand extends BaseCommand {

    public FestivalMatchDeleteCommand(FlagGameAPI api) {
        super(
                api,
                true,
                2,
                "<round> <stage> <- delete a festival match",
                Perms.FESTIVAL_MATCH_DELETE,
                "delete"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        FestivalSchedule festival = api.getPlayers().getPlayer(player)
                .getSetupSession().map(s -> s.getSelected(FestivalSchedule.class))
                .orElseThrow(() -> new CommandException("&cフェスを選択してください！"));
        int index;
        try {
            index = Integer.parseInt(args.get(0)) - 1;
        } catch (NumberFormatException ex) {
            throw new CommandException("&c異常な数値フォーマットです！");
        }
        if (festival.getMatches().size() <= index) {
            throw new CommandException("&cそのラウンドは存在しません！");
        } else if (!festival.getRound(index).containsKey(args.get(1))) {
            throw new CommandException("&cそのマッチは追加されていません！");
        }
        festival.removeMatch(index, args.get(1));
        if (festival.getRound(index).isEmpty()) {
            festival.removeRound(index);
        }
        sendMessage(sender, "&aマッチを削除しました！");
    }

    @Override
    protected Collection<String> complete(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        FestivalSchedule festival = api.getPlayers().getPlayer(player)
                .getSetupSession().map(s -> s.getSelected(FestivalSchedule.class))
                .orElseThrow(() -> new FlagGameException());
        if (args.isEmpty()) {
            throw new IllegalArgumentException();
        } else if (args.size() == 1) {
            return IntStream.rangeClosed(1, festival.getMatches().size())
                    .mapToObj(Integer::toString)
                    .filter(s -> s.startsWith(args.get(0)))
                    .collect(Collectors.toList());
        } else if (args.size() == 2) {
            try {
                int index = Integer.parseInt(args.get(0)) - 1;
                return festival.getRound(index).keySet().stream()
                        .filter(s -> s.startsWith(args.get(0)))
                        .collect(Collectors.toList());
            } catch (NumberFormatException ex) {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }
}
