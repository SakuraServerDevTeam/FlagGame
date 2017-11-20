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

import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.session.Reservable;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.api.exception.CommandException;
import syam.flaggame.permission.Perms;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.StageSetupSession;
import jp.llv.flaggame.api.stage.Stage;

/**
 *
 * @author toyblocks
 */
public abstract class AreaCommand extends BaseCommand {

    public AreaCommand(FlagGameAPI api, int argLength, String usage, Perms permission, String name, String... aliases) {
        super(api, true, argLength, usage, permission, name, aliases);
    }

    public AreaCommand(FlagGameAPI api, int argLength, String usage, String name, String... aliases) {
        super(api, true, argLength, usage, name, aliases);
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        GamePlayer gp = api.getPlayers().getPlayer(player);
        Reservable selected = gp.getSetupSession(StageSetupSession.class)
                .orElseThrow(() -> new CommandException("&c先に編集するゲームを選択してください"))
                .getReserved();
        if (!(selected instanceof Stage)) {
            throw new CommandException("&cあなたはステージを選択していません！");
        }
        this.execute(args, player, (Stage) selected);
    }

    public abstract void execute(List<String> args, Player player, Stage stage) throws CommandException;

}
