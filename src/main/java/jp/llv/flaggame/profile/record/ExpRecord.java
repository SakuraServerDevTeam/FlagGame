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

    public static final String FIELD_EXP = "exp";

    public ExpRecord(UUID game, double x, double y, double z, UUID player, long exp) {
        super(game, x, y, z, player);
        super.put(FIELD_EXP, exp);
    }

    public ExpRecord(UUID game, UUID player, Location location, long exp) {
        super(game, player, location);
        super.put(FIELD_EXP, exp);
    }

    public ExpRecord(UUID game, Player player, long exp) {
        super(game, player);
        super.put(FIELD_EXP, exp);
    }

    /*package*/ ExpRecord(Document base) {
        super(base);
    }

    /**
     * Gets actual experience point the player earned.
     *
     * @return actual experience point.
     */
    public double getExp() {
        return super.getDouble(FIELD_EXP);
    }

}
