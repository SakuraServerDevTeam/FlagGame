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

import jp.llv.flaggame.game.protection.Protection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jp.llv.flaggame.rollback.StageData;
import jp.llv.flaggame.rollback.StageDataType;

/**
 *
 * @author toyblocks
 */
public class AreaInfo {

    private final Map<String, RollbackData> rollbacks = new HashMap<>();
    private final Map<Protection, AreaState> protection = new EnumMap<>(Protection.class);
    private final Map<AreaPermission, AreaPermissionStateSet> permissions = new EnumMap<>(AreaPermission.class);

    
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
    
    public AreaState isProtected(Protection type) {
        AreaState state = protection.get(type);
        return state == null ? AreaState.DEFAULT : state;
    }
    
    public void setProtected(Protection type, AreaState state) {
        protection.put(type, state);
    }
    
    /*package*/ Map<Protection, AreaState> getProtectionMap() {
        return Collections.unmodifiableMap(protection);
    }
    
    /*package*/ void setProtectionMap(Map<Protection, AreaState> map) {
        protection.putAll(map);
    }
    
    public AreaPermissionStateSet getPermission(AreaPermission type) {
        if (!permissions.containsKey(type)) {
            permissions.put(type, new AreaPermissionStateSet());
        }
        return permissions.get(type); 
    }
    
    /*package*/ void setPermissions(Map<AreaPermission, AreaPermissionStateSet> permissions) {
        this.permissions.putAll(permissions);
    }
    
    /*package*/ Map<AreaPermission, AreaPermissionStateSet> getPermissions() {
        return this.permissions;
    }
    
    public static class RollbackData {
        
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
