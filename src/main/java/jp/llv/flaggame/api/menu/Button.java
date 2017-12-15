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

import java.util.Objects;
import java.util.function.BiPredicate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author toyblocks
 */
public final class Button {
    
    private final boolean async;
    private final ItemStack icon;
    private final BiPredicate<Player, ClickType> listener;

    public Button(ItemStack icon, BiPredicate<Player, ClickType> listener, boolean async) {
        this.icon = Objects.requireNonNull(icon);
        this.listener = Objects.requireNonNull(listener);
        this.async = async;
    }
    
    public Button(Material icon, String name, BiPredicate<Player, ClickType> listener, boolean async) {
        this(new ItemStack(icon), listener, async);
        ItemMeta meta = this.icon.getItemMeta();
        meta.setDisplayName(name);
        this.icon.setItemMeta(meta);
    }

    public ItemStack getIcon() {
        return icon;
    }
    
    public boolean click(Player player, ClickType type) {
        return listener.test(player, type);
    }

    public boolean isAsync() {
        return async;
    }
    
}
