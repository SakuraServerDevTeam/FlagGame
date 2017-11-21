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
package jp.llv.flaggame.api.player;

import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.stage.objective.ObjectiveType;
import jp.llv.flaggame.api.reception.TeamColor;

/**
 *
 * @author toyblocks
 */
public interface StageSetupSession extends SetupSession<Stage> {

    TeamColor getSelectedColor();

    Byte getSelectedHp();

    Double getSelectedPoint();

    ObjectiveType getSetting();

    StageSetupSession setHp(Byte hp);

    StageSetupSession setSelectedColor(TeamColor color);

    StageSetupSession setSelectedPoint(double point);

    StageSetupSession setSetting(ObjectiveType setting);
    
}
