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
package jp.llv.flaggame.stage;

import jp.llv.flaggame.api.stage.area.StageAreaSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.stage.area.StageAreaInfo;
import jp.llv.flaggame.api.stage.permission.GamePermissionState;
import jp.llv.flaggame.util.ValueSortedMap;
import org.bukkit.Location;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author toyblocks
 */
public class AreaSet implements StageAreaSet {

    private static final String STAGE_AREA_NAME = "stage";

    private final Map<String, Cuboid> areas;
    private final Map<String, StageAreaInfo> information;

    public AreaSet() {

        this.areas = new ValueSortedMap<>(Comparator.comparing(Cuboid::getArea), Comparator.naturalOrder());
        this.information = new HashMap<>();
    }

    @Override
    public void setArea(String name, Cuboid area) {
        Objects.requireNonNull(area);
        this.areas.put(name, area);
        getAreaInfo(name).removeRollbacks();
    }

    @Override
    public void setStageArea(Cuboid area) {
        setArea(STAGE_AREA_NAME, area);
    }

    @Override
    public void removeArea(String name) {
        areas.remove(name);
        information.remove(name);
    }

    @Override
    public Cuboid getArea(String name) {
        return areas.get(name);
    }

    @Override
    public Cuboid getStageArea() {
        return areas.get(STAGE_AREA_NAME);
    }

    @Override
    public boolean hasStageArea() {
        return getStageArea() != null;
    }

    @Override
    public void setAreaInfo(String name, StageAreaInfo info) {
        Objects.requireNonNull(info);
        this.information.put(name, info);
    }

    @Override
    public void setStageAreaInfo(StageAreaInfo info) {
        setAreaInfo(STAGE_AREA_NAME, info);
    }

    @Override
    public StageAreaInfo getAreaInfo(String name) {
        if (!areas.containsKey(name)) {
            return null;
        }
        if (!information.containsKey(name)) {
            information.put(name, new AreaInfo());
        }
        return information.get(name);
    }

    @Override
    public StageAreaInfo getStageAreaInfo() {
        return getAreaInfo(STAGE_AREA_NAME);
    }

    @Override
    public Set<String> getAreas() {
        return Collections.unmodifiableSet(areas.keySet());
    }

    @Override
    public List<String> getAreas(Location loc) {
        return areas.entrySet().stream()
                .filter(e -> e.getValue().contains(loc))
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    @Override
    public String getPrimaryArea(Location loc) {
        List<String> a = getAreas(loc);
        return a.isEmpty() ? null : a.get(0);
    }

    @Override
    public List<StageAreaInfo> getAreaInfo(Location loc) {
        return areas.entrySet().stream()
                .filter(e -> e.getValue().contains(loc))
                .map(e -> getAreaInfo(e.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean getAreaInfo(Location loc, Function<? super StageAreaInfo, ? extends GamePermissionState> mapper) {
        for (StageAreaInfo info : getAreaInfo(loc)) {
            GamePermissionState s = mapper.apply(info);
            if (s.isForceful()) {
                return s.isPositive();
            }
        }
        return GamePermissionState.DEFAULT.isPositive();
    }

    @Override
    public Map<String, Cuboid> getAreaMap() {
        return Collections.unmodifiableMap(areas);
    }

    public void setAreaMap(Map<String, Cuboid> map) {
        areas.putAll(map);
    }

    @Override
    public Map<String, StageAreaInfo> getAreaInfoMap() {
        return Collections.unmodifiableMap(information);
    }

    public void setAreaInfoMap(Map<String, StageAreaInfo> map) {
        information.putAll(map);
    }

}
