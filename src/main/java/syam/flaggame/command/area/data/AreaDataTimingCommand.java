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
import org.bukkit.entity.Player;
import jp.llv.flaggame.util.ConvertUtils;
import syam.flaggame.FlagGame;
import syam.flaggame.command.area.AreaCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author toyblocks
 */
public class AreaDataTimingCommand extends AreaCommand {

    public AreaDataTimingCommand(FlagGame plugin) {
        super(
                plugin,
                3,
                "<id> <name> [timing] <- schedule rollback",
                Perms.AREA_DATA_TIMING,
                "timing"
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
            throw new CommandException("&cその名前のロールバックデータは存在しません！");
        }
        if (args.size() == 2) {
            sendMessage(player, "&aステージ'&6" + stage.getName()
                        + "&a'のエリア'&6" + id
                        + "&a'でのロールバック'&6" + savename
                        + "&a'は開始後'" + Actions.getTimeString(data.getTiming())
                        + "&a'です！"
            );
        } else {
            long timing;
            try {
                timing = ConvertUtils.toMiliseconds(Double.parseDouble(args.get(2)));
            } catch (NumberFormatException ex) {
                throw new CommandException("&c無効な数字です！", ex);
            }
            if (timing < 0) {
                throw new CommandException("&c0以上の数字を指定する必要性があります！");
            }
            data.setTiming(timing);
            sendMessage(player, "&aステージ'&6" + stage.getName()
                        + "&a'のエリア'&6" + id
                        + "&a'でのロールバック'&6" + savename
                        + "&a'を開始後'" + Actions.getTimeString(timing)
                        + "&a'にスケジュールしました！"
            );
        }
    }

}
