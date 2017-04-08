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
import jp.llv.flaggame.reception.GameReception;
import syam.flaggame.FlagGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class GameCloseCommand extends BaseCommand {

    public GameCloseCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                1,
                "<game> [reason] <- close the reception",
                Perms.GAME_CLOSE,
                "close"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        GameReception reception = null;
        try {
            UUID uuid = UUID.fromString(args.get(0));
            reception = plugin.getReceptions().getReception(uuid).orElse(null);
        } catch(IllegalArgumentException ex) {
        }
        if (reception == null) {
            reception = plugin.getStages().getStage(args.get(0)).flatMap(Stage::getReception)
                    .orElseThrow(() -> new CommandException("&c受付'" + args.get(0) + "'が見つかりません！"));
        }
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        
        if (reception.getState()==GameReception.State.CLOSED) {
            throw new CommandException("&cその受付は既に破棄されています!");
        }
        reception.close(args.size() < 2 ? sender.getName()+"Closed":args.get(1));
        gPlayer.sendMessage("&a成功しました!");
    }
    
}
