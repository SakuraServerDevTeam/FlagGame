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
package syam.flaggame.command.trophy;

import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.TrophySetupSession;
import jp.llv.flaggame.api.trophy.Trophy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author toyblocks
 */
public class TrophyInitCommand extends BaseCommand {

    public TrophyInitCommand(FlagGameAPI api) {
        super(api,
                true,
                0,
                "<- select an intial trophy",
                Perms.TROPHY_SELECT,
                "initial",
                "init"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        Trophy trophy = this.api.getTrophies().getInitialTrophy();

        // 既に選択中のトロフィーと同じトロフィーでない限りはセッションを作成
        if (gPlayer.getSetupSession(TrophySetupSession.class).map(TrophySetupSession::getReserved).orElse(null) != trophy) {
            gPlayer.createSetupSession(trophy, TrophySetupSession.class);
        }
        Actions.message(player, "&初期トロフィーを選択しました！");
    }

}
