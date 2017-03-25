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
package jp.llv.flaggame.profile.stats;

import java.util.UUID;
import jp.llv.flaggame.profile.record.RecordType;
import org.bson.Document;

/**
 *
 * @author toyblocks
 */
public class StageStat extends Document {
    
    public static final String FIELD_STAGE = "stage";
    public static final String FIELD_TYPE = RecordType.FIELD_TYPE;
    public static final String FIELD_COUNT = "count";
    public static final String FIELD_SUM = "sum";
    
    public StageStat(String stage, RecordType type, int count, double sum) {
        super.put(FIELD_STAGE, stage);
        super.put(FIELD_TYPE, type.toString().toLowerCase());
        super.put(FIELD_COUNT, count);
        super.put(FIELD_SUM, sum);
    }

    public StageStat(Document base) {
        super.putAll(base);
    }
    
    public String getStage() {
        return super.getString(FIELD_STAGE);
    }
    
    public RecordType getType() {
        return RecordType.valueOf(super.getString(FIELD_TYPE).toUpperCase());
    }
    
    public int getCount() {
        return super.getInteger(FIELD_COUNT);
    }
    
    public double getSum() {
        return super.getDouble(FIELD_SUM);
    }
    
}
