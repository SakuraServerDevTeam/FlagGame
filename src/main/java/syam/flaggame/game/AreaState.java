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
package syam.flaggame.game;

import org.bukkit.ChatColor;

/**
 *
 * @author toyblocks
 */
public enum AreaState {
    TRUE(true, true, ChatColor.GREEN),
    FALSE(false, true, ChatColor.RED),
    DEFAULT(false, false, ChatColor.GOLD),;

    private final ChatColor color;
    private final boolean positive;
    private final boolean forceful;

    private AreaState(boolean positive, boolean forceful, ChatColor color) {
        this.positive = positive;
        this.forceful = forceful;
        this.color = color;
    }

    public boolean isPositive() {
        return positive;
    }

    public boolean isForceful() {
        return forceful;
    }

    public String format() {
        return color + toString().toLowerCase();
    }
    
    public static AreaState of(String name) {
        return valueOf(name.toUpperCase());
    }

}
