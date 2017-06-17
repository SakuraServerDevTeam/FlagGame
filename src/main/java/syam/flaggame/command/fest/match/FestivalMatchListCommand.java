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

import java.util.List;
import java.util.Map;
import jp.llv.flaggame.reception.fest.FestivalMatch;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import jp.llv.flaggame.util.DashboardBuilder;
import jp.llv.flaggame.util.FlagTabCompleter;
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
public class FestivalMatchListCommand extends BaseCommand {

    public FestivalMatchListCommand(FlagGameAPI api) {
        super(
                api,
                false,
                0,
                "<- show matches in a selected festival",
                Perms.FESTIVAL_MATCH_LIST,
                FlagTabCompleter.builder()
                .forArg(1).suggestList((p, s, a) -> p.getFestivals().getFestivals().keySet())
                .create(),
                "list"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        FestivalSchedule festival = args.size() >= 1
                ? api.getFestivals().getFestival(args.get(0)).orElse(null)
                : player == null
                        ? null
                        : api.getPlayers().getPlayer(player).getSetupSession()
                        .map(s -> s.getSelected(FestivalSchedule.class)).orElse(null);
        if (festival == null) {
            throw new CommandException("&cフェスを選択してください！");
        }
        DashboardBuilder dashboard = DashboardBuilder
                .newBuilder("Festival Matches")
                .key("Name").value(festival.getName())
                .buttonRun("overview").append("festival overview").append(festival.getName()).create()
                .br();
        List<Map<String, FestivalMatch>> source = festival.getMatches();
        for (int i = 0; i < source.size(); i++) {
            Map<String, FestivalMatch> round = source.get(i);
            for (Map.Entry<String, FestivalMatch> match : round.entrySet()) {
                dashboard.key(i + 1).value(match.getKey())
                        .buttonSuggest("set team").append("festival match team").create()
                        .buttonRun(".bdelete").append("festival match delete")
                        .append(i + 1).append(match.getKey()).create().br();
            }
        }
        dashboard.buttonSuggest("create").append("festival match create")
                .append(source.size() + 1).create().sendTo(sender);
    }

}
