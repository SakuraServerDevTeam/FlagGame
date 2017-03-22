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

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.rollback.RollbackTarget;

/**
 *
 * @author toyblocks
 */
public class AreaInfo {

    private final Map<Long, RollbackTarget> rollbacks = new HashMap<>();
    private final Map<TeamColor, State> godmode = new EnumMap<>(TeamColor.class);
    private final Map<Protection, State> protection = new EnumMap<>(Protection.class);
    
    public Map<Long, RollbackTarget> getRollbacks() {
        return Collections.unmodifiableMap(rollbacks);
    }
    
    public void removeRollback(long timing) {
        rollbacks.remove(timing);
    }
    
    public void addRollback(long timing, RollbackTarget target) {
        rollbacks.put(timing, target);
    }
    
    public State isGodmode(TeamColor color) {
        State state = godmode.get(color);
        return state == null ? State.DEFAULT : state;
    }
    
    public void setGodmode(TeamColor color, State state) {
        godmode.put(color, state);
    }
    
    public State isProtected(Protection type) {
        State state = protection.get(type);
        return state == null ? State.DEFAULT : state;
    }
    
    public void setProtected(Protection type, State state) {
        protection.put(type, state);
    }
    
    public enum State {
        TRUE(true, true),
        FALSE(false, true),
        DEFAULT(false, false),;

        private final boolean positive;
        private final boolean forceful;

        private State(boolean positive, boolean forceful) {
            this.positive = positive;
            this.forceful = forceful;
        }

        public boolean isPositive() {
            return positive;
        }

        public boolean isForceful() {
            return forceful;
        }

    }

}
