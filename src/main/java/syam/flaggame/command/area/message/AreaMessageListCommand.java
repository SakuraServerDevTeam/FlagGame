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
public class AreaMessageListCommand extends AreaCommand {

    public AreaMessageListCommand(FlagGameAPI api) {
        super(
                api,
                0,
                "<id> <- show a list of messages",
                Perms.AREA_MESSAGE_LIST,
                "list",
                "l"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        StageAreaInfo info = stage.getAreas().getAreaInfo(args.get(0));
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        List<? extends StageAreaInfo.StageMessageData> messages = info.getMessages();
        sendMessage(player, "&a ==============&b MessageList(" + messages.size() + ") &a==============");
        if (messages.isEmpty()) {
            Actions.message(player, " &7定義済みのメッセージがありません");
        } else {
            for (int i = 0; i < messages.size(); i++) {
                StageAreaInfo.StageMessageData message = messages.get(i);
                sendMessage(player, "&7"
                                    + (i + 1) + ". &6"
                                    + message.getMessage()
                                    + "&7(" + message.getType().toString().toLowerCase()
                                    + "," + Actions.getTimeString(message.getTiming())
                                    + ")"
                );
            }
        }
        sendMessage(player, "&a ============================================");
    }

}
