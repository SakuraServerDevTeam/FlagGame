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
package syam.flaggame.command.player;

import java.util.List;
import jp.llv.flaggame.profile.PlayerProfile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.util.OnelineBuilder;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class PlayerVibeCommand extends BaseCommand {

    public PlayerVibeCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                0,
                "[player] <- show specified player's vibe",
                "vibe"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Player target = null;
        if (args.size() >= 1) {
            Perms.PLAYER_VIBE_OTHER.requireTo(sender);
            target = plugin.getServer().getPlayer(args.get(0));
        } else if (player != null) {
            Perms.PLAYER_VIBE_SELF.requireTo(sender);
            target = player;
        }
        if (target == null) {
            throw new CommandException("&cプレイヤーを指定してください！");
        }
        PlayerProfile profile = plugin.getProfiles().getProfile(target.getUniqueId());
        OnelineBuilder.newBuilder()
                .value(target.getName()).info("のチョーシは")
                .value(profile.getVibe().orElseThrow(() -> new CommandException("&c対象のプロファイルがが読み込まれていません！")))
                .info("です！").sendTo(sender);
    }

}
