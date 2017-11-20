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
import jp.llv.flaggame.api.FlagGameAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.stage.Stage;
import syam.flaggame.permission.Perms;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.StageSetupSession;
import syam.flaggame.util.Actions;
import syam.flaggame.util.Cuboid;
import syam.flaggame.util.WorldEditHandler;

public class StageSelectCommand extends BaseCommand {

    public StageSelectCommand(FlagGameAPI api) {
        super(
                api,
                true,
                0,
                "[stage] <- select exist stage",
                Perms.STAGE_SELECT,
                "select",
                "sel"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        if (args.size() >= 1) {
            Stage stage = this.api.getStages().getStage(args.get(0))
                    .orElseThrow(() -> new CommandException("&cステージ'" + args.get(0) + "'が見つかりません！"));

            // 既に選択中のステージと同じステージでない限りはセッションを作成
            if (gPlayer.getSetupSession(StageSetupSession.class).map(StageSetupSession::getReserved).orElse(null) != stage) {
                gPlayer.createSetupSession(stage, StageSetupSession.class);
            }

            String msg = "&aステージ'&6" + stage.getName() + "&a'を選択しました！";
            if (selectRegion(player, stage)) {
                Actions.message(player, msg + "(+WorldEdit)");
            } else {
                Actions.message(player, msg);
            }
        } else {
            gPlayer.getSetupSession(StageSetupSession.class).orElseThrow(() -> new CommandException("&cあなたはステージを選択していません！"));
            gPlayer.destroySetupSession(StageSetupSession.class);
            Actions.message(player, "&aステージの選択を解除しました！");
        }
    }

    private boolean selectRegion(Player player, Stage stage) {
        if (!stage.getAreas().hasStageArea() || !WorldEditHandler.isAvailable()) {
            return false;
        }
        Cuboid stageArea = stage.getAreas().getStageArea();
        return WorldEditHandler.setSelectedArea(player, stageArea);
    }
}
