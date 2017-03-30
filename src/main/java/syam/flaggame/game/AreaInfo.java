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
package syam.flaggame.game;

import jp.llv.flaggame.game.permission.GamePermission;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jp.llv.flaggame.game.permission.GamePermissionStateSet;
import jp.llv.flaggame.rollback.StageData;
import jp.llv.flaggame.rollback.StageDataType;

/**
 *
 * @author toyblocks
 */
public class AreaInfo {

    private final Map<String, RollbackData> rollbacks = new HashMap<>();
    private final Map<GamePermission, GamePermissionStateSet> permissions = new EnumMap<>(GamePermission.class);

    
    public RollbackData addRollback(String id) {
        if (rollbacks.containsKey(id)) {
            throw new IllegalStateException("ID duplication");
        }
        RollbackData data = new RollbackData();
        rollbacks.put(id, data);
        return data;
    }
    
    /*package*/ void addRollback(String name, RollbackData data) {
        rollbacks.put(name, data);
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
    
    public List<RollbackData> getInitialRollbacks() {
        return rollbacks.values().stream().filter(r -> r.getTiming() == 0).collect(Collectors.toList());
    }
    
    public List<RollbackData> getDelayedRollbacks() {
        return rollbacks.values().stream().filter(r -> r.getTiming() != 0).collect(Collectors.toList());
    }
    
    /*package*/ void setRollbacks(Map<String, RollbackData> map) {
        rollbacks.putAll(map);
    }
    
    public void removeRollbacks() {
        rollbacks.clear();
    }
    
    public GamePermissionStateSet getPermission(GamePermission type) {
        if (!permissions.containsKey(type)) {
            permissions.put(type, new GamePermissionStateSet());
        }
        return permissions.get(type); 
    }
    
    /*package*/ void setPermissions(Map<GamePermission, GamePermissionStateSet> permissions) {
        this.permissions.putAll(permissions);
    }
    
    /*package*/ Map<GamePermission, GamePermissionStateSet> getPermissions() {
        return this.permissions;
    }
    
    /**
     * Scheduled rollback data.
     */
    public static class RollbackData {
        
        /**
         * scheduled timing. if zero, this will be handled in reception.
         * otherwise in game.
         */
        private long timing = 0L;
        private StageData target = StageDataType.NONE.newInstance();
        private byte[] data = {};

        /*package*/ RollbackData() {
        }
        
        public StageData getTarget() {
            return target;
        }
        
        public void setTarget(StageData target) {
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
