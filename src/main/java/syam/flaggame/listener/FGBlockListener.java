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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;

import syam.flaggame.FlagGame;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

public class FGBlockListener implements Listener {

    private final FlagGame plugin;

    public FGBlockListener(final FlagGame plugin) {
        this.plugin = plugin;
    }

    /* 登録するイベントはここから下に */
    /**
     * ブロックを破壊した
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (onBlockChange(event, event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    /**
     * ブロックを設置した
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (onBlockChange(event, event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    // フラッグが壊されたか取得した
    public boolean onBlockChange(final BlockEvent event, final Player player) {
        final Block block = event.getBlock();
        // ゲーム用ワールドでなければ返す
        if (block.getWorld() != Bukkit.getWorld(plugin.getConfigs().getGameWorld())) {
            return false;
        }
        
        if (Perms.IGNORE_PROTECT.has(player)) {
            return false;
        }
        
        Block b = event.getBlock();
        Location loc = b.getLocation();
        for (Stage stage : plugin.getStages()) {
            if (stage.getAreas().hasStageArea() && stage.getAreas().getStageArea().contains(loc) && stage.isProtected()) {
                return true;
            }
        }

        return plugin.getConfigs().isProtected();
    }

    // 看板設置
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignChange(final SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockState state = event.getBlock().getState();

        if (state instanceof Sign) {
            Sign sign = (Sign) state;

            /* 特殊看板設置 */
            if (event.getLine(0).toLowerCase().contains("[flaggame]")) {
                // 権限チェック
                if (!Perms.SIGN.has(player)) {
                    event.setLine(0, "Denied!");
                    Actions.message(player, "&cYou don't have permission to do this!");
                    return;
                }
                event.setLine(0, "§a[FlagGame]");
            }
        }
    }

    /* 以下ワールド保護 */
    // 葉の消滅を抑制
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeavesDecay(final LeavesDecayEvent event) {
        // ゲーム用ワールドでなければ返す
        if (event.getBlock().getWorld() != Bukkit.getWorld(plugin.getConfigs().getGameWorld())) {
            return;
        }

        // ワールド保護チェック
        if (plugin.getConfigs().isProtected()) {
            event.setCancelled(true);
        }
    }

    // 氷 → 水 抑制
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFade(final BlockFadeEvent event) {
        // ゲーム用ワールドでなければ返す
        if (event.getBlock().getWorld() != Bukkit.getWorld(plugin.getConfigs().getGameWorld())) {
            return;
        }

        // ワールド保護チェック
        if (plugin.getConfigs().isProtected()) {
            event.setCancelled(true);
        }
    }

    // 水 → 氷 抑制
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockForm(final BlockFormEvent event) {
        // ゲーム用ワールドでなければ返す
        if (event.getBlock().getWorld() != Bukkit.getWorld(plugin.getConfigs().getGameWorld())) {
            return;
        }

        // ワールド保護チェック
        if (plugin.getConfigs().isProtected()) {
            event.setCancelled(true);
        }
    }
}
