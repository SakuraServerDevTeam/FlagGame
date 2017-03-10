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
package jp.llv.flaggame.profile.record;

import java.util.UUID;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 */
public abstract class ScoreRecord extends PlayerRecord {
    
    private static final String FIELD_SCORE = "score";
    
    public ScoreRecord(UUID game, double x, double y, double z, UUID player, double score) {
        super(game, x, y, z, player);
        super.put(FIELD_SCORE, score);
    }

    public ScoreRecord(UUID game, UUID player, Location location, double score) {
        super(game, player, location);
        super.put(FIELD_SCORE, score);
    }

    public ScoreRecord(UUID game, Player player, double score) {
        super(game, player);
        super.put(FIELD_SCORE, score);
    }

    /*package*/ ScoreRecord(Document base) {
        super(base);
    }
    
    public double getScore() {
        return super.getDouble(FIELD_SCORE);
    }
    
}
