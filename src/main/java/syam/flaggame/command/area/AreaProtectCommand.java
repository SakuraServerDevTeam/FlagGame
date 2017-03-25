/*
 * Copyright (C) 2017 toyblocks
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

import java.util.Arrays;
import java.util.stream.Collectors;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.AreaState;
import jp.llv.flaggame.game.protection.Protection;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class AreaProtectCommand extends AreaCommand {

    public AreaProtectCommand(FlagGame plugin) {
        super(plugin);
        name = "area protect";
        argLength = 1;
        usage = "<id> [<type> <state>] <- manage protection of the region";
    }

    @Override
    public void execute(Stage stage) throws CommandException {
        String id = args.get(0);
        AreaInfo info = stage.getAreas().getAreaInfo(id);
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        if (args.size() == 1) {
            sendMessage("&a'&6" + stage.getName() + "&a'の'&6" + id + "&a'エリアの保護");
            for (Protection type : Protection.values()) {
                sendMessage("&6" + type.toString().toLowerCase() + "&7: " + info.isProtected(type).format());
            }
        } else if (args.size() == 2) {
            sendUsage();
            return;
        }
        Protection type;
        try {
            type = Protection.valueOf(args.get(1).toUpperCase());
        } catch (IllegalArgumentException ex) {
            String types = Arrays.stream(Protection.values())
                    .map(p -> p.toString().toLowerCase())
                    .collect(Collectors.joining("/"));
            throw new CommandException("&cその保護タイプはサポートされていません！\n&c" + types, ex);
        }
        AreaState state;
        try {
            state = AreaState.valueOf(args.get(2).toUpperCase());
        } catch (IllegalStateException ex) {
            String types = Arrays.stream(AreaState.values())
                    .map(p -> p.toString().toLowerCase())
                    .collect(Collectors.joining("/"));
            throw new CommandException("&cその保護状態はサポートされていません！\n&c" + types, ex);
        }
        info.setProtected(type, state);
        sendMessage("&a'&6" + stage.getName() + "&a'の'&6"
                    + id + "&a'エリアの保護'&6"
                    + type.toString().toLowerCase() + "&a'を'&6"
                    + state.format() + "&a'に設定しました！");
    }

    @Override
    public boolean permission() {
        return Perms.STAGE_CONFIG_SET.has(player);
    }

}
