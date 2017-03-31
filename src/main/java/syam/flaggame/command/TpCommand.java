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
package syam.flaggame.command;

import java.util.List;
import org.bukkit.Location;
import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jp.llv.flaggame.reception.TeamColor;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.player.SetupSession;
import syam.flaggame.util.Actions;

public class TpCommand extends BaseCommand {

    public TpCommand(FlagGame plugin) {
        super(
                plugin,
                true,
                1,
                "<area> [team] [game] <- tp to specific location",
                "tp"
        );
    
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        if (args.get(0).equalsIgnoreCase("spawn")) {
            if (args.size() < 2) {
                throw new CommandException("&c引数が足りません！");
            }
            
            GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
            
            // ゲーム取得
            Stage stage = null;
            // 引数からゲーム取得
            if (args.size() >= 3) {
                stage = this.plugin.getStages().getStage(args.get(2)).orElse(null);
            }

            // 取れなかった場合選択済みゲームを取得
            if (stage == null) {
                stage = gPlayer.getSetupSession().map(SetupSession::getSelectedStage)
                        .orElseThrow(() -> new CommandException("&c先にゲームを選択してください"));
            }

            // チーム取得
            TeamColor team = null;
            for (TeamColor tm : TeamColor.values()) {
                if (tm.name().toLowerCase().equalsIgnoreCase(args.get(1))) {
                    team = tm;
                    break;
                }
            }
            if (team == null) {
                throw new CommandException("&cチーム'" + args.get(1) + "'が見つかりません！");
            }

            Location loc = stage.getSpawn(team);

            if (loc == null) {
                throw new CommandException("&c" + team.getTeamName() + "チームのスポーン地点は未設定です！");
            }

            // テレポート
            player.teleport(loc);
            Actions.message(player, "&a" + team.getTeamName() + "チームのスポーン地点にテレポートしました！");
        } else {
            Actions.message(player, "&cそのエリアは未定義です");
        }
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.TP.has(target);
    }
}
