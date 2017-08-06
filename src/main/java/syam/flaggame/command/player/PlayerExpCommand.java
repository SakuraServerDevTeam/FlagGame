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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.util.OnelineBuilder;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.profile.PlayerProfile;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class PlayerExpCommand extends BaseCommand {

    public PlayerExpCommand(FlagGameAPI api) {
        super(
                api,
                false,
                0,
                "[player] <- show specified player's exp",
                "exp"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Player target = null;
        if (args.size() >= 1) {
            Perms.PLAYER_EXP_OTHER.requireTo(sender);
            target = api.getServer().getPlayer(args.get(0));
        } else if (player != null) {
            Perms.PLAYER_EXP_SELF.requireTo(sender);
            target = player;
        }
        if (target == null) {
            throw new CommandException("&cプレイヤーを指定してください！");
        }
        PlayerProfile profile = api.getProfiles().getProfile(target.getUniqueId());
        OnelineBuilder.newBuilder()
                .value(target.getName()).info("のレベルは")
                .value(profile.getLevel().orElseThrow(() -> new CommandException("&c対象のプロファイルがが読み込まれていません！")))
                .info("で、次のレベルまでの必要経験値は")
                .value(profile.getExpRequiredToLevelUp().orElseThrow(() -> new CommandException("&c対象のプロファイルがが読み込まれていません！")))
                .info("です！").sendTo(sender);
    }

}
