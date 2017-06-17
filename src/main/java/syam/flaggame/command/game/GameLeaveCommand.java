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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import jp.llv.flaggame.api.FlagGameAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;

import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import jp.llv.flaggame.api.reception.Reception;

public class GameLeaveCommand extends BaseCommand {

    public GameLeaveCommand(FlagGameAPI api) {
        super(
                api,
                true,
                0,
                "<- leave the game",
                "leave"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        // 参加しているゲームを取得する
        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);

        World world = player.getWorld();

        // ゲームに参加していないプレイヤー
        if (!gPlayer.getEntry().isPresent()) {
            // check permission
            if (!Perms.GAME_LEAVE_SPECTATE.has(sender)) {
                throw new CommandException("&cあなたはゲームに参加していません");
            }
            // ゲームワールド内
            if (world.equals(Bukkit.getWorld(api.getConfig().getGameWorld()))) {
                leaveFromGameworld(gPlayer, world.getSpawnLocation());
            } else {// 別ワールド
                throw new CommandException("&cこのゲームワールド外からこのコマンドを使うことはできません！");
            }
        } else {// ゲームに参加しているプレイヤー
            Reception reception = gPlayer.getEntry().get();
            switch (reception.getState().toGameState()) {
                case PREPARATION:
                    if (!Perms.GAME_LEAVE_READY.has(sender)) {
                        throw new CommandException("&cゲームのエントリーを取り消す権限がありません");
                    }
                    reception.leave(gPlayer);
                    gPlayer.sendMessage("&a" + reception.getID() + "のエントリーを取り消しました。");
                    break;
                case STARTED:
                    throw new CommandException("&cゲーム中に退場することはできません！");
                case FINISHED:
                    if (!Perms.GAME_LEAVE_FINISHED.has(sender)) {
                        throw new CommandException("&cゲームのエントリーを破棄する権限がありません");
                    }
                    reception.leave(gPlayer);
                    gPlayer.sendMessage("&a" + reception.getID() + "のエントリーを破棄しました。");
                    break;
            }
        }
    }

    private void leaveFromGameworld(GamePlayer gPlayer, Location def) {
        // プレイヤーデータに以前の座標が記録されていればその場所へTp
        if (gPlayer.tpBack()) {
            gPlayer.sendMessage("&aテレポートしました！");
        } else {
            gPlayer.getPlayer().teleport(def, TeleportCause.PLUGIN);
            gPlayer.sendMessage("&aゲームワールドのスポーン地点に戻りました！");
        }
    }
}
