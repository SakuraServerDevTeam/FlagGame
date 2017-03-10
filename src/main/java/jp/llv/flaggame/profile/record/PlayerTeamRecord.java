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
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 */
public class PlayerTeamRecord extends PlayerRecord {
    
    public static final String FIELD_TEAM = "team";

    public PlayerTeamRecord(UUID game, double x, double y, double z, UUID player, TeamColor team) {
        super(game, x, y, z, player);
        super.put(FIELD_TEAM, team.toString().toLowerCase());
    }

    public PlayerTeamRecord(UUID game, Player player, TeamColor team) {
        super(game, player);
        super.put(FIELD_TEAM, team.toString().toLowerCase());
    }

    /*package*/ PlayerTeamRecord(Document base) {
        super(base);
    }
    
    public TeamColor getTeam() {
        return TeamColor.of(super.getString(FIELD_TEAM));
    }

    @Override
    public RecordType getType() {
        return RecordType.TEAM;
    }
    
}
