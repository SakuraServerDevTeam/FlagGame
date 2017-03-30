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

import java.util.Map;
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
public class AreaRollbackCommand extends AreaCommand {

    public AreaRollbackCommand(FlagGame plugin) {
        super(
                plugin,
                1,
                "<id> [name] [timing] <- schedule rollback",
                "area rollback"
        );
    }

    @Override
    public void execute(Stage stage) throws CommandException {
        String id = args.get(0);
        AreaInfo info = stage.getAreas().getAreaInfo(id);
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }

        if (args.size() == 1) {
            int count = info.getRollbacks().size();
            Actions.message(sender, "&a ==============&b RollbackList(" + count + ") &a==============");
            if (count == 0) {
                Actions.message(sender, " &7設定されているロールバックがありません");
            } else {
                for (Map.Entry<String, AreaInfo.RollbackData> entry : info.getRollbacks().entrySet()) {
                    sendMessage("&6" + entry.getKey() + "&a: " + entry.getValue().getTarget().toString().toLowerCase());
                }
            }
            Actions.message(sender, "&a ============================================");
            return;
        }

        String savename = args.get(1);
        AreaInfo.RollbackData data = info.getRollback(savename);
        if (data == null) {
            throw new CommandException("&cその名前のロールバックデータは存在しません！");
        }
        if (args.size() == 2) {
            sendMessage("&aステージ'&6" + stage.getName()
                        + "&a'のエリア'&6" + id
                        + "&a'でのロールバック'&6" + savename
                        + "&a'は開始後'" + Actions.getTimeString(data.getTiming())
                        + "&a'です！"
            );
        } else {
            int timing;
            try {
                timing = Integer.parseInt(args.get(2));
            } catch (NumberFormatException ex) {
                throw new CommandException("&c無効な数字です！", ex);
            }
            if (timing < 0) {
                throw new CommandException("&c0以上の数字を指定する必要性があります！");
            }
            data.setTiming(timing);
            sendMessage("&aステージ'&6" + stage.getName()
                        + "&a'のエリア'&6" + id
                        + "&a'でのロールバック'&6" + savename
                        + "&a'を開始後'" + Actions.getTimeString(timing)
                        + "&a'にスケジュールしました！"
            );
        }
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.STAGE_CONFIG_SET.has(target);
    }

}
