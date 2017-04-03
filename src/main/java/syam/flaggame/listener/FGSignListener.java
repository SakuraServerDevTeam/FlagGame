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
package syam.flaggame.listener;

import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.reception.TeamColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import syam.flaggame.FlagGame;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author toyblocks
 */
public class FGSignListener implements Listener {

    private final FlagGame plugin;

    public FGSignListener(FlagGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getState() instanceof Sign)) {
            return;
        }
        Sign sign = (Sign) block.getState();
        if (!sign.getLine(0).equals("§a[FlagGame]")) {
            return;
        }
        Player player = event.getPlayer();
        switch (sign.getLine(1).toLowerCase()) {
            case "heal":
                onClickAtHealSign(player, sign);
                return;
            case "kill":
                onClickAtKillSign(player, sign);
                return;
            case "stage":
                onClickAtStageSign(player, sign);
                return;
            case "join":
                onClickAtJoinSign(player, sign);
                return;
            case "watch":
                onClickAtWatchSign(player, sign);
                return;
        }
        Actions.sendPrefixedMessage(player, "&cThis sign is broken! Please contact server staff!");
    }

    private void onClickAtHealSign(Player player, Sign sign) {
        String line3 = sign.getLine(2);
        if (!"".equals(line3) && !line3.isEmpty()) {//特定チーム限定
            TeamColor signTeam;

            try {
                signTeam = TeamColor.valueOf(line3.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                Actions.message(player, "&cThis sign is broken! Please contact server staff!");
                return;
            }

            GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
            if (!gplayer.getTeam().isPresent()) {
                Actions.message(player, "&cこの看板はフラッグゲーム中にのみ使うことができます");
                return;
            }

            if (gplayer.getTeam().get().getColor() != signTeam) {
                Actions.message(player, "&cこれはあなたのチームの看板ではありません！");
                return;
            }
        }

        // 20以上にならないように体力とお腹ゲージを+2(ハート、おにく1つ分)回復させる
        double nowHP = player.getHealth();
        nowHP = nowHP + 2;
        if (nowHP > 20) {
            nowHP = 20;
        }

        int nowFL = player.getFoodLevel();
        nowFL = nowFL + 2;
        if (nowFL > 20) {
            nowFL = 20;
        }

        // プレイヤーにセット
        player.setHealth(nowHP);
        player.setFoodLevel(nowFL);
        player.setFireTicks(0); // 燃えてれば消してあげる

        Actions.sendPrefixedMessage(player, ChatMessageType.ACTION_BAR, "&aHealed!");
    }

    private void onClickAtKillSign(Player player, Sign sign) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
        if (gplayer.getTeam().isPresent()) {
            Game game = gplayer.getGame().get();
            GamePlayer.sendMessage(game, "&6[" + game.getName() + "]&6 '" + gplayer.getColoredName() + "&6'が自殺しました。");
        }
        player.setHealth(0);
        player.setFoodLevel(0);
    }

    private void onClickAtStageSign(Player player, Sign sign) {
        plugin.getServer().dispatchCommand(player, "flaggame:flag stage info " + sign.getLine(2));
    }

    private void onClickAtJoinSign(Player player, Sign sign) {
        plugin.getServer().dispatchCommand(player, "flaggame:flag join " + sign.getLine(2));
    }

    private void onClickAtWatchSign(Player player, Sign sign) {
        plugin.getServer().dispatchCommand(player, "flaggame:flag watch " + sign.getLine(2));
    }

}
