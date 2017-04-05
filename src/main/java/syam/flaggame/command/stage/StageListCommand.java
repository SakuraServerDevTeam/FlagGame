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

import java.util.Collection;
import java.util.List;
import jp.llv.flaggame.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.util.DashboardBuilder;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class StageListCommand extends BaseCommand {

    public StageListCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                0,
                "<- show information about stages",
                Perms.STAGE_CONFIG_CHECK,
                "stage list"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Collection<Stage> stages = plugin.getStages().getStages().values();
        DashboardBuilder.newBuilder("Stages", stages.size())
                .appendList(stages, (d, stage) -> {
                    d.key(stage.getName());
                    appendStageState(d, stage);
                    d.buttonRun("show").append("stage dashboard").append(stage.getName()).create()
                            .buttonRun("select").append("stage select").append(stage.getName()).create()
                            .buttonRun("delete").append("stage delete").append(stage.getName()).create();
                }).buttonSuggest("create").append("stage create").create()
                .sendTo(sender);
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
