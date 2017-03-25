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

import java.util.logging.Level;
import jp.llv.flaggame.rollback.RollbackException;
import jp.llv.flaggame.rollback.RollbackTarget;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
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
        try {
            RollbackTarget.LEGACY.deserialize(stage, area, null);
        } catch (RollbackException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to rollback", ex);
            throw new CommandException("&cロールバックに失敗しました！", ex);
        }
        sendMessage("&aエリアの初期化に成功しました！");
    }

    @Override
    public boolean permission() {
        return Perms.STAGE_CONFIG_SET.has(player);
    }
    
}
