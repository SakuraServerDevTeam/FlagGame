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
import jp.llv.flaggame.reception.TeamColor;
import org.bson.Document;

/**
 *
 * @author toyblocks
 */
public class GameFinishRecord extends GameRecord {

    private static final String FIELD_TEAM_WON = "won";
    
    public GameFinishRecord(UUID game, TeamColor color) {
        super(game);
        super.put(FIELD_TEAM_WON, color == null ? null : color.toString().toLowerCase());
    }

    /*package*/ GameFinishRecord(Document base) {
        super(base);
    }
    
    public TeamColor getTeamWon() {
        String color = super.getString(FIELD_TEAM_WON);
        return color == null ? null : TeamColor.of(color);
    }

    @Override
    public RecordType getType() {
        return RecordType.GAME_FINISH;
    }
    
}
