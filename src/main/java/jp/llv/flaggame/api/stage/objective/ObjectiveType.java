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
package jp.llv.flaggame.api.stage.objective;

import jp.llv.flaggame.util.StringUtil;
import jp.llv.flaggame.api.stage.objective.StageObjective;

/**
 *
 * @author SakuraServerDev
 */
public enum ObjectiveType {

    FLAG(Flag.class),
    CHEST(GameChest.class),
    NEXUS(Nexus.class),
    BANNER_SLOT(BannerSlot.class),
    BANNER_SPAWNER(BannerSpawner.class),;

    private final Class<? extends StageObjective> clazz;

    private ObjectiveType(Class<? extends StageObjective> clazz) {
        this.clazz = clazz;
    }

    public boolean is(StageObjective instance) {
        return clazz.isInstance(instance);
    }

    public String getName() {
        return StringUtil.capitalize(name());
    }

    public Class<? extends StageObjective> getType() {
        return clazz;
    }

    public static ObjectiveType of(String name) {
        return valueOf(name.toUpperCase());
    }

}
