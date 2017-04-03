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
import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.command.dashboard.DashboardBuilder;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.game.objective.ObjectiveType;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class StageDashboardCommand extends BaseCommand {

    public StageDashboardCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                1,
                "<stage> <- show information about a stage",
                Perms.STAGE_CONFIG_CHECK,
                "stage dashboard"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Stage stage = null;
        if (player != null) {
            GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
            stage = gplayer.getSetupSession().map(s -> s.getSelectedStage()).orElse(null);
        }
        if (stage == null && !args.isEmpty()) {
            stage = plugin.getStages().getStage(args.get(0)).orElse(null);
        }
        if (stage == null) {
            throw new CommandException("&cステージが見つかりません！");
        }
        DashboardBuilder builder = DashboardBuilder
                .newBuilder("Stage Overview")
                .key("Name").value(stage.getName())
                .buttonRun("select").append("select").append(stage.getName()).create()
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
                .key("Spawns");
        if (stage.getSpawns().isEmpty()) {
            builder.value("undefined");
        } else {
            boolean head = true;
            for (TeamColor color : stage.getSpawns().keySet()) {
                if (!head) {
                    builder.space();
                }
                head = false;
                builder.text(color.getBungeeChatColor(), color.toString().toLowerCase())
                        .buttonRun("set here").append("stage set spawn").append(color).create()
                        .buttonRun("delete").append("stage set spawn").append(color).append("none").create();
                if (sender instanceof Player) {
                    builder.buttonTp("tp", (Player) sender, stage.getSpawns().get(color));
                }
            }
        }
        builder.buttonSuggest("add").append("stage set spawn").create().br()
                .key("SpecSpawn").value(stage.getSpecSpawn().isPresent() ? "defined" : "undefined")
                .buttonRun("set here").append("stage set specspawn").create();
        if (sender instanceof Player && stage.getSpecSpawn().isPresent()) {
            builder.buttonTp("tp", (Player) sender, stage.getSpecSpawn().get());
        }
        builder.br()
                .buttonRun("area").append("area list").create();
        for (ObjectiveType obj : ObjectiveType.values()) {
            builder.buttonRun(obj.toString().toLowerCase())
                    .append("objective list").append(obj).create();
        }
        builder.sendTo(sender);
    }
    
}
