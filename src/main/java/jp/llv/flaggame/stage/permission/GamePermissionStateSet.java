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
package jp.llv.flaggame.stage.permission;

import jp.llv.flaggame.api.stage.permission.StagePermissionStateSet;
import jp.llv.flaggame.api.stage.permission.GamePermissionState;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import jp.llv.flaggame.api.reception.TeamColor;
import jp.llv.flaggame.api.reception.TeamType;

/**
 *
 * @author toyblocks
 */
public class GamePermissionStateSet implements StagePermissionStateSet {

    private final Map<TeamColor, GamePermissionState> values;

    public GamePermissionStateSet() {
        this.values = new EnumMap<>(TeamColor.class);
    }

    public GamePermissionStateSet(Map<TeamColor, GamePermissionState> values) {
        this.values = new HashMap<>(values);
    }

    @Override
    public GamePermissionState getState(TeamType team) {
        return values.containsKey(team.toColor()) ? values.get(team.toColor()) : GamePermissionState.DEFAULT;
    }

    @Override
    public Map<TeamColor, GamePermissionState> getState() {
        return Collections.unmodifiableMap(this.values);
    }

    @Override
    public void setState(TeamType team, GamePermissionState state) {
        Objects.requireNonNull(team);
        if (state == null || state == GamePermissionState.DEFAULT) {
            values.remove(team.toColor());
        } else {
            values.put(team.toColor(), state);
        }
    }

    @Override
    public boolean isAllDefault() {
        return values.isEmpty();
    }

}
