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
package syam.flaggame.command.stage;

import java.util.List;
import jp.llv.flaggame.profile.record.StageRateRecord;
import jp.llv.flaggame.reception.GameReception;
import syam.flaggame.FlagGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author SakuraServerDev
 */
public class StageRateCommand extends BaseCommand {
    
    public StageRateCommand(FlagGame plugin) {
        super(
                plugin,
                true,
                1,
                "<rate> <- rate the stage",
                Perms.STAGE_RATE,
                "rate"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
        if (!gplayer.getEntry().isPresent()) {
            throw new CommandException("&c評価対象に参加していません！");
        }
        GameReception reception = gplayer.getEntry().get();
        if (reception.getState() != GameReception.State.FINISHED) {
            throw new CommandException("&c評価対象が評価段階ではありません！");
        }
        int rate = -1;
        try {
            rate = Integer.parseInt(args.get(0));
        } catch(NumberFormatException ex) {
        }
        if (rate < 0 || 5 < rate) {
            throw new CommandException("&c0-5の整数値で評価してください！");
        }
        reception.getRecordStream().push(new StageRateRecord(reception.getID(), player, plugin.getConfigs().getScoreRate(), rate));
        reception.leave(gplayer);
        sendMessage(sender, "&a投票への協力ありがとうございました！");
    }
    
}