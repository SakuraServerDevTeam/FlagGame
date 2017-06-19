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
package syam.flaggame.command.game;

import java.util.List;
import java.util.UUID;
import jp.llv.flaggame.api.FlagGameAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.api.exception.CommandException;
import syam.flaggame.permission.Perms;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.api.stage.Stage;

/**
 *
 * @author Toyblocks
 */
public class GameCloseCommand extends BaseCommand {

    public GameCloseCommand(FlagGameAPI api) {
        super(
                api,
                false,
                1,
                "<game> [reason] <- close the reception",
                Perms.GAME_CLOSE,
                "close"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Reception reception = null;
        try {
            UUID uuid = UUID.fromString(args.get(0));
            reception = api.getReceptions().getReception(uuid).orElse(null);
        } catch (IllegalArgumentException ex) {
        }
        if (reception == null) {
            reception = api.getStages().getStage(args.get(0)).flatMap(Stage::getReception)
                    .orElseThrow(() -> new CommandException("&c受付'" + args.get(0) + "'が見つかりません！"));
        }
        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);

        if (reception.getState() == Reception.State.CLOSED) {
            throw new CommandException("&cその受付は既に破棄されています!");
        }
        reception.close(args.size() < 2 ? sender.getName() + "Closed" : args.get(1));
        gPlayer.sendMessage("&a成功しました!");
    }

}
