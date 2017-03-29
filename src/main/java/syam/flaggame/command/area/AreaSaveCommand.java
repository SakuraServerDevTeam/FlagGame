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
import java.util.logging.Level;
import java.util.stream.Collectors;
import jp.llv.flaggame.rollback.SerializeTask;
import jp.llv.flaggame.rollback.StageData;
import jp.llv.flaggame.rollback.StageDataType;
import jp.llv.flaggame.util.ConvertUtils;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

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
        StageDataType target;
        try {
            target = StageDataType.valueOf(args.get(2).toUpperCase());
        } catch (IllegalArgumentException ex) {
            String types = Arrays.stream(StageDataType.values())
                    .map(p -> p.toString().toLowerCase())
                    .collect(Collectors.joining("/"));
            throw new CommandException("&cそのロールバック対象はサポートされていません！\n&c" + types, ex);
        }
        if (target == StageDataType.NONE) {
            info.removeRollback(savename);
            sendMessage("&a'&6" + stage.getName() + "&a'の'&6"
                        + id + "&a'エリアの'&6"
                        + savename + "&a'を削除しました！");
            return;
        }
        StageData structure = target.newInstance();
        data.setTarget(structure);
        final Player playerFinal = player;
        SerializeTask task = structure.save(plugin, stage, stage.getAreas().getArea(id), ex -> {
            if (ex == null && playerFinal.isOnline()) {
                Actions.sendPrefixedMessage(sender, "&a'&6" + stage.getName() + "&a'の'&6"
                                                    + id + "&a'エリアの'&6"
                                                    + savename + "&a'をセーブしました！");
            } else if (ex != null && playerFinal.isOnline()) {
                Actions.sendPrefixedMessage(sender, "&c'&6" + stage.getName() + "&c'の'&6"
                                                    + id + "&c'エリアの'&6"
                                                    + savename + "&c'のセーブに失敗しました！");
                plugin.getLogger().log(Level.WARNING, "Failed to save stage area", ex);
            }
        });
        String etr = Actions.getTimeString(ConvertUtils.toMiliseconds(task.getEstimatedTickRemaining()));
        Actions.sendPrefixedMessage(sender, "&a'&6" + stage.getName() + "&a'の'&6"
                                            + id + "&a'エリアの'&6"
                                            + savename + "&a'をセーブしています...");
        Actions.sendPrefixedMessage(sender, "&aこれにはおよそ"+etr+"間かかる予定です...");
        task.start(plugin);
    }

    @Override
    public boolean permission() {
        return Perms.STAGE_CONFIG_SET.has(sender);
    }

}
