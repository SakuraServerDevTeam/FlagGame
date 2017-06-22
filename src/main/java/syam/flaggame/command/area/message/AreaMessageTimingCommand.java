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
package syam.flaggame.command.area.message;

import java.util.List;
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
 * @author SakuraServerDev
 */
public class AreaMessageTimingCommand extends AreaCommand {

    public AreaMessageTimingCommand(FlagGameAPI api) {
        super(
                api,
                3,
                "<id> <index> <timing> <- set message timing",
                Perms.AREA_MESSAGE_TIMING,
                "timing"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        StageAreaInfo info = stage.getAreas().getAreaInfo(args.get(0));
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        int index;
        try {
            index = Integer.parseInt(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("&c無効なインデックスです！整数値を指定してください！", ex);
        }
        if (index < 0 || info.getMessages().size() <= index) {
            throw new CommandException("&c無効なインデックスです！");
        }
        long timing;
        try {
            timing = ConvertUtils.toMiliseconds(Double.parseDouble(args.get(2)));
        } catch (NumberFormatException ex) {
            throw new CommandException("&c無効な数値です！正の小数値を指定してください！", ex);
        }
        if (timing <= 0) {
            throw new CommandException("&c無効な数値です！正の小数値を指定してください！");
        }
        StageAreaInfo.StageMessageData data = info.getMessages().get(index);
        data.setTiming(timing);
        sendMessage(player, "&a'&6" + stage.getName() + "&a'の'&6"
                            + args.get(0) + "&a'エリアのメッセージ'&6"
                            + data.getMessage() + "&a'のタイミングを'&6"
                            + Actions.getTimeString(timing) + "&a'に設定しました！");
    }

}
