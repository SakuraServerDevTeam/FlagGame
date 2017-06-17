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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.reception.fest.FestivalMatch;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import jp.llv.flaggame.util.OnelineBuilder;
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
public class FestivalMatchTeamCommand extends BaseCommand {

    public FestivalMatchTeamCommand(FlagGameAPI api) {
        super(
                api,
                true,
                4,
                "<round> <stage> <team> <color> <- delete a festival match",
                Perms.FESTIVAL_MATCH_DELETE,
                "team"
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
            throw new CommandException("&c異常な数値フォーマットです！", ex);
        }
        if (!festival.getRound(index).containsKey(args.get(1))) {
            throw new CommandException("&cそのマッチは追加されていません！");
        }
        FestivalMatch match = festival.getMatch(index, args.get(1));
        TeamColor team = festival.getTeam(args.get(2));
        if (team == null) {
            throw new CommandException("&cそのチームは設定されていません！");
        }
        TeamColor color;
        try {
            color = TeamColor.of(args.get(3));
        } catch (IllegalArgumentException ex) {
            throw new CommandException("&c異常なチーム色です！");
        }
        match.setColorMap(team, color);
        OnelineBuilder.newBuilder()
                .info("フェス").value(festival.getName())
                .info("のラウンド").value(index)
                .info("のステージ").value(args.get(1))
                .info("でチーム").value(args.get(2))
                .info("が").text(color.getBungeeChatColor(), color.getName())
                .info("としてプレイするよう設定しました！");
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
                        .filter(s -> s.startsWith(args.get(1)))
                        .collect(Collectors.toList());
            } catch (NumberFormatException ex) {
                return Collections.emptyList();
            }
        } else if (args.size() == 3) {
            return festival.getTeams().values().stream()
                    .filter(s -> s.startsWith(args.get(2)))
                    .collect(Collectors.toList());
        } else if (args.size() == 4) {
            return Arrays.stream(TeamColor.values())
                    .map(TeamColor::name)
                    .filter(s -> s.startsWith(args.get(3)))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
