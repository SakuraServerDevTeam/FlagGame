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

import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.menu.Button;
import jp.llv.flaggame.api.menu.Menu;
import jp.llv.flaggame.api.menu.PageableMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import jp.llv.flaggame.api.menu.MenuAPI;

/**
 *
 * @author toyblocks
 */
public class MenuManager implements MenuAPI {

    private final FlagGameAPI api;

    public MenuManager(FlagGameAPI api) {
        this.api = api;
        api.getServer().getPluginManager().registerEvents(new MenuListener(api), api.getPlugin());
    }

    @Override
    public InventoryMenu createMenu(String title, List<Button> buttons) {
        return new InventoryMenu(title, buttons);
    }

    @Override
    public PageableInventoryMenu createPageableMenu(String title, List<Button> buttons) {
        return new PageableInventoryMenu(this, title, buttons);
    }

    @Override
    public void openMenu(Player player, Menu menu) {
        Menu target = menu instanceof PageableMenu ? ((PageableMenu) menu).getPage(0) : menu;
        MenuInventoryHolder holder = new MenuInventoryHolder(player, menu);
        Inventory inventory = api.getServer().createInventory(holder, target.size());
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getButton(i) == null) {
                continue;
            }
            inventory.setItem(i, menu.getButton(i).getIcon());
        }
        player.openInventory(inventory);
    }

}
