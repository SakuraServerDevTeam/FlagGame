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
import java.util.logging.Level;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.rollback.RollbackException;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.command.dashboard.OnelineBuilder;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class StageSaveCommand extends BaseCommand {

    public StageSaveCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                0,
                "[stage] <- save the stage",
                Perms.SAVE,
                "stage save"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Stage stage;
        if (args.size() < 1) {
            if (player == null) {
                throw new CommandException("&cステージを指定してください！");
            } else {
                GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
                stage = gplayer.getSetupSession()
                        .orElseThrow(() -> new CommandException("&cステージを指定してください！"))
                        .getSelectedStage();
                gplayer.destroySetupSession();
                gplayer.sendMessage("&aステージの選択を解除しました！");
            }
        } else {
            stage = plugin.getStages().getStage(args.get(0))
                    .orElseThrow(() -> new CommandException("&cステージが見つかりません！"));
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Actions.sendPrefixedMessage(sender, "&aステージデータを直列化しています...");
            World gameWorld = plugin.getServer().getWorld(plugin.getConfigs().getGameWorld());
            for (String area : stage.getAreas().getAreas()) {
                AreaInfo info = stage.getAreas().getAreaInfo(area);
                for (AreaInfo.RollbackData rollback : info.getRollbacks().values()) {
                    try {
                        rollback.setData(rollback.getTarget().write(plugin, gameWorld));
                    } catch (RollbackException ex) {
                        OnelineBuilder.newBuilder().warn("ステージ").value(stage.getName())
                                .warn("のエリア").value(area)
                                .warn("の直列化に失敗しました！").sendTo(sender);
                        plugin.getLogger().log(Level.WARNING, "Failed to serialize stage data", ex);
                    }
                }
            }
            Actions.sendPrefixedMessage(sender, "&aステージデータを直列化しました！");
            if (!plugin.getDatabases().isPresent()) {
                Actions.sendPrefixedMessage(sender, "&cデータベースへの接続に失敗しました！");
                return;
            }
            plugin.getDatabases().get().saveStage(stage, result -> {
                try {
                    result.get();
                    Actions.sendPrefixedMessage(sender, "&aステージを保存しました！");
                } catch (DatabaseException ex) {
                    Actions.sendPrefixedMessage(sender, "&cステージの保存に失敗しました！");
                    plugin.getLogger().log(Level.WARNING, "Failed to save stage", ex);
                }
            });
        });
    }

}
