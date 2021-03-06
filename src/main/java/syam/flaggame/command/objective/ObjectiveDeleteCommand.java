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
package syam.flaggame.command.objective;

import jp.llv.flaggame.api.stage.objective.ObjectiveType;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.util.OnelineBuilder;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.stage.Stage;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class ObjectiveDeleteCommand extends ObjectiveCommand {

    public ObjectiveDeleteCommand(FlagGameAPI api) {
        super(
                api,
                3,
                "<x> <y> <z> <- delete objective located there",
                Perms.OBJECTIVE_DELETE,
                "delete",
                "del"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage, ObjectiveType type) throws CommandException {
        int x, y, z;
        try {
            x = Integer.parseInt(args.get(0));
            y = Integer.parseInt(args.get(1));
            z = Integer.parseInt(args.get(2));
        } catch (NumberFormatException ex) {
            throw new CommandException("&c無効な数値です！");
        }
        Location loc = new Location(api.getGameWorld(), x, y, z);
        if (!stage.getObjective(loc, type.getType()).isPresent()) {
            throw new CommandException("&cそこには" + type.getName() + "はありません！");
        };
        stage.removeObjective(loc);
        OnelineBuilder.newBuilder().info("ステージ").value(stage.getName()).info("の")
                .value(type.getName()).info("を削除しました！")
                .sendTo(player);
    }

}
