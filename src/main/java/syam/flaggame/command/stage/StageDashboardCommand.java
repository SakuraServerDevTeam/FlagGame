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
package syam.flaggame.command.stage;

import java.util.List;
import jp.llv.flaggame.reception.TeamColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.util.DashboardBuilder;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.stage.objective.ObjectiveType;
import syam.flaggame.permission.Perms;
import jp.llv.flaggame.api.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class StageDashboardCommand extends BaseCommand {

    public StageDashboardCommand(FlagGameAPI api) {
        super(
                api,
                false,
                1,
                "<stage> <- show information about a stage",
                Perms.STAGE_DASHBOARD,
                "dashboard",
                "d"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Stage stage = null;
        if (player != null) {
            GamePlayer gplayer = api.getPlayers().getPlayer(player);
            stage = gplayer.getSetupSession().map(s -> s.getSelected(Stage.class)).orElse(null);
        }
        if (stage == null && !args.isEmpty()) {
            stage = api.getStages().getStage(args.get(0)).orElse(null);
        }
        if (stage == null) {
            throw new CommandException("&cステージが見つかりません！");
        }
        DashboardBuilder builder = DashboardBuilder
                .newBuilder("Stage Overview")
                .key("Name").value(stage.getName())
                .buttonRun("select").append("stage select").append(stage.getName()).create()
                .buttonRun("delete").append("stage delete").append(stage.getName()).create().br()
                .key("State").value(stage.isAvailable())
                .buttonRun("switch").append("stage set available").append(!stage.isAvailable()).create().space()
                .key("Protect").value(stage.isProtected())
                .buttonRun("switch").append("stage set protect").append(!stage.isProtected()).create().br()
                .key("Author").value(stage.getAuthor())
                .buttonSuggest("edit").append("stage set author").create().space()
                .key("Guide").value(stage.getDescription())
                .buttonSuggest("edit").append("stage set guide").create().br()
                .key("Description").value(stage.getDescription())
                .buttonSuggest("edit").append("stage set description").create().br()
                .key("Gametime").value(Actions.getTimeString(stage.getGameTime()))
                .buttonSuggest("edit").append("stage set gametime").create().space()
                .key("Cooldown").value(Actions.getTimeString(stage.getCooldown()))
                .buttonSuggest("edit").append("stage set cooldown").create().space()
                .key("TeamLimit").value(stage.getTeamLimit())
                .buttonSuggest("edit").append("stage set teamlimit").create().br()
                .key("KillScore").value(stage.getKillScore())
                .buttonSuggest("edit").append("stage set killscore").create().space()
                .key("DeathScore").value(stage.getDeathScore())
                .buttonSuggest("edit").append("stage set deathscore").create().br()
                .key("EntryFee").value(Actions.formatMoney(stage.getEntryFee()))
                .buttonSuggest("edit").append("stage set entryfee").create().space()
                .key("Prize").value(Actions.formatMoney(stage.getPrize()))
                .buttonSuggest("edit").append("stage set prize").create().br()
                .buttonRun("area").append("area list").create();
        for (ObjectiveType obj : ObjectiveType.values()) {
            builder.buttonRun(obj.toString().toLowerCase())
                    .append("objective list").append(obj).create();
        }
        builder.sendTo(sender);
    }

}
