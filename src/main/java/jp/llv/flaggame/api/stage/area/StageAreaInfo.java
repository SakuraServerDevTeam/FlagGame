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
package jp.llv.flaggame.api.stage.area;

import java.util.List;
import java.util.Map;
import jp.llv.flaggame.api.stage.permission.GamePermission;
import jp.llv.flaggame.api.stage.permission.StagePermissionStateSet;
import jp.llv.flaggame.api.stage.rollback.StageData;

/**
 *
 * @author toyblocks
 */
public interface StageAreaInfo {

    StageMessageData addMessage(String message);

    StageRollbackData addRollback(String id);

    List<? extends StageRollbackData> getDelayedRollbacks();

    List<? extends StageRollbackData> getInitialRollbacks();

    List<? extends StageMessageData> getMessages();

    StagePermissionStateSet getPermission(GamePermission type);

    Map<GamePermission, ? extends StagePermissionStateSet> getPermissions();

    StageRollbackData getRollback(String id);

    Map<String, ? extends StageRollbackData> getRollbacks();

    void removeMessage(int index);

    void removeRollback(String id);

    void removeRollbacks();

    interface StageRollbackData {

        byte[] getData();

        StageData getTarget();

        long getTiming();

        void setData(byte[] data);

        void setTarget(StageData target);

        void setTiming(long timing);

    }

    interface StageMessageData {

        String getMessage();

        long getTiming();

        GameMessageType getType();

        void setMessage(String message);

        void setTiming(long timing);

        void setType(GameMessageType type);

    }

}
