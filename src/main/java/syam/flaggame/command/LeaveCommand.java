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

import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.reception.GameReception;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import syam.flaggame.FlagGame;

import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;

public class LeaveCommand extends BaseCommand {

    public LeaveCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = true;
        name = "leave";
        argLength = 0;
        usage = "<leave> <- leave the game";
    }

    @Override
    public void execute() throws CommandException {
        // 参加しているゲームを取得する
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        

        World world = player.getWorld();

        // ゲームに参加していないプレイヤー
        if (!gPlayer.getEntry().isPresent()) {
            // check permission
            if (!Perms.LEAVE_SPECTATE.has(sender)) {
                throw new CommandException("&cあなたはゲームに参加していません");
            }
            // ゲームワールド内
            if (world.equals(Bukkit.getWorld(plugin.getConfigs().getGameWorld()))) {
                leaveFromGameworld(gPlayer, world.getSpawnLocation());
            } else {// 別ワールド
                throw new CommandException("&cこのゲームワールド外からこのコマンドを使うことはできません！");
            }
        } else {// ゲームに参加しているプレイヤー
            GameReception reception = gPlayer.getEntry().get();
            if (reception.getState().toGameState() == Game.State.PREPARATION) {
                if (!Perms.LEAVE_READY.has(sender)) {
                    throw new CommandException("&cゲームのエントリーを取り消す権限がありません");
                }
                reception.leave(gPlayer);
                gPlayer.sendMessage("&a"+reception.getID()+"のエントリーを取り消しました。");
            } else {
                    throw new CommandException("&cThat function is not implemented in alpha-build");
            }
        }
    }

    private void leaveFromGameworld(GamePlayer gPlayer, Location def) {
        // プレイヤーデータに以前の座標が記録されていればその場所へTp
        if (gPlayer.tpBack()) {
            gPlayer.sendMessage("&aテレポートしました！");
        } else {
            player.teleport(def, TeleportCause.PLUGIN);
            gPlayer.sendMessage("&aゲームワールドのスポーン地点に戻りました！");
        }
    }

    @Override
    public boolean permission() {
        return (Perms.LEAVE_GAME.has(sender) || Perms.LEAVE_READY.has(sender) || Perms.LEAVE_SPECTATE.has(sender));
    }
}
