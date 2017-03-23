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
import jp.llv.flaggame.reception.TeamColor;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class AreaGodCommand extends AreaCommand {

    public AreaGodCommand(FlagGame plugin) {
        super(plugin);
        name = "area god";
        argLength = 0;
        usage = "<id> <[color] [state]> <- manage area godmode";
    }

    @Override
    public void execute(Stage stage) throws CommandException {
        String id = args.get(0);
        AreaInfo info = stage.getAreas().getAreaInfo(id);
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        if (args.size() == 1) {
            sendMessage("&a'&6" + stage.getName() + "&a'の'&6" + id + "&a'の無敵状態");
            for (TeamColor color : stage.getSpawns().keySet()) {
                sendMessage(color.getRichName() + "&7: " + info.isGodmode(color).format());
            }
        } else if (args.size() == 2) {
            sendUsage();
            return;
        }
        TeamColor color;
        try {
            color = TeamColor.valueOf(args.get(1).toUpperCase());
        } catch (IllegalArgumentException ex) {
            String types = Arrays.stream(TeamColor.values())
                    .map(p -> p.toString().toLowerCase())
                    .collect(Collectors.joining("/"));
            throw new CommandException("&cそのチーム色はサポートされていません！\n&c" + types, ex);
        }
        AreaInfo.State state;
        try {
            state = AreaInfo.State.valueOf(args.get(2).toUpperCase());
        } catch (IllegalStateException ex) {
            String types = Arrays.stream(AreaInfo.State.values())
                    .map(p -> p.toString().toLowerCase())
                    .collect(Collectors.joining("/"));
            throw new CommandException("&cその無敵状態はサポートされていません！\n&c" + types, ex);
        }
        info.setGodmode(color, state);
        sendMessage("&a'&6" + stage.getName() + "&a'の'&6"
                    + id + "&a'エリアの'"
                    + color.getRichName() + "&a'の無敵設定を'&6"
                    + state.format() + "&a'に設定しました！");
    }

    @Override
    public boolean permission() {
        return Perms.STAGE_CONFIG_SET.has(player);
    }
    
}
