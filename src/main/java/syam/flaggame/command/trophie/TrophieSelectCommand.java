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
package syam.flaggame.command.trophie;

import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.TrophieSetupSession;
import jp.llv.flaggame.api.trophie.Trophie;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author toyblocks
 */
public class TrophieSelectCommand extends BaseCommand {

    public TrophieSelectCommand(FlagGameAPI api) {
        super(
                api,
                true,
                0,
                "[trophie] <- select a trophie",
                Perms.TROPHIE_SELECT,
                "select",
                "sel"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        if (args.size() >= 1) {
            Trophie trophie = this.api.getTrophies().getTrophie(args.get(0))
                    .orElseThrow(() -> new CommandException("&cトロフィー'" + args.get(0) + "'が見つかりません！"));

            // 既に選択中のトロフィーと同じトロフィーでない限りはセッションを作成
            if (gPlayer.getSetupSession(TrophieSetupSession.class).map(TrophieSetupSession::getReserved).orElse(null) != trophie) {
                gPlayer.createSetupSession(trophie, TrophieSetupSession.class);
            }
            Actions.message(player, "&トロフィー'&6" + trophie.getName() + "&a'を選択しました！");
        } else {
            gPlayer.getSetupSession(TrophieSetupSession.class).orElseThrow(() -> new CommandException("&cあなたはトロフィーを選択していません！"));
            gPlayer.destroySetupSession(TrophieSetupSession.class);
            Actions.message(player, "&aトロフィーの選択を解除しました！");
        }
    }

}
