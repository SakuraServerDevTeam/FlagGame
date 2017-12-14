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
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.NickPosition;
import jp.llv.flaggame.api.player.TrophySetupSession;
import jp.llv.flaggame.api.trophy.Trophy;
import jp.llv.flaggame.util.DashboardBuilder;
import jp.llv.flaggame.util.FlagTabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author toyblocks
 */
public class TrophyDashboardCommand extends BaseCommand {

    public TrophyDashboardCommand(FlagGameAPI api) {
        super(api,
                true,
                0,
                "[trophy] <- show a trophy dashboard",
                Perms.TROPHY_DASHBOARD,
                FlagTabCompleter.builder()
                        .forArg(0).suggestList((a, s, p) -> a.getTrophies().getNormalTrophies())
                        .create(),
                "dashboard",
                "d"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        Trophy trophy;
        if (!args.isEmpty()) {
            trophy = this.api.getTrophies().getTrophy(args.get(0))
                    .orElseThrow(() -> new CommandException("&cトロフィー'" + args.get(0) + "'が見つかりません！"));
        } else {
            trophy = gPlayer.getSetupSession(TrophySetupSession.class)
                    .orElseThrow(() -> new CommandException("&cあなたはトロフィーを選択していません！"))
                    .getReserved();
        }
        DashboardBuilder dashboard = DashboardBuilder
                .newBuilder("Trophy Overview")
                .key("Name");
        if (api.getTrophies().getInitialTrophy() == trophy) {
            dashboard.value("(Initial trophy)")
                    .buttonRun("select").append("trophy initial").append(trophy.getName()).create();
        } else {
            dashboard.value(trophy.getName())
                    .buttonRun("select").append("trophy select").append(trophy.getName()).create()
                    .buttonRun("delete").append("trophy delete").append(trophy.getName()).create();
        }
        dashboard.br()
                .key("money").value(Actions.formatMoney(trophy.getRewardMoney()))
                .buttonSuggest("edit").append("trophy set money").create()
                .key("bit").value(trophy.getRewardBits())
                .buttonSuggest("edit").append("trophy set bit").create().br();
        for (NickPosition pos : NickPosition.values()) {
            dashboard.buttonRun("nick(" + pos + ")")
                    .append("trophy nick list").append(pos).create();
        }
        dashboard.buttonRun("kit")
                .append("trophy kit list").create()
                .sendTo(sender);
    }

}
