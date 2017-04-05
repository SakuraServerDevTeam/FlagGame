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
package jp.llv.flaggame.database;

import java.util.Map;
import java.util.UUID;
import jp.llv.flaggame.profile.RecordStream;
import jp.llv.flaggame.profile.StatEntry;
import jp.llv.flaggame.profile.record.RecordType;
import syam.flaggame.game.Stage;

/**
 *
 * @author toyblocks
 */
public interface Database extends AutoCloseable {

    void connect() throws DatabaseException;

    default void tryConnect() {
        try {
            this.connect();
        } catch (DatabaseException ex) {
        }
    }

    boolean isConnected();

    void loadStages(DatabaseCallback<Stage, RuntimeException> consumer, DatabaseCallback<Void, DatabaseException> callback);

    void saveStage(Stage stage, DatabaseCallback<Void, DatabaseException> callback);

    void deleteStage(Stage stage, DatabaseCallback<Void, DatabaseException> callback);

    void saveReocrds(RecordStream records, DatabaseCallback<Void, DatabaseException> callback);

    void loadPlayerStat(UUID player, DatabaseCallback<Map.Entry<RecordType, StatEntry>, RuntimeException> consumer, DatabaseCallback<Void, DatabaseException> callback);

    void loadPlayerExp(UUID player, DatabaseCallback<Long, DatabaseException> callback);

    void loadPlayerVibe(UUID player, DatabaseCallback<Double, DatabaseException> callback);

    void loadStageStat(String stage, DatabaseCallback<Map.Entry<RecordType, StatEntry>, RuntimeException> consumer, DatabaseCallback<Void, DatabaseException> callback);

    @Override
    public void close() throws DatabaseException;

}
