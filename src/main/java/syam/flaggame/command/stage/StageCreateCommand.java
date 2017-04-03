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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.event.StageCreateEvent;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.StageReservedException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class StageCreateCommand extends BaseCommand {

    public StageCreateCommand(FlagGame plugin) {
        super(
                plugin,
                true,
                1,
                "<stage> <- create stage",
                Perms.CREATE,
                "stage create"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        if (!Stage.NAME_REGEX.matcher(args.get(0)).matches()) {
            throw new CommandException("&cこのステージ名は使用できません！");
        }

        if (this.plugin.getStages().getStage(args.get(0)).isPresent()) {
            throw new CommandException("&cそのステージ名は既に存在します！");
        }

        // Call event
        Stage stage = new Stage(args.get(0));
        StageCreateEvent stageCreateEvent = new StageCreateEvent(player, stage);
        plugin.getServer().getPluginManager().callEvent(stageCreateEvent);
        if (stageCreateEvent.isCancelled()) {
            return;
        }

        // 新規ゲーム登録
        stage.setAvailable(false);
        stage.setProtected(false);
        this.plugin.getStages().addStage(args.get(0), stage);

        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(stage);

        // update dynmap
        plugin.getDynmap().updateRegions();
        Actions.message(player, "&a新規ステージ'" + stage.getName() + "'を登録して選択しました！");
    }

}
