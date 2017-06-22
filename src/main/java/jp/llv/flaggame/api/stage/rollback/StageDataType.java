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
package jp.llv.flaggame.api.stage.rollback;

import java.util.function.Supplier;
import jp.llv.flaggame.stage.rollback.ClassicStageData;
import jp.llv.flaggame.stage.rollback.StructureLibStageData;
import jp.llv.flaggame.stage.rollback.VoidStageData;

/**
 *
 * @author SakuraServerDev
 */
public enum StageDataType {

    NONE(VoidStageData::new),
    CLASSIC(ClassicStageData::new),
    BLOCKS(() -> new StructureLibStageData(false, true)),
    ENTITIES(() -> new StructureLibStageData(true, true)),;

    private final Supplier<StageData> constructor;

    private StageDataType(Supplier<StageData> constructor) {
        this.constructor = constructor;
    }

    public StageData newInstance() {
        return constructor.get();
    }

}
