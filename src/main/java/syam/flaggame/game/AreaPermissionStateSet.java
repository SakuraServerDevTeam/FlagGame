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
package syam.flaggame.game;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import jp.llv.flaggame.reception.TeamColor;

/**
 *
 * @author toyblocks
 */
public class AreaPermissionStateSet {
    
    private final Map<TeamColor, AreaState> values;

    /*package*/ AreaPermissionStateSet() {
        this.values = new EnumMap<>(TeamColor.class);
    }
    
    /*package*/ AreaPermissionStateSet(Map<TeamColor, AreaState> values) {
        this.values = values;
    }
    
    public AreaState getState(TeamColor team) {
        return values.containsKey(team) ? values.get(team) : AreaState.DEFAULT;
    }
    
    /*package*/ Map<TeamColor, AreaState> getState() {
        return this.values;
    }
    
    public void setState(TeamColor team, AreaState state) {
        Objects.requireNonNull(team);
        if (state == null) {
            values.remove(team);
        } else {
            values.put(team, state);
        }
    }
    
}
