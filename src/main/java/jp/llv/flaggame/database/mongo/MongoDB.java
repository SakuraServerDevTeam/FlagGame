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
package jp.llv.flaggame.database.mongo;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.connection.ClusterSettings;
import java.util.Collections;
import jp.llv.flaggame.database.Database;
import jp.llv.flaggame.database.DatabaseCallback;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.database.DatabaseResult;
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

    private static final String ID = "_id";
    private static final String SET = "$set";
    private static final String COLLECTION_STAGE = "stages";

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
            client = MongoClients.create(
                    MongoClientSettings.builder()
                    .clusterSettings(
                            ClusterSettings.builder().hosts(
                                    Collections.singletonList(new ServerAddress(
                                            config.getDatabaseAddress(),
                                            config.getDatabasePort()
                                    ))).build()
                    ).credentialList(
                            config.getDatabaseUsername() == null
                                    ? Collections.emptyList()
                                    : Collections.singletonList(MongoCredential.createCredential(
                                            config.getDatabaseUsername(),
                                            config.getDatabaseDbname(),
                                            config.getDatabaseUserpass().toCharArray()
                                    )))
                    .build()
            );
        }
        database = client.getDatabase(config.getDatabaseDbname());
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
        }
    }

    private MongoCollection<BsonValue> getStageCollection() throws DatabaseException {
        if (database == null) {
            throw new DatabaseException("Not connected");
        }
        return database.getCollection(COLLECTION_STAGE).withDocumentClass(BsonValue.class);
    }

    @Override
    public void loadStages(DatabaseCallback<Stage, RuntimeException> consumer, DatabaseCallback<Void, DatabaseException> callback) {
        try {
            getStageCollection().find()
                    .map(BsonDocument.class::cast)
                    .map(StageBsonConverter::readStage)
                    .forEach(
                            new MongoDBResultCallback<>(consumer),
                            new MongoDBErrorCallback<>(callback)
                    );
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    @Override
    public void saveStage(Stage stage, DatabaseCallback<Void, DatabaseException> callback) {
        try {
            MongoCollection<BsonValue> coll = getStageCollection();
            BsonDocument bson = StageBsonConverter.writeStage(stage);
            coll.updateOne(
                    Filters.eq(ID, bson.get(ID)),
                    new BsonDocument(SET, bson),
                    new UpdateOptions().upsert(true),
                    new MongoDBErrorCallback<>(callback)
            );
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    @Override
    public void deleteStage(Stage stage, DatabaseCallback<Void, DatabaseException> callback) {
        try {
            getStageCollection().deleteOne(
                    Filters.eq(ID, stage.getName()),
                    new MongoDBErrorCallback<>(callback)
            );
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

}
