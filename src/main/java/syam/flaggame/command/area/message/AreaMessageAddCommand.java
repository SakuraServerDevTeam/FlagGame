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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.area.AreaCommand;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.stage.area.GameMessageType;
import jp.llv.flaggame.api.stage.area.StageAreaInfo;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class AreaMessageAddCommand extends AreaCommand {

    public AreaMessageAddCommand(FlagGameAPI api) {
        super(
                api,
                3,
                "<id> <type> <message> <- add message",
                Perms.AREA_MESSAGE_ADD,
                "add"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        StageAreaInfo info = stage.getAreas().getAreaInfo(args.get(0));
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        GameMessageType type;
        try {
            type = GameMessageType.of(args.get(1));
        } catch (IllegalArgumentException ex) {
            String types = Arrays.stream(GameMessageType.values())
                    .map(p -> p.toString().toLowerCase())
                    .collect(Collectors.joining("/"));
            throw new CommandException("&cそのメッセージタイプはサポートされていません！\n&c" + types, ex);
        }
        StageAreaInfo.StageMessageData data = info.addMessage(args.get(2));
        data.setType(type);
        sendMessage(player, "&a'&6" + stage.getName() + "&a'の'&6"
                            + args.get(0) + "&a'エリアにメッセージ'&6"
                            + args.get(2) + "&a'を追加しました！");
    }

}
