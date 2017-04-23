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
package syam.flaggame.command.fest;

import java.util.List;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import jp.llv.flaggame.util.FlagTabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.player.SetupSession;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalSelectCommand extends BaseCommand {
    
    public FestivalSelectCommand(FlagGame plugin) {
        super(
                plugin,
                true,
                0, 
                "[festival] <- select an existing festival", 
                Perms.FESTIVAL_SELECT,
                FlagTabCompleter.builder()
                .forArg(1).suggestList((p, s, a) -> p.getFestivals().getFestivals().keySet())
                .create(),
                "select", 
                "sel"
        );
    }
    
    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer gPlayer = plugin.getPlayers().getPlayer(player);
        if (args.size() >= 1) {
            FestivalSchedule festival = plugin.getFestivals().getFestival(args.get(0))
                    .orElseThrow(() -> new CommandException("&フェス'" + args.get(0) + "'が見つかりません！"));
            
            if (gPlayer.getSetupSession().map(SetupSession::getSelected).orElse(null) != festival) {
                gPlayer.createSetupSession(festival);
            }

            sendMessage(sender, "&aステージ'&6" + festival.getName() + "&a'を選択しました！");
        } else {
            gPlayer.getSetupSession().orElseThrow(() -> new CommandException("&cあなたはステージを選択していません！"));
            gPlayer.destroySetupSession();
            Actions.message(player, "&aフェスの選択を解除しました！");
        }
    }
    
}
