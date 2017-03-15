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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import jp.llv.flaggame.reception.TeamColor;
import org.bson.Document;

/**
 *
 * @author toyblocks
 */
public class GameFinishRecord extends GameRecord {

    private static final String FIELD_TEAM_WON = "win";
    
    public GameFinishRecord(UUID game, List<TeamColor> colors) {
        super(game);
        super.put(FIELD_TEAM_WON, colors.stream().map(c -> c.toString().toLowerCase()).collect(Collectors.toList()));
    }

    /*package*/ GameFinishRecord(Document base) {
        super(base);
    }
    
    public List<TeamColor> getTeamsWin() {
        List<String> colors = (List<String>) super.get(FIELD_TEAM_WON);
        return colors.stream().map(c -> TeamColor.valueOf(c.toUpperCase())).collect(Collectors.toList());
    }

    @Override
    public RecordType getType() {
        return RecordType.GAME_FINISH;
    }
    
}
