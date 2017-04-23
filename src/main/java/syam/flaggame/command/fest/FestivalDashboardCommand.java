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
package syam.flaggame.command.fest;

import java.util.List;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import jp.llv.flaggame.util.DashboardBuilder;
import jp.llv.flaggame.util.FlagTabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalDashboardCommand extends BaseCommand {

    public FestivalDashboardCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                0,
                "[festival] <- show information about a festival",
                Perms.FESTIVAL_DASHBOARD,
                FlagTabCompleter.builder()
                .forArg(1).suggestList((p, s, a) -> p.getFestivals().getFestivals().keySet())
                .create(),
                "dashboard",
                "d"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        FestivalSchedule festival = args.size() >= 1
                ? plugin.getFestivals().getFestival(args.get(0)).orElse(null)
                : player == null
                        ? null
                        : plugin.getPlayers().getPlayer(player).getSetupSession()
                        .map(s -> s.getSelected(FestivalSchedule.class)).orElse(null);
        if (festival == null) {
            throw new CommandException("&cフェスを選択してください！");
        }
        DashboardBuilder
                .newBuilder("Festival Overview")
                .key("Name").value(festival.getName())
                .buttonRun("select").append("festival select").append(festival.getName()).create()
                .buttonRun("delete").append("festival delete").append(festival.getName()).create().br()
                .key("EntryFee").value(Actions.formatMoney(festival.getEntryFee()))
                .buttonSuggest("edit").append("festival set entryfee").create().space()
                .key("Prize").value(Actions.formatMoney(festival.getPrize()))
                .buttonSuggest("edit").append("festival set prize").create().br()
                .appendList(festival.getTeams().entrySet(), (d, e) -> {
                    d.text(e.getKey().getBungeeChatColor(), e.getValue())
                            .buttonSuggest("set color").append("festival set team").append(e.getValue()).create()
                            .buttonRun("delete").append("festival set team").append(e.getValue()).append("none").create();
                }).buttonSuggest("add team").append("festival set team").create().br()
                .buttonRun("manage matches").append("festival match list").create()
                .sendTo(sender);
    }

}
