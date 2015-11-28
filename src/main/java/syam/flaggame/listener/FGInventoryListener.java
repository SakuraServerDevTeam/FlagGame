/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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

import java.util.logging.Logger;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

import syam.flaggame.FlagGame;
import syam.flaggame.player.GamePlayer;

/**
 * FGInventoryListener (FGInventoryListener.java)
 *
 * @author syam(syamn)
 */
public class FGInventoryListener implements Listener {

    public static final Logger log = FlagGame.logger;
    private static final String logPrefix = FlagGame.logPrefix;
    private static final String msgPrefix = FlagGame.msgPrefix;

    private final FlagGame plugin;

    public FGInventoryListener(final FlagGame plugin) {
        this.plugin = plugin;
    }

    /* 登録するイベントはここから下に */
    // プレイヤーがインベントリをクリックした
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        // getSlot() == 39: 装備(頭)インベントリ
        if (event.getSlotType() != SlotType.ARMOR || event.getSlot() != 39) {
            return;
        }

        // プレイヤーインスタンスを持たなければ返す
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        GamePlayer player = plugin.getPlayers().getPlayer((Player) event.getWhoClicked());

        player.getTeam().ifPresent(team -> {
            event.setCurrentItem(new ItemStack(Material.WOOL, 1, (short) 0, team.getColor().getBlockData()));
            event.setCancelled(true);
            event.setResult(Result.DENY);
        });
    }
}
