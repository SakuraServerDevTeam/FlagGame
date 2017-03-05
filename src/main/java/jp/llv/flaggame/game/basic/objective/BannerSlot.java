/* 
 * Copyright (C) 2017 Toyblocks, SakuraServerDev
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
package jp.llv.flaggame.game.basic.objective;

import org.bukkit.Location;
import syam.flaggame.enums.TeamColor;

/**
 *
 * @author Toyblocks
 */
public class BannerSlot {
    
    private final Location loc;
    private final TeamColor color;

    public BannerSlot(Location loc, TeamColor color) {
        this.loc = loc;
        this.color = color;
    }

    public Location getLocation() {
        return loc;
    }

    public TeamColor getColor() {
        return color;
    }
    
}
