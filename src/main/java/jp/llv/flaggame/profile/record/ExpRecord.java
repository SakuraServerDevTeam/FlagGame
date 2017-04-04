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
import org.bukkit.entity.Player;

/**
 *
 * @author SakuraServerDev
 */
public abstract class ExpRecord extends PlayerRecord {
    
    private static final String FIELD_EXP = "exp";
    
    public ExpRecord(UUID game, double x, double y, double z, UUID player, double exp) {
        super(game, x, y, z, player);
        super.put(FIELD_EXP, exp);
    }

    public ExpRecord(UUID game, UUID player, Location location, double exp) {
        super(game, player, location);
        super.put(FIELD_EXP, exp);
    }

    public ExpRecord(UUID game, Player player, double exp) {
        super(game, player);
        super.put(FIELD_EXP, exp);
    }

    /*package*/ ExpRecord(Document base) {
        super(base);
    }
    
    public double getExp() {
        return super.getDouble(FIELD_EXP);
    }
    
}
