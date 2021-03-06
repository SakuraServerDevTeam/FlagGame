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
package jp.llv.flaggame.api.stage.objective;

import jp.llv.flaggame.api.reception.TeamColor;
import org.bukkit.Location;

/**
 *
 * @author toyblocks
 */
public class Spawn extends StageObjective {

    private final TeamColor color;

    public Spawn(Location location, TeamColor color) {
        super(location, ObjectiveType.SPAWN, false);
        this.color = color;
    }
    
    public TeamColor getColor() {
        return color;
    }
    
}
