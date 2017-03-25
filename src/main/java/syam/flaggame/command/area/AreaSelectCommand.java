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

import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Cuboid;
import syam.flaggame.util.WorldEditHandler;

/**
 *
 * @author toyblocks
 */
public class AreaSelectCommand extends AreaCommand {

    public AreaSelectCommand(FlagGame plugin) {
        super(plugin);
        name = "area select";
        argLength = 1;
        usage = "<id> <- select region";
    }

    @Override
    public void execute(Stage stage) throws CommandException {
        String id = args.get(0);
        Cuboid area = stage.getAreas().getArea(id);
        if (area == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        if (!WorldEditHandler.setSelectedArea(player, area)) {
            throw new CommandException("&cエリア選択に失敗しました！");
        }
        sendMessage("&aエリアを選択しました！");
    }

    @Override
    public boolean permission() {
        return Perms.SELECT.has(player);
    }

}
