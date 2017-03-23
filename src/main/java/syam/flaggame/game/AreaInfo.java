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
import org.bukkit.ChatColor;

/**
 *
 * @author toyblocks
 */
public class AreaInfo {

    private final Map<String, RollbackData> rollbacks = new HashMap<>();
    private final Map<TeamColor, State> godmode = new EnumMap<>(TeamColor.class);
    private final Map<Protection, State> protection = new EnumMap<>(Protection.class);
    
    public RollbackData addRollback(String id) {
        if (rollbacks.containsKey(id)) {
            throw new IllegalStateException("ID duplication");
        }
        RollbackData data = new RollbackData();
        rollbacks.put(id, data);
        return data;
    }
    
    public void removeRollback(String id) {
        rollbacks.remove(id);
    }
    
    public RollbackData getRollback(String id) {
        return rollbacks.get(id);
    }
    
    public Map<String, RollbackData> getRollbacks() {
        return Collections.unmodifiableMap(rollbacks);
    }
    
    public void removeRollbacks() {
        rollbacks.clear();
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
        TRUE(true, true, ChatColor.GREEN),
        FALSE(false, true, ChatColor.RED),
        DEFAULT(false, false, ChatColor.GOLD),;

        private final ChatColor color;
        private final boolean positive;
        private final boolean forceful;

        private State(boolean positive, boolean forceful, ChatColor color) {
            this.positive = positive;
            this.forceful = forceful;
            this.color = color;
        }

        public boolean isPositive() {
            return positive;
        }

        public boolean isForceful() {
            return forceful;
        }
        
        public String format() {
            return color + toString().toLowerCase();
        }

    }
    
    public static class RollbackData {
        
        private long timing = 0L;
        private RollbackTarget target = RollbackTarget.NONE;
        private byte[] data = {};

        private RollbackData() {
        }
        
        public RollbackTarget getTarget() {
            return target;
        }
        
        public void setTarget(RollbackTarget target) {
            this.target = target;
        }
        
        public long getTiming() {
            return timing;
        }
        
        public void setTiming(long timing) {
            if (timing < 0) {
                throw new IllegalArgumentException("Negative timing is not allowed");
            }
            this.timing = timing;
        }
        
        public byte[] getData() {
            return data;
        }
        
        public void setData(byte[] data) {
            this.data = data;
        }
        
    }

}
