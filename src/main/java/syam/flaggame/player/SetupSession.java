/* 
 * Copyright (C) 2017 Syamn, SakuraServerDev
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
import syam.flaggame.game.Configables;
import syam.flaggame.game.Stage;

/**
 *
 * @author Toyblocks
 */
public class SetupSession {
    
    private final Stage stage;
    private Configables setting;
    
    private TeamColor color = null;
    private Double point = null;
    private Byte hp = null;
    
    /*package*/ SetupSession(Stage stage) {
        if (stage == null) throw new NullPointerException();
        this.stage = stage;
    }
    
    public Stage getSelectedStage() {
        return this.stage;
    }
    
    public Configables getSetting() {
        return this.setting;
    }
    
    public SetupSession setSetting(Configables setting) {
        this.setting = setting;
        return this;
    }
    
    public Double getSelectedPoint() {
        if (this.setting != Configables.FLAG && this.setting != Configables.NEXUS) throw new IllegalStateException();
        return this.point;
    }
    
    public SetupSession setSelectedPoint(double point) {
        if (this.setting != Configables.FLAG && this.setting != Configables.NEXUS) throw new IllegalStateException();
        this.point = point;
        return this;
    }

    public TeamColor getSelectedColor() {
        if (this.setting != Configables.BANNER_SLOT) throw new IllegalStateException();
        return color;
    }

    public SetupSession setSelectedColor(TeamColor color) {
        if (this.setting != Configables.BANNER_SLOT) throw new IllegalStateException();
        this.color = color;
        return this;
    }

    public Byte getSelectedHp() {
        if (this.setting != Configables.BANNER_SPAWNER) throw new IllegalStateException();
        return hp;
    }

    public SetupSession setHp(Byte hp) {
        if (this.setting != Configables.BANNER_SPAWNER) throw new IllegalStateException();
        this.hp = hp;
        return this;
    }
    
}
