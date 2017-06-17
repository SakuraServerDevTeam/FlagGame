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
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.rollback.RollbackException;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.util.OnelineBuilder;
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

    public StageSaveCommand(FlagGameAPI api) {
        super(
                api,
                false,
                0,
                "[stage] <- save the stage",
                Perms.STAGE_SAVE,
                "save"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Stage stage;
        if (args.size() < 1) {
            if (player == null) {
                throw new CommandException("&cステージを指定してください！");
            } else {
                GamePlayer gplayer = api.getPlayers().getPlayer(player);
                stage = gplayer.getSetupSession()
                        .orElseThrow(() -> new CommandException("&cステージを指定してください！"))
                        .getSelected(Stage.class);
                if (stage == null) {
                    throw new CommandException("&cあなたはステージを選択していません！");
                }
                gplayer.destroySetupSession();
                gplayer.sendMessage("&aステージの選択を解除しました！");
            }
        } else {
            stage = api.getStages().getStage(args.get(0))
                    .orElseThrow(() -> new CommandException("&cステージが見つかりません！"));
        }
        api.getServer().getScheduler().runTaskAsynchronously(api.getPlugin(), () -> {
            Actions.sendPrefixedMessage(sender, "&aステージデータを直列化しています...");
            World gameWorld = api.getServer().getWorld(api.getConfig().getGameWorld());
            for (String area : stage.getAreas().getAreas()) {
                AreaInfo info = stage.getAreas().getAreaInfo(area);
                for (AreaInfo.RollbackData rollback : info.getRollbacks().values()) {
                    try {
                        rollback.setData(rollback.getTarget().write(api, gameWorld));
                    } catch (RollbackException ex) {
                        OnelineBuilder.newBuilder().warn("ステージ").value(stage.getName())
                                .warn("のエリア").value(area)
                                .warn("の直列化に失敗しました！").sendTo(sender);
                        api.getLogger().warn("Failed to serialize stage data", ex);
                    }
                }
            }
            Actions.sendPrefixedMessage(sender, "&aステージデータを直列化しました！");
            if (!api.getDatabase().isPresent()) {
                Actions.sendPrefixedMessage(sender, "&cデータベースへの接続に失敗しました！");
                return;
            }
            api.getDatabase().get().saveStage(stage, result -> {
                try {
                    result.get();
                    Actions.sendPrefixedMessage(sender, "&aステージを保存しました！");
                } catch (DatabaseException ex) {
                    Actions.sendPrefixedMessage(sender, "&cステージの保存に失敗しました！");
                    api.getLogger().warn("Failed to save stage", ex);
                }
            });
        });
    }

}
