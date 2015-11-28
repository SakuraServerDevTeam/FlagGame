/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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

import java.util.Optional;
import syam.flaggame.enums.config.Configables;
import syam.flaggame.game.Stage;

/**
 *
 * @author Toyblocks
 */
public class SetupSession {
    
    private final Stage stage;
    private Configables setting;
    
    private Byte selectedFlagType = null;
    
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
    
    public Optional<Byte> getSelectedFlagType() {
        if (this.setting != Configables.FLAG) throw new IllegalStateException();
        return Optional.ofNullable(this.selectedFlagType);
    }
    
    public void setSelectedFlagType(byte flagType) {
        if (this.setting != Configables.FLAG) throw new IllegalStateException();
        this.selectedFlagType = flagType;
    }
    
}
