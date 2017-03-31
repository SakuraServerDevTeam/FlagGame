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
package syam.flaggame.command.area;

import java.util.List;
import java.util.logging.Level;
import jp.llv.flaggame.rollback.SerializeTask;
import jp.llv.flaggame.rollback.StageData;
import jp.llv.flaggame.util.ConvertUtils;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author toyblocks
 */
public class AreaLoadCommand extends AreaCommand {

    public AreaLoadCommand(FlagGame plugin) {
        super(
                plugin,
                2,
                "<id> <name> <- load region",
                "area load",
                "alo"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        String id = args.get(0);
        AreaInfo info = stage.getAreas().getAreaInfo(id);
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        String savename = args.get(1);
        AreaInfo.RollbackData data = info.getRollback(savename);
        if (data == null) {
            throw new CommandException("&c該当の名前のデータは保存されていません！");
        }
        StageData target = data.getTarget();
        final Player playerFinal = player;
        SerializeTask task = target.load(plugin, stage, stage.getAreas().getArea(id), ex -> {
            if (ex == null && playerFinal.isOnline()) {
                Actions.sendPrefixedMessage(player, "&a'&6" + stage.getName() + "&a'の'&6"
                                                    + id + "&a'エリアの'&6"
                                                    + savename + "&a'をロードしました！");
            } else if (ex != null && playerFinal.isOnline()) {
                Actions.sendPrefixedMessage(player, "&c'&6" + stage.getName() + "&c'の'&6"
                                                    + id + "&c'エリアの'&6"
                                                    + savename + "&c'のロードに失敗しました！");
                plugin.getLogger().log(Level.WARNING, "Failed to load stage area", ex);
            }
        });
        String etr = Actions.getTimeString(ConvertUtils.toMiliseconds(task.getEstimatedTickRemaining()));
        Actions.sendPrefixedMessage(player, "&a'&6" + stage.getName() + "&a'の'&6"
                                            + id + "&a'エリアの'&6"
                                            + savename + "&a'をロードしています...");
        Actions.sendPrefixedMessage(player, "&aこれにはおよそ"+etr+"間かかる予定です...");
        task.start(plugin);
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.ROLLBACK.has(target);
    }

}
