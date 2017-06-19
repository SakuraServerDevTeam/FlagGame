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
package jp.llv.flaggame.profile.record;

import java.util.UUID;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author toyblocks
 */
public abstract class LocationRecord extends GameRecord {

    private final static String FIELD_LOCATION_X = "x";
    private final static String FIELD_LOCATION_Y = "y";
    private final static String FIELD_LOCATION_Z = "z";

    public LocationRecord(UUID game, double x, double y, double z) {
        super(game);
        super.put(FIELD_LOCATION_X, x);
        super.put(FIELD_LOCATION_Y, y);
        super.put(FIELD_LOCATION_Z, z);
    }

    public LocationRecord(UUID game, Location loc) {
        this(game, loc.getX(), loc.getY(), loc.getZ());
    }

    /*package*/ LocationRecord(Document base) {
        super(base);
    }

    public double getX() {
        return super.getDouble(FIELD_LOCATION_X);
    }

    public double getY() {
        return super.getDouble(FIELD_LOCATION_Y);
    }

    public double getZ() {
        return super.getDouble(FIELD_LOCATION_Z);
    }

    public Location getLocation(World world) {
        return new Location(world, getX(), getY(), getZ());
    }

}
