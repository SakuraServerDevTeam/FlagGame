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

import jp.llv.flaggame.reception.TeamColor;
import syam.flaggame.command.objective.ObjectiveType;
import syam.flaggame.game.Stage;

/**
 *
 * @author Toyblocks
 */
public class SetupSession {

    private final Stage stage;
    private ObjectiveType setting;

    private TeamColor color = null;
    private Double point = null;
    private Byte hp = null;

    /*package*/ SetupSession(Stage stage) {
        if (stage == null) {
            throw new NullPointerException();
        }
        this.stage = stage;
    }

    public Stage getSelectedStage() {
        return this.stage;
    }

    public ObjectiveType getSetting() {
        return this.setting;
    }

    public SetupSession setSetting(ObjectiveType setting) {
        if (this.setting == setting) {
            return this;
        }
        this.setting = setting;
        this.color = null;
        this.point = null;
        this.hp = null;
        return this;
    }

    public Double getSelectedPoint() {
        if (this.setting != ObjectiveType.FLAG && this.setting != ObjectiveType.NEXUS && this.setting != ObjectiveType.BANNER_SPAWNER) {
            throw new IllegalStateException();
        }
        return this.point;
    }

    public SetupSession setSelectedPoint(double point) {
        if (this.setting != ObjectiveType.FLAG && this.setting != ObjectiveType.NEXUS && this.setting != ObjectiveType.BANNER_SPAWNER) {
            throw new IllegalStateException();
        }
        this.point = point;
        return this;
    }

    public TeamColor getSelectedColor() {
        if (this.setting != ObjectiveType.BANNER_SLOT && this.setting != ObjectiveType.NEXUS) {
            throw new IllegalStateException();
        }
        return color;
    }

    public SetupSession setSelectedColor(TeamColor color) {
        if (this.setting != ObjectiveType.BANNER_SLOT && this.setting != ObjectiveType.NEXUS) {
            throw new IllegalStateException();
        }
        this.color = color;
        return this;
    }

    public Byte getSelectedHp() {
        if (this.setting != ObjectiveType.BANNER_SPAWNER) {
            throw new IllegalStateException();
        }
        return hp;
    }

    public SetupSession setHp(Byte hp) {
        if (this.setting != ObjectiveType.BANNER_SPAWNER) {
            throw new IllegalStateException();
        }
        this.hp = hp;
        return this;
    }

}
