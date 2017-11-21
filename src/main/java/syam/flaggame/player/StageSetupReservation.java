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
package syam.flaggame.player;

import jp.llv.flaggame.api.player.StageSetupSession;
import jp.llv.flaggame.api.player.SetupReservation;
import jp.llv.flaggame.api.reception.TeamColor;
import jp.llv.flaggame.api.session.Reservable;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.stage.objective.ObjectiveType;

/**
 *
 * @author Toyblocks
 */
public class StageSetupReservation extends SetupReservation<Stage> implements StageSetupSession {

    private ObjectiveType setting;

    private TeamColor color = null;
    private Double point = null;
    private Byte hp = null;

    public StageSetupReservation(Reservable.Reservation<Stage> reservation) {
        super(reservation);
    }

    @Override
    public ObjectiveType getSetting() {
        return this.setting;
    }

    @Override
    public StageSetupReservation setSetting(ObjectiveType setting) {
        if (this.setting == setting) {
            return this;
        }
        this.setting = setting;
        this.color = null;
        this.point = null;
        this.hp = null;
        return this;
    }

    @Override
    public Double getSelectedPoint() {
        if (this.setting != ObjectiveType.FLAG && this.setting != ObjectiveType.NEXUS && this.setting != ObjectiveType.BANNER_SPAWNER) {
            throw new IllegalStateException();
        }
        return this.point;
    }

    @Override
    public StageSetupReservation setSelectedPoint(double point) {
        if (this.setting != ObjectiveType.FLAG && this.setting != ObjectiveType.NEXUS && this.setting != ObjectiveType.BANNER_SPAWNER) {
            throw new IllegalStateException();
        }
        this.point = point;
        return this;
    }

    @Override
    public TeamColor getSelectedColor() {
        if (this.setting != ObjectiveType.BANNER_SLOT && this.setting != ObjectiveType.NEXUS) {
            throw new IllegalStateException();
        }
        return color;
    }

    @Override
    public StageSetupReservation setSelectedColor(TeamColor color) {
        if (this.setting != ObjectiveType.BANNER_SLOT && this.setting != ObjectiveType.NEXUS) {
            throw new IllegalStateException();
        }
        this.color = color;
        return this;
    }

    @Override
    public Byte getSelectedHp() {
        if (this.setting != ObjectiveType.BANNER_SPAWNER) {
            throw new IllegalStateException();
        }
        return hp;
    }
    
    @Override
    public StageSetupReservation setHp(Byte hp) {
        if (this.setting != ObjectiveType.BANNER_SPAWNER) {
            throw new IllegalStateException();
        }
        this.hp = hp;
        return this;
    }

}
