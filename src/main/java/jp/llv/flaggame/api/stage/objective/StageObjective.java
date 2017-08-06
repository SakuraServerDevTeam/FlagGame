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

/**
 *
 * @author SakuraServerDev
 */
public abstract class StageObjective {

    private final String name;
    private final Location location;
    private final ObjectiveType type;
    private final boolean block;

    public StageObjective(String name, Location location, ObjectiveType type, boolean block) {
        this.name = name;
        this.location = location.clone();
        this.type = type;
        this.block = block;
    }

    public StageObjective(Location location, ObjectiveType type, boolean block) {
        this.name = type.getName();
        this.location = location;
        this.type = type;
        this.block = block;
    }

    public final String getName() {
        return name;
    }

    public final Location getLocation() {
        return location.clone();
    }

    public final ObjectiveType getType() {
        return type;
    }

    public final boolean isBlock() {
        return block;
    }

}
