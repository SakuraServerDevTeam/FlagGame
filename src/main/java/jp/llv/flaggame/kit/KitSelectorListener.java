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
package jp.llv.flaggame.kit;

import jp.llv.flaggame.api.kit.Kit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 *
 * @author toyblocks
 */
public class KitSelectorListener implements Listener {
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof KitSelectorInventoryHolder) {
            event.setCancelled(true);
            
            KitSelectorInventoryHolder holder = (KitSelectorInventoryHolder) event.getInventory().getHolder();
            int slot = event.getRawSlot();
            if (slot < 0 || holder.getSize() <= slot) {
                return;
            }
            int index = holder.getPage() * 54 + event.getRawSlot();
            Kit kit = holder.getChoices().get(index);
        }
    }
    
}
