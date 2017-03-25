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

/**
 *
 * @author toyblocks
 */
public class GameStartRecord extends GameRecord {

    public static final String FIELD_STAGE = "stage";
    
    public GameStartRecord(UUID game, String stage) {
        super(game);
        super.put(FIELD_STAGE, stage);
    }

    /*package*/ GameStartRecord(Document base) {
        super(base);
    }
    
    public String getStage() {
        return super.getString(FIELD_STAGE);
    }

    @Override
    public RecordType getType() {
        return RecordType.GAME_START;
    }
    
}
