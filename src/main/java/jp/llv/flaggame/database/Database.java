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
import syam.flaggame.player.CachedAccount;
import jp.llv.flaggame.api.player.Account;
import jp.llv.flaggame.api.kit.Kit;
import jp.llv.flaggame.profile.RecordStream;
import jp.llv.flaggame.api.profile.StatEntry;
import jp.llv.flaggame.api.profile.RecordType;
import jp.llv.flaggame.api.stage.Stage;

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

    void loadKits(DatabaseCallback<Kit, RuntimeException> consumer, DatabaseCallback<Void, DatabaseException> callback);

    void saveKit(Kit kit, DatabaseCallback<Void, DatabaseException> callback);

    void deleteKit(Kit kit, DatabaseCallback<Void, DatabaseException> callback);

    void saveReocrds(RecordStream records, DatabaseCallback<Void, DatabaseException> callback);

    void loadPlayerStat(UUID player, DatabaseCallback<Map.Entry<RecordType, StatEntry>, RuntimeException> consumer, DatabaseCallback<Void, DatabaseException> callback);

    void loadPlayerExp(UUID player, DatabaseCallback<Long, DatabaseException> callback);

    void loadPlayerVibe(UUID player, DatabaseCallback<Double, DatabaseException> callback);

    void loadStageStat(String stage, DatabaseCallback<Map.Entry<RecordType, StatEntry>, RuntimeException> consumer, DatabaseCallback<Void, DatabaseException> callback);

    void loadPlayerAccount(UUID player, DatabaseCallback<CachedAccount, DatabaseException> callback);

    void savePlayerAccount(Account account, DatabaseCallback<Void, DatabaseException> callback);

    @Override
    public void close() throws DatabaseException;

}
