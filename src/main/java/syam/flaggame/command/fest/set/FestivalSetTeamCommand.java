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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import jp.llv.flaggame.util.OnelineBuilder;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalSetTeamCommand extends FestivalSetCommand {

    public FestivalSetTeamCommand(FlagGameAPI api) {
        super(
                api,
                2,
                "<team> <color|'none'> <- set festival team",
                Perms.FESTIVAL_SET_TEAM,
                null,
                "team"
        );
    }

    @Override
    protected void execute(List<String> args, FestivalSchedule fest, Player sender) throws FlagGameException {
        if (args.get(1).equalsIgnoreCase("none")) {
            if (!fest.getTeams().containsValue(args.get(0))) {
                throw new CommandException("&cそのチームは存在しません！");
            }
            fest.removeTeam(args.get(0));
            OnelineBuilder.newBuilder()
                    .info("フェス").value(fest.getName())
                    .info("のチーム").value(args.get(0))
                    .info("を削除しました！！")
                    .sendTo(sender);
        } else {
            TeamColor color;
            try {
                color = TeamColor.of(args.get(1));
            } catch (IllegalArgumentException ex) {
                throw new CommandException("&c不明なチーム色です！", ex);
            }
            boolean replace = fest.getTeams().containsKey(color)
                              || fest.getTeams().containsValue(args.get(0));
            fest.setTeam(color, args.get(0));
            OnelineBuilder.newBuilder()
                    .info("フェス").value(fest.getName())
                    .info("にチーム").value(color.getBungeeChatColor(), args.get(0))
                    .info("を" + (replace ? "上書き" : "") + "設定しました！")
                    .sendTo(sender);
        }
    }

    @Override
    protected Collection<String> complete(List<String> args, FestivalSchedule fest, Player sender) throws FlagGameException {
        switch (args.size()) {
            case 1:
                return fest.getTeams().values().stream()
                        .filter(s -> s.startsWith(args.get(0)))
                        .collect(Collectors.toList());
            case 2:
                ArrayList<String> source = new ArrayList<>();
                for (TeamColor color : TeamColor.values()) {
                    source.add(color.name().toLowerCase());
                }
                source.add("none");
                return source.stream()
                        .filter(s -> s.startsWith(args.get(1)))
                        .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

}
