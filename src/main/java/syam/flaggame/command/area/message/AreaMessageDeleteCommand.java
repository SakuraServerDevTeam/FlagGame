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
import syam.flaggame.FlagGame;
import syam.flaggame.command.area.AreaCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class AreaMessageDeleteCommand extends AreaCommand {

    public AreaMessageDeleteCommand(FlagGame plugin) {
        super(
                plugin,
                1,
                "<id> <index> <- delete message",
                Perms.AREA_MESSAGE_DELETE,
                "delete",
                "del"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        AreaInfo info = stage.getAreas().getAreaInfo(args.get(0));
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
        AreaInfo.MessageData data = info.getMessages().get(index - 1);
        sendMessage(player, "&a'&6" + stage.getName() + "&a'の'&6"
                            + args.get(0) + "&a'エリアのメッセージ'&6"
                            + data.getMessage() + "&a'を削除しました！"
        );
    }
}
