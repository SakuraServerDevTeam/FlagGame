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

import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.player.SetupSession;
import syam.flaggame.util.Actions;
import syam.flaggame.util.Cuboid;
import syam.flaggame.util.WorldEditHandler;

public class SelectCommand extends BaseCommand {

    public SelectCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                1,
                "<reception-type> [optional args...] <- ready game",
                "ready"
        );
    
    }

    @Override
    public void execute() throws CommandException {
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        if (args.size() >= 1) {
            // flag select (ステージ名) - 選択
            Stage stage = this.plugin.getStages().getStage(args.get(0))
                    .orElseThrow(() -> new CommandException("&cステージ'" + args.get(0) + "'が見つかりません！"));
            
            // 既に選択中のステージと同じステージでない限りはセッションを作成
            if (gPlayer.getSetupSession().map(SetupSession::getSelectedStage).orElse(null) != stage) {
                gPlayer.createSetupSession(stage);
            }

            String msg = "&aステージ'&6" + stage.getName() + "&a'を選択しました！";
            if (selectRegion(stage)) {
                Actions.message(player, msg + "(+WorldEdit)");
            } else {
                Actions.message(player, msg);
            }
        } else {
            // flag select - 選択解除
            gPlayer.destroySetupSession();
            Actions.message(player, "&aステージの選択を解除しました！");
        }
    }

    private boolean selectRegion(final Stage stage) {
        if (!stage.getAreas().hasStageArea() || !WorldEditHandler.isAvailable()) {
            return false;
        }
        Cuboid stageArea = stage.getAreas().getStageArea();
        return WorldEditHandler.setSelectedArea(player, stageArea);
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.SELECT.has(target);
    }
}
