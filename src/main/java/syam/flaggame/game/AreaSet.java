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
package syam.flaggame.game;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import jp.llv.flaggame.game.permission.GamePermissionState;
import jp.llv.flaggame.util.ValueSortedMap;
import org.bukkit.Location;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author toyblocks
 */
public class AreaSet {

    private static final String STAGE_AREA_NAME = "stage";

    private final Map<String, Cuboid> areas;
    private final Map<String, AreaInfo> information;

    public AreaSet() {
        this.areas = ValueSortedMap.newInstance(Comparator.comparing(Cuboid::getArea));
        this.information = new HashMap<>();
    }

    public void setArea(String name, Cuboid area) {
        Objects.requireNonNull(area);
        this.areas.put(name, area);
        AreaInfo info = getAreaInfo(name);
        if (info != null) {
            info.removeRollbacks();
        }
    }

    public void setStageArea(Cuboid area) {
        setArea(STAGE_AREA_NAME, area);
    }

    public void removeArea(String name) {
        areas.remove(name);
        information.remove(name);
    }

    public Cuboid getArea(String name) {
        return areas.get(name);
    }

    public Cuboid getStageArea() {
        return areas.get(STAGE_AREA_NAME);
    }

    public boolean hasStageArea() {
        return getStageArea() != null;
    }

    public void setAreaInfo(String name, AreaInfo info) {
        Objects.requireNonNull(info);
        this.information.put(name, info);
    }

    public void setStageAreaInfo(AreaInfo info) {
        setAreaInfo(STAGE_AREA_NAME, info);
    }

    public AreaInfo getAreaInfo(String name) {
        if (!areas.containsKey(name)) {
            return null;
        }
        if (!information.containsKey(name)) {
            information.put(name, new AreaInfo());
        }
        return information.get(name);
    }

    public AreaInfo getStageAreaInfo() {
        return getAreaInfo(STAGE_AREA_NAME);
    }

    public Set<String> getAreas() {
        return Collections.unmodifiableSet(areas.keySet());
    }

    public List<String> getAreas(Location loc) {
        return areas.entrySet().stream()
                .filter(e -> e.getValue().contains(loc))
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    public String getPrimaryArea(Location loc) {
        List<String> a = getAreas(loc);
        return a.isEmpty() ? null : a.get(0);
    }

    public List<AreaInfo> getAreaInfo(Location loc) {
        return areas.entrySet().stream()
                .filter(e -> e.getValue().contains(loc))
                .map(e -> getAreaInfo(e.getKey()))
                .collect(Collectors.toList());
    }

    public boolean getAreaInfo(Location loc, Function<? super AreaInfo, ? extends GamePermissionState> mapper) {
        for (AreaInfo info : getAreaInfo(loc)) {
            GamePermissionState s = mapper.apply(info);
            if (s.isForceful()) {
                return s.isPositive();
            }
        }
        return GamePermissionState.DEFAULT.isPositive();
    }

    public Map<String, Cuboid> getAreaMap() {
        return Collections.unmodifiableMap(areas);
    }

    /*package*/ void setAreaMap(Map<String, Cuboid> map) {
        areas.putAll(map);
    }

    /*package*/ Map<String, AreaInfo> getAreaInfoMap() {
        return information;
    }

    /*package*/ void setAreaInfoMap(Map<String, AreaInfo> map) {
        information.putAll(map);
    }

}
