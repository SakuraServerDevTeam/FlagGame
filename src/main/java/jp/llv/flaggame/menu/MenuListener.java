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
package jp.llv.flaggame.menu;

import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.menu.Button;
import jp.llv.flaggame.api.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 *
 * @author toyblocks
 */
public class MenuListener implements Listener {

    private final FlagGameAPI api;

    /*package*/ MenuListener(FlagGameAPI api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof MenuInventoryHolder) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            Menu menu = ((MenuInventoryHolder) event.getInventory().getHolder()).getMenu();
            if (event.getRawSlot() < 0 || menu.size() <= event.getRawSlot()) {
                return;
            }
            Button button = menu.getButton(event.getRawSlot());
            if (button.isAsync()) {
                api.getServer().getScheduler().runTaskAsynchronously(api.getPlugin(), ()
                        -> click(button, player, event.getClick())
                );
            } else {
                click(button, player, event.getClick());
            }
        }
    }

    private void click(Button button, Player player, ClickType type) {
        if (button.click(player, type)) {
            player.closeInventory();
        }
    }

}
