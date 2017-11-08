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

import java.util.List;
import jp.llv.flaggame.api.kit.Kit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 *
 * @author toyblocks
 */
public class KitSelectorInventoryHolder implements InventoryHolder {

    private final Player player;
    private final List<Kit> choices;
    private final int size;
    private final int page;

    public KitSelectorInventoryHolder(Player player, List<Kit> choices, int size, int page) {
        this.player = player;
        this.choices = choices;
        this.size = size;
        this.page = page;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Kit> getChoices() {
        return choices;
    }

    public int getSize() {
        return size;
    }

    public int getPage() {
        return page;
    }
    
    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException("This object is an data holder.");
    }
    
}
