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
package jp.llv.flaggame.api.menu;

import java.util.List;
import jp.llv.flaggame.menu.InventoryMenu;
import jp.llv.flaggame.menu.PageableInventoryMenu;
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 */
public interface MenuAPI {

    InventoryMenu createMenu(String title, List<Button> buttons);

    PageableInventoryMenu createPageableMenu(String title, List<Button> buttons);

    void openMenu(Player player, Menu menu);
    
}
