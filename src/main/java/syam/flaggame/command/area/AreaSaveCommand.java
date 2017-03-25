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

import java.util.Arrays;
import java.util.stream.Collectors;
import jp.llv.flaggame.rollback.RollbackException;
import jp.llv.flaggame.rollback.RollbackTarget;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class AreaSaveCommand extends AreaCommand {

    public AreaSaveCommand(FlagGame plugin) {
        super(plugin);
        name = "area save";
        argLength = 3;
        usage = "<id> <name> <target> <- save region";
    }

    @Override
    public void execute(Stage stage) throws CommandException {
        String id = args.get(0);
        AreaInfo info = stage.getAreas().getAreaInfo(id);
        if (info == null) {
            throw new CommandException("&cその名前のエリアは存在しません！");
        }
        String savename = args.get(1);
        AreaInfo.RollbackData data = info.getRollback(savename);
        if (data == null) {
            data = info.addRollback(savename);
        }
        RollbackTarget target;
        try {
            target = RollbackTarget.valueOf(args.get(2).toUpperCase());
        } catch(IllegalArgumentException ex) {
            String types = Arrays.stream(RollbackTarget.values())
                    .map(p -> p.toString().toLowerCase())
                    .collect(Collectors.joining("/"));
            throw new CommandException("&cそのロールバック対象はサポートされていません！\n&c" + types, ex);
        }
        data.setTarget(target);
        try {
            data.setData(target.serialize(stage, stage.getAreas().getArea(id)));
        } catch (RollbackException ex) {
            throw new CommandException("&c領域の保存に失敗しました！", ex);
        }
        sendMessage("&a'&6" + stage.getName() + "&a'の'&6"
                    + id + "&a'エリアの'&6"
                    + savename + "&a'をセーブしました！");
    }

    @Override
    public boolean permission() {
        return Perms.STAGE_CONFIG_SET.has(player);
    }
    
}
