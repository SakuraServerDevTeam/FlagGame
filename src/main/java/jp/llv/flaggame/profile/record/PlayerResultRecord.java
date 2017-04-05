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
import org.bson.BsonDouble;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author SakuraServerDev
 */
public abstract class PlayerResultRecord extends ExpRecord {
    
    public static final String FIELD_VIBE = "vibe";

    public PlayerResultRecord(UUID game, double x, double y, double z, UUID player, long exp, double vive) {
        super(game, x, y, z, player, exp);
        super.put(FIELD_VIBE, new BsonDouble(vive));
    }

    public PlayerResultRecord(UUID game, UUID player, Location location, long exp, double vive) {
        super(game, player, location, exp);
        super.put(FIELD_VIBE, new BsonDouble(vive));
    }

    public PlayerResultRecord(UUID game, Player player, long exp, double vive) {
        super(game, player, exp);
        super.put(FIELD_VIBE, new BsonDouble(vive));
    }

    /*package*/ PlayerResultRecord(Document base) {
        super(base);
    }
    
}
