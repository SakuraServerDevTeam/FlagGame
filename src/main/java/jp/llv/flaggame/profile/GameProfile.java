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
package jp.llv.flaggame.profile;

import java.util.LinkedList;
import jp.llv.flaggame.profile.record.GameRecord;
import java.util.Queue;
import java.util.UUID;

/**
 *
 * @author toyblocks
 */
public class GameProfile {
    
    private final UUID id;
    private final String stage;
    private final Queue<GameRecord> records = new LinkedList<>();

    public GameProfile(UUID id, String stage) {
        this.id = id;
        this.stage = stage;
    }

    public UUID getID() {
        return id;
    }

    public String getStage() {
        return stage;
    }

    public Queue<GameRecord> getRecords() {
        return records;
    }
    
}
