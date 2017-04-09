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
package jp.llv.flaggame.game.basic;

import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import syam.flaggame.FlagGame;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class BGInventoryListener extends BGListener {

    private final FlagGame plugin;
    private final Collection<GamePlayer> players;

    public BGInventoryListener(FlagGame plugin, BasicGame game) {
        super(game);
        this.plugin = plugin;
        this.players = game.getReception().getPlayers();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    @SuppressWarnings("deprecation")
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() != InventoryType.SlotType.ARMOR || event.getSlot() != 39) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);

        if (!this.players.contains(gplayer)) {
            return;
        }

        event.setCurrentItem(
                new ItemStack(Material.WOOL, 1, (short) 0, gplayer.getTeam().get().getColor().getBlockData())
        );
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
    }

}
