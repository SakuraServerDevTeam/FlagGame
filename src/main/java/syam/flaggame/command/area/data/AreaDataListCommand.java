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
import java.util.Map;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.area.AreaCommand;
import jp.llv.flaggame.util.DashboardBuilder;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class AreaDataListCommand extends AreaCommand {

    public AreaDataListCommand(FlagGame plugin) {
        super(
                plugin,
                1,
                "<id> <- show a list of area data",
                Perms.STAGE_CONFIG_CHECK,
                "area data list"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage) throws CommandException {
        String id = args.get(0);
        AreaInfo info = stage.getAreas().getAreaInfo(id);
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        
        Map<String, AreaInfo.RollbackData> rollbacks = info.getRollbacks();
        DashboardBuilder.newBuilder("Area Data", rollbacks.size())
                .appendMap(rollbacks, (t, name, data) -> {
                    t.value(data.getTarget().getType().toString().toLowerCase()).space()
                            .green("開始後").green(Actions.getTimeString(data.getTiming()))
                            .buttonSuggest("edit").append("area data timing").append(id).append(name).create()
                            .buttonRun("load").append("area data load").append(id).append(name).create()
                            .buttonRun("delete").append("area data delete").append(id).append(name).create();
                }).buttonSuggest("add").append("area data save").append(id).create()
                .sendTo(player);
    }

}
