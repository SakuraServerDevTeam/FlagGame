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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
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
        this.areas = new ValueSortedMap<>(Comparator.comparing(Cuboid::getArea));
        this.information = new HashMap<>();
    }
    
    public void setArea(String name, Cuboid area) {
        Objects.requireNonNull(area);
        this.areas.put(name, area);
    }
    
    public void setStageArea(Cuboid area) {
        setArea(STAGE_AREA_NAME, area);
    }
    
    public Cuboid getArea(String name) {
        return areas.get(name);
    }
    
    public Cuboid getStageArea() {
        return areas.get(STAGE_AREA_NAME);
    }
    
    public void setAreaInfo(String name, AreaInfo info) {
        Objects.requireNonNull(info);
        this.information.put(name, info);
    }
    
    public void setStageAreaInfo(AreaInfo info) {
        setAreaInfo(STAGE_AREA_NAME, info);
    }
    
    public AreaInfo getAreaInfo(String name) {
        return information.get(name);
    }
    
    public AreaInfo getStageAreaInfo() {
        return getAreaInfo(STAGE_AREA_NAME);
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
                .map(e -> information.get(e.getKey()))
                .collect(Collectors.toList());
    }
    
    public boolean getAreaInfo(Location loc, Function<? super AreaInfo, ? extends AreaInfo.State> mapper) {
        for (AreaInfo info : getAreaInfo(loc)) {
            AreaInfo.State s = mapper.apply(info);
            if (s.isForceful()) {
                return s.isPositive();
            }
        }
        return AreaInfo.State.DEFAULT.isPositive();
    }
    
}
