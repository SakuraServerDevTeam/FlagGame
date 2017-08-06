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
package jp.llv.flaggame.api.stage.objective;

import org.bukkit.Location;
import jp.llv.flaggame.reception.TeamColor;

/**
 *
 * @author Toyblocks
 */
public class Nexus extends StageObjective {

    private final TeamColor color;
    private final double point;

    public Nexus(Location loc, TeamColor color, double point) {
        super(loc, ObjectiveType.NEXUS, true);
        this.color = color;
        this.point = point;
    }

    public TeamColor getColor() {
        return color;
    }

    public double getPoint() {
        return point;
    }

}
