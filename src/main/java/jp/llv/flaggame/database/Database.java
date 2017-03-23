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
package jp.llv.flaggame.database;

import java.util.Collection;
import java.util.UUID;
import jp.llv.flaggame.profile.GameRecordStream;
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
    
    GameRecordStream getGameProgile(UUID game) throws DatabaseException;
    
    void saveGameProfile(GameRecordStream profile) throws DatabaseException;
    
    Collection<UUID> getGames() throws DatabaseException;
    
    Collection<Stage> loadStages() throws DatabaseException;
    
    void saveStages(Collection<Stage> stages) throws DatabaseException;

    @Override
    public void close() throws DatabaseException;

}
