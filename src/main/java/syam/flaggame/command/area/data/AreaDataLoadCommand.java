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
package syam.flaggame.command.area.data;

import java.util.List;
import jp.llv.flaggame.api.stage.rollback.SerializeTask;
import jp.llv.flaggame.api.stage.rollback.StageData;
import jp.llv.flaggame.util.ConvertUtils;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.area.AreaCommand;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.stage.area.StageAreaInfo;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author toyblocks
 */
public class AreaDataLoadCommand extends AreaCommand {

    public AreaDataLoadCommand(FlagGameAPI api) {
        super(
                api,
                2,
                "<id> <name> <- load region",
                Perms.AREA_DATA_LOAD,
                "load"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        String id = args.get(0);
        StageAreaInfo info = stage.getAreas().getAreaInfo(id);
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        String savename = args.get(1);
        StageAreaInfo.StageRollbackData data = info.getRollback(savename);
        if (data == null) {
            throw new CommandException("&c該当の名前のデータは保存されていません！");
        }
        StageData target = data.getTarget();
        final Player playerFinal = player;
        SerializeTask task = target.load(stage, stage.getAreas().getArea(id), ex -> {
            if (ex == null && playerFinal.isOnline()) {
                Actions.sendPrefixedMessage(player, "&a'&6" + stage.getName() + "&a'の'&6"
                                                    + id + "&a'エリアの'&6"
                                                    + savename + "&a'をロードしました！");
            } else if (ex != null && playerFinal.isOnline()) {
                Actions.sendPrefixedMessage(player, "&c'&6" + stage.getName() + "&c'の'&6"
                                                    + id + "&c'エリアの'&6"
                                                    + savename + "&c'のロードに失敗しました！");
                api.getLogger().warn("Failed to load stage area", ex);
            }
        });
        String etr = Actions.getTimeString(ConvertUtils.toMiliseconds(task.getEstimatedTickRemaining()));
        Actions.sendPrefixedMessage(player, "&a'&6" + stage.getName() + "&a'の'&6"
                                            + id + "&a'エリアの'&6"
                                            + savename + "&a'をロードしています...");
        Actions.sendPrefixedMessage(player, "&aこれにはおよそ" + etr + "間かかる予定です...");
        task.start(api.getPlugin());
    }

}
