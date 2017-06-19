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
package jp.llv.flaggame.reception.fest;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import jp.llv.flaggame.reception.TeamColor;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalMatch {

    private String stage;
    private final Map<TeamColor, TeamColor> colorMapping = new EnumMap<>(TeamColor.class);

    public FestivalMatch(String stage) {
        Objects.requireNonNull(stage);
        this.stage = stage;
    }

    public String getStage() {
        return stage;
    }

    public Map<TeamColor, TeamColor> getColorMapping() {
        return Collections.unmodifiableMap(colorMapping);
    }

    public void setStage(String name) {
        Objects.requireNonNull(name);
        this.stage = name;
    }

    public void setColorMap(TeamColor team, TeamColor stage) {
        Objects.requireNonNull(team);
        Objects.requireNonNull(stage);
        colorMapping.put(team, stage);
    }

    public void setColorMapping(Map<TeamColor, TeamColor> mapping) {
        for (Map.Entry<TeamColor, TeamColor> entry : mapping.entrySet()) {
            setColorMap(entry.getKey(), entry.getValue());
        }
    }

}
