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

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import jp.llv.flaggame.profile.GameRecordStream;
import syam.flaggame.ConfigurationManager;
import syam.flaggame.FlagGame;

/**
 *
 * @author toyblocks
 */
public class MongoDB implements Database {

    private final FlagGame plugin;
    private final ConfigurationManager config;
    private MongoClient client;
    private MongoDatabase database;

    public MongoDB(FlagGame plugin, ConfigurationManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void connect() throws DatabaseException {
        if (client == null) {
            ServerAddress addr = new ServerAddress(
                    config.getDatabaseAddress(),
                    config.getDatabasePort()
            );
            MongoCredential credential = config.getDatabaseUsername() == null ? null
                    : MongoCredential.createCredential(
                            config.getDatabaseUsername(),
                            config.getDatabaseDbname(),
                            config.getDatabaseUserpass().toCharArray()
                    );
            client = credential == null ? new MongoClient(addr) : new MongoClient(addr, Arrays.asList(credential));
            
        }
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public GameRecordStream getGameProgile(UUID game) throws DatabaseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveGameProfile(GameRecordStream profile) throws DatabaseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<UUID> getGames() throws DatabaseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
        }
    }

}
