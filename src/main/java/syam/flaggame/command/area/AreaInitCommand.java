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

import java.util.logging.Level;
import jp.llv.flaggame.rollback.StageDataType;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author toyblocks
 */
public class AreaInitCommand extends AreaCommand {

    public AreaInitCommand(FlagGame plugin) {
        super(plugin);
        name = "area init";
        argLength = 1;
        usage = "<id> <- init region";
    }

    @Override
    public void execute(Stage stage) throws CommandException {
        String id = args.get(0);
        Cuboid area = stage.getAreas().getArea(id);
        if (area == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        final Player playerFinal = player;
        StageDataType.NONE.newInstance().load(plugin, stage, area, ex -> {
            if (ex == null && playerFinal.isOnline()) {
                Actions.sendPrefixedMessage(sender, "&a'&6" + stage.getName() + "&a'の'&6"
                                                    + id + "&a'エリアを初期化しました！");
            } else if (ex != null && playerFinal.isOnline()) {
                Actions.sendPrefixedMessage(sender, "&c'&6" + stage.getName() + "&c'の'&6"
                                                    + id + "&c'エリアの初期化に失敗しました！");
                plugin.getLogger().log(Level.WARNING, "Failed to init stage area", ex);
            }

        });
    }

    @Override
    public boolean permission() {
        return Perms.STAGE_CONFIG_SET.has(sender);
    }

}
