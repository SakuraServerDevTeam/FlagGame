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
package jp.llv.flaggame.api.stage.area;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import jp.llv.flaggame.api.stage.permission.GamePermissionState;
import org.bukkit.Location;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author toyblocks
 */
public interface StageAreaSet {

    Cuboid getArea(String name);

    StageAreaInfo getAreaInfo(String name);

    List<? extends StageAreaInfo> getAreaInfo(Location loc);

    boolean getAreaInfo(Location loc, Function<? super StageAreaInfo, ? extends GamePermissionState> mapper);

    Map<String, StageAreaInfo> getAreaInfoMap();

    Map<String, Cuboid> getAreaMap();

    Set<String> getAreas();

    List<String> getAreas(Location loc);

    String getPrimaryArea(Location loc);

    Cuboid getStageArea();

    StageAreaInfo getStageAreaInfo();

    boolean hasStageArea();

    void removeArea(String name);

    void setArea(String name, Cuboid area);

    void setAreaInfo(String name, StageAreaInfo info);

    void setStageArea(Cuboid area);

    void setStageAreaInfo(StageAreaInfo info);

}
