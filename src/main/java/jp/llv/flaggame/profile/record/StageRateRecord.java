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
import org.bson.BsonInt32;
import org.bson.Document;
import org.bukkit.entity.Player;
import syam.flaggame.ConfigurationManager;

/**
 *
 * @author toyblocks
 */
public class StageRateRecord extends ExpRecord {

    private static final String FIELD_RATE = "rate";
    
    public StageRateRecord(UUID game, double x, double y, double z, UUID player, double exp, int rate) {
        super(game, x, y, z, player, exp);
        super.put(FIELD_RATE, new BsonInt32(rate));
    }

    public StageRateRecord(UUID game, Player player, double exp, int rate) {
        super(game, player, exp);
        super.put(FIELD_RATE, new BsonInt32(rate));
    }

    public StageRateRecord(Document base) {
        super(base);
    }

    @Override
    public RecordType getType() {
        return RecordType.RATE;
    }

    @Override
    public double getExp(ConfigurationManager config) {
        return config.getScoreRate();
    }
    
}
