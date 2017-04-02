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
package syam.flaggame.command;

import java.util.Collection;
import java.util.List;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.reception.TeamColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.dashboard.DashboardBuilder;
import syam.flaggame.game.objective.ObjectiveType;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class DashboardCommand extends BaseCommand {

    public DashboardCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                0,
                "[stage] <- show information about stages",
                (Perms) null,
                "dashboard",
                "d"
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
            showStages(sender);
        } else {
            showStageInfo(sender, stage);
        }
    }

    private void showStages(CommandSender sender) {
        Collection<Stage> stages = plugin.getStages().getStages().values();
        DashboardBuilder.newBuilder("Stages", stages.size())
                .appendList(stages, (d, stage) -> {
                    d.key(stage.getName());
                    appendStageState(d, stage);
                    d.buttonRun("show").append("dashboard").append(stage.getName()).create()
                            .buttonRun("select").append("select").append(stage.getName()).create()
                            .buttonRun("delete").append("stage delete").append(stage.getName()).create();
                }).buttonSuggest("create").append("stage create").create()
                .sendTo(sender);
    }

    private void showStageInfo(CommandSender sender, Stage stage) {
        DashboardBuilder builder = DashboardBuilder
                .newBuilder("Stage Overview")
                .key("Name").value(stage.getName())
                .buttonRun("select").append("select").append(stage.getName()).create()
                .buttonRun("delete").append("stage delete").append(stage.getName()).create().br()
                .key("State").value(stage.isAvailable())
                .buttonRun("switch").append("set available").append(!stage.isAvailable()).create().space()
                .key("Protect").value(stage.isProtected())
                .buttonRun("switch").append("set protect").append(!stage.isProtected()).create().br()
                .key("Author").value(stage.getAuthor())
                .buttonSuggest("edit").append("set author").create().space()
                .key("Guide").value(stage.getDescription())
                .buttonSuggest("edit").append("set guide").create().br()
                .key("Description").value(stage.getDescription())
                .buttonSuggest("edit").append("set description").create().br()
                .key("Gametime").value(Actions.getTimeString(stage.getGameTime()))
                .buttonSuggest("edit").append("set gametime").create().space()
                .key("Cooldown").value(Actions.getTimeString(stage.getCooldown()))
                .buttonSuggest("edit").append("set cooldown").create().space()
                .key("TeamLimit").value(stage.getTeamLimit())
                .buttonSuggest("edit").append("set teamlimit").create().br()
                .key("KillScore").value(stage.getKillScore())
                .buttonSuggest("edit").append("set killscore").create().space()
                .key("DeathScore").value(stage.getDeathScore())
                .buttonSuggest("edit").append("set deathscore").create().br()
                .key("EntryFee").value(Actions.formatMoney(stage.getEntryFee()))
                .buttonSuggest("edit").append("set entryfee").create().space()
                .key("Prize").value(Actions.formatMoney(stage.getPrize()))
                .buttonSuggest("edit").append("set prize").create().br()
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
                        .buttonRun("set here").append("set spawn").append(color).create()
                        .buttonRun("delete").append("set spawn").append(color).append("none").create();
                if (sender instanceof Player) {
                    builder.buttonTp("tp", (Player) sender, stage.getSpawns().get(color));
                }
            }
        }
        builder.buttonSuggest("add").append("set spawn").create().br()
                .key("SpecSpawn").value(stage.getSpecSpawn().isPresent() ? "defined" : "undefined")
                .buttonRun("set here").append("set specspawn").create();
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

    private static void appendStageState(DashboardBuilder builder, Stage stage) {
        if (stage.getReception().isPresent()) {
            if (stage.getReception().get().getState().toGameState() == Game.State.PREPARATION) {
                builder.gold("受付中");
            } else {
                builder.green("開始済");
            }
        } else {
            builder.gray("待機中");
        }
    }

}
