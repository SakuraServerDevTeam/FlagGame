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

import java.util.ArrayList;
import jp.llv.flaggame.game.permission.GamePermission;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jp.llv.flaggame.game.permission.GamePermissionState;
import jp.llv.flaggame.game.permission.GamePermissionStateSet;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.rollback.StageData;
import jp.llv.flaggame.rollback.StageDataType;

/**
 *
 * @author toyblocks
 */
public class AreaInfo {

    private final Map<String, RollbackData> rollbacks = new HashMap<>();
    private final Map<GamePermission, GamePermissionStateSet> permissions = new EnumMap<>(GamePermission.class);
    private final List<MessageData> messages = new ArrayList<>();

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

    public void setRollbacks(Map<String, RollbackData> map) {
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

    public void setPermissions(Map<GamePermission, GamePermissionStateSet> permissions) {
        for (Map.Entry<GamePermission, GamePermissionStateSet> entry : permissions.entrySet()) {
            GamePermissionStateSet dest = getPermission(entry.getKey());
            for (Map.Entry<TeamColor, GamePermissionState> state : entry.getValue().getState().entrySet()) {
                dest.setState(state.getKey(), state.getValue());
            }
        }
        this.permissions.putAll(permissions);
    }

    public Map<GamePermission, GamePermissionStateSet> getPermissions() {
        return Collections.unmodifiableMap(permissions);
    }

    public List<MessageData> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public MessageData addMessage(String message) {
        MessageData data = new MessageData();
        data.setMessage(message);
        return data;
    }

    public void removeMessage(int index) {
        this.messages.remove(index);
    }

    public void setMessages(List<MessageData> messages) {
        this.messages.addAll(messages);
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

        public RollbackData() {
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

    public static class MessageData {

        private long timing = 1L;
        private GameMessageType type = GameMessageType.CHAT;
        private String message = "";

        public MessageData() {
        }

        public long getTiming() {
            return timing;
        }

        public void setTiming(long timing) {
            if (timing <= 0) {
                throw new IllegalArgumentException("Negative or zero timing is not allowed");
            }
            this.timing = timing;
        }

        public GameMessageType getType() {
            return type;
        }

        public void setType(GameMessageType type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

}
