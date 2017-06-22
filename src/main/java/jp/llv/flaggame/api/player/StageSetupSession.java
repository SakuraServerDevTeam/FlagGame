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

import jp.llv.flaggame.api.session.Reservable;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.api.stage.objective.ObjectiveType;
import syam.flaggame.player.SetupSession;

/**
 *
 * @author toyblocks
 */
public interface StageSetupSession {

    Reservable<?> getSelected();

    <T extends Reservable<T>> T getSelected(Class<T> clazz);

    TeamColor getSelectedColor();

    Byte getSelectedHp();

    Double getSelectedPoint();

    ObjectiveType getSetting();

    SetupSession setHp(Byte hp);

    SetupSession setSelectedColor(TeamColor color);

    SetupSession setSelectedPoint(double point);

    SetupSession setSetting(ObjectiveType setting);

}
