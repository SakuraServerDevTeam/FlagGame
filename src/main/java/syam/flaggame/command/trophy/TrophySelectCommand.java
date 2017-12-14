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
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;
import jp.llv.flaggame.api.trophy.Trophy;
import jp.llv.flaggame.api.player.TrophySetupSession;
import jp.llv.flaggame.util.FlagTabCompleter;

/**
 *
 * @author toyblocks
 */
public class TrophySelectCommand extends BaseCommand {

    public TrophySelectCommand(FlagGameAPI api) {
        super(api,
                true,
                0,
                "[trophy] <- select a trophy",
                Perms.TROPHY_SELECT,
                FlagTabCompleter.builder()
                .forArg(0).suggestList((a, s, p) -> a.getTrophies().getNormalTrophies())
                .create(),
                "select",
                "sel"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        if (args.size() >= 1) {
            Trophy trophy = this.api.getTrophies().getTrophy(args.get(0))
                    .orElseThrow(() -> new CommandException("&cトロフィー'" + args.get(0) + "'が見つかりません！"));

            // 既に選択中のトロフィーと同じトロフィーでない限りはセッションを作成
            if (gPlayer.getSetupSession(TrophySetupSession.class).map(TrophySetupSession::getReserved).orElse(null) != trophy) {
                gPlayer.createSetupSession(trophy, TrophySetupSession.class);
            }
            Actions.message(player, "&トロフィー'&6" + trophy.getName() + "&a'を選択しました！");
        } else {
            gPlayer.getSetupSession(TrophySetupSession.class).orElseThrow(() -> new CommandException("&cあなたはトロフィーを選択していません！"));
            gPlayer.destroySetupSession(TrophySetupSession.class);
            Actions.message(player, "&aトロフィーの選択を解除しました！");
        }
    }

}
