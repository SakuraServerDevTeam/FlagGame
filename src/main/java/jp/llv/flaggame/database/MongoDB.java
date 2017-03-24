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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import jp.llv.flaggame.profile.GameRecordStream;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import syam.flaggame.ConfigurationManager;
import syam.flaggame.FlagGame;
import syam.flaggame.game.Stage;
import syam.flaggame.game.StageBsonConverter;

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
            database = client.getDatabase(config.getDatabaseDbname());
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
    public Collection<Stage> loadStages() throws DatabaseException {
        MongoCollection<BsonValue> coll = database.getCollection("stages").withDocumentClass(BsonValue.class);
        List<Stage> result = new ArrayList<>();
        coll.find().map(BsonDocument.class::cast)
                .map(StageBsonConverter::readStage)
                .forEach((Consumer<Stage>) result::add);
        return result;
    }

    @Override
    public void saveStages(Collection<Stage> stages) throws DatabaseException {
        MongoCollection<BsonValue> coll = database.getCollection("stages").withDocumentClass(BsonValue.class);
        coll.deleteMany(new BsonDocument());
        stages.parallelStream().map(StageBsonConverter::writeStage).forEach(coll::insertOne);
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
        }
    }

}
