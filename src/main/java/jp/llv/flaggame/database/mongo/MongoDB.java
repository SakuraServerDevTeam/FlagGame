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

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.UUID;
import jp.llv.flaggame.api.player.Account;
import jp.llv.flaggame.api.kit.Kit;
import jp.llv.flaggame.database.Database;
import jp.llv.flaggame.database.DatabaseCallback;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.database.DatabaseResult;
import jp.llv.flaggame.profile.RecordStream;
import jp.llv.flaggame.api.profile.StatEntry;
import jp.llv.flaggame.profile.record.ExpRecord;
import jp.llv.flaggame.profile.record.GameStartRecord;
import jp.llv.flaggame.profile.record.PlayerRecord;
import jp.llv.flaggame.profile.record.PlayerResultRecord;
import jp.llv.flaggame.api.profile.RecordType;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.database.mongo.bson.AccountDeserializer;
import jp.llv.flaggame.database.mongo.bson.AccountSerializer;
import jp.llv.flaggame.database.mongo.bson.KitDeserializer;
import jp.llv.flaggame.database.mongo.bson.KitSerializer;
import jp.llv.flaggame.database.mongo.bson.StageDeserializer;
import jp.llv.flaggame.database.mongo.bson.StageSerializer;
import jp.llv.flaggame.database.mongo.bson.TrophySerializer;
import jp.llv.flaggame.profile.record.ScoreRecord;
import jp.llv.flaggame.util.MapUtils;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import syam.flaggame.CachedFlagConfig;
import syam.flaggame.FlagGame;
import jp.llv.flaggame.api.trophy.Trophy;
import jp.llv.flaggame.database.mongo.bson.TrophyDeserializer;

/**
 *
 * @author toyblocks
 */
public class MongoDB implements Database {

    public static final String VIEW_PLAYER_STATS = "player_stats";
    public static final String VIEW_PLAYER_EXP = "player_exp";
    public static final String VIEW_PLAYER_VIBE = "player_vibe";
    public static final String VIEW_STAGE_STATS = "stage_stats";
    public static final String VIEW_GAME_HISTORY = "game_history";
    public static final String COLLECTION_STAGE = "stage";
    public static final String COLLECTION_KIT = "kit";
    public static final String COLLECTION_ACCOUNT = "account";
    public static final String COLLECTION_RECORD = "record";
    public static final String COLLECTION_TROPHY = "trophy";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_COUNT = "count";

    private static final String SET = "$set";
    private static final char FIELD_SEPARATOR = '.';

    private final FlagGame plugin;
    private final CachedFlagConfig config;
    private MongoClient client;
    private MongoDatabase database;

    public MongoDB(FlagGame plugin, CachedFlagConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void connect() throws DatabaseException {
        ConnectionString connectionString;
        try {
            connectionString = new ConnectionString(config.getDatabaseConnection());
        } catch (IllegalArgumentException ex) {
            throw new DatabaseException(ex);
        }
        if (client == null) {
            client = MongoClients.create(connectionString);
        }
        String databaseName = connectionString.getDatabase();
        if (databaseName == null) {
            throw new DatabaseException("A database name is not specified in a connection string");
        }
        database = client.getDatabase(connectionString.getDatabase());
        MongoViews.createViews(database, plugin.getLogger());
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
            database = null;
            client = null;
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
                    .map(StageDeserializer.Version::readStage)
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
            BsonDocument bson = StageSerializer.getInstance().writeStage(stage);
            coll.updateOne(Filters.eq(FIELD_ID, bson.get(FIELD_ID)),
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
            getStageCollection().deleteOne(Filters.eq(FIELD_ID, stage.getName()),
                    new MongoDBErrorCallback<>(callback)
            );
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    private MongoCollection<BsonValue> getTrophyCollection() throws DatabaseException {
        if (database == null) {
            throw new DatabaseException("Not connected");
        }
        return database.getCollection(COLLECTION_TROPHY).withDocumentClass(BsonValue.class);
    }

    @Override
    public void loadTrophies(DatabaseCallback<Trophy, RuntimeException> consumer, DatabaseCallback<Void, DatabaseException> callback) {
        try {
            getTrophyCollection().find()
                    .map(BsonDocument.class::cast)
                    .map(bson -> TrophyDeserializer.Version.readTrophy(plugin.getAPI().getRegistry(), bson))
                    .forEach(
                            new MongoDBResultCallback<>(consumer),
                            new MongoDBErrorCallback<>(callback)
                    );
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    @Override
    public void saveTrophy(Trophy trophy, DatabaseCallback<Void, DatabaseException> callback) {
        try {
            MongoCollection<BsonValue> coll = getTrophyCollection();
            BsonDocument bson = TrophySerializer.getInstance().writeTrophy(trophy);
            coll.updateOne(Filters.eq(FIELD_ID, bson.get(FIELD_ID)),
                    new BsonDocument(SET, bson),
                    new UpdateOptions().upsert(true),
                    new MongoDBErrorCallback<>(callback)
            );
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    @Override
    public void deleteTrophy(Trophy trophy, DatabaseCallback<Void, DatabaseException> callback) {
        try {
            getTrophyCollection().deleteOne(Filters.eq(FIELD_ID, trophy.getName()),
                    new MongoDBErrorCallback<>(callback)
            );
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    private MongoCollection<BsonValue> getKitCollection() throws DatabaseException {
        if (database == null) {
            throw new DatabaseException("Not connected");
        }
        return database.getCollection(COLLECTION_KIT).withDocumentClass(BsonValue.class);
    }

    @Override
    public void loadKits(DatabaseCallback<Kit, RuntimeException> consumer, DatabaseCallback<Void, DatabaseException> callback) {
        try {
            getKitCollection().find()
                    .map(BsonDocument.class::cast)
                    .map(KitDeserializer.Version::readKit)
                    .forEach(
                            new MongoDBResultCallback<>(consumer),
                            new MongoDBErrorCallback<>(callback)
                    );
        } catch (UncheckedIOException ex) {
            callback.call(DatabaseResult.fail(new DatabaseException(ex)));
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    @Override
    public void saveKit(Kit kit, DatabaseCallback<Void, DatabaseException> callback) {
        try {
            MongoCollection<BsonValue> coll = getKitCollection();
            BsonDocument bson = KitSerializer.getInstance().writeKit(kit);
            coll.updateOne(Filters.eq(FIELD_ID, bson.get(FIELD_ID)),
                    new BsonDocument(SET, bson),
                    new UpdateOptions().upsert(true),
                    new MongoDBErrorCallback<>(callback)
            );
        } catch (UncheckedIOException ex) {
            callback.call(DatabaseResult.fail(new DatabaseException(ex)));
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    @Override
    public void deleteKit(Kit kit, DatabaseCallback<Void, DatabaseException> callback) {
        try {
            getKitCollection().deleteOne(Filters.eq(FIELD_ID, kit.getName()),
                    new MongoDBErrorCallback<>(callback)
            );
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    private MongoCollection<Document> getRecordCollection() throws DatabaseException {
        if (database == null) {
            throw new DatabaseException("Not connected");
        }
        return database.getCollection(COLLECTION_RECORD);
    }

    @Override
    public void saveReocrds(RecordStream records, DatabaseCallback<Void, DatabaseException> callback) {
        if (records.getRecords().isEmpty()) {
            return;
        }
        try {
            MongoCollection<Document> coll = getRecordCollection();
            coll.insertMany(records.getDocuments(), new MongoDBErrorCallback<>(callback));
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    @Override
    public void loadPlayerStat(UUID player, DatabaseCallback<Map.Entry<RecordType, StatEntry>, RuntimeException> consumer, DatabaseCallback<Void, DatabaseException> callback) {
        if (database == null) {
            callback.call(DatabaseResult.fail(new DatabaseException("Not connected")));
            return;
        }
        MongoCollection<Document> coll = database.getCollection(VIEW_PLAYER_STATS);
        coll.find(new Document(FIELD_ID + FIELD_SEPARATOR + PlayerRecord.FIELD_PLAYER, player))
                .map(d -> MapUtils.tuple(
                RecordType.of(d.get(FIELD_ID, Document.class).getString(RecordType.FIELD_TYPE)),
                new StatEntry(d.getInteger(FIELD_COUNT, 0), getDouble(d, ScoreRecord.FIELD_SCORE))
        )).forEach(new MongoDBResultCallback<>(consumer), new MongoDBErrorCallback<>(callback));
    }

    @Override
    public void loadPlayerExp(UUID player, DatabaseCallback<Long, DatabaseException> callback) {
        if (database == null) {
            callback.call(DatabaseResult.fail(new DatabaseException("Not connected")));
            return;
        }
        MongoCollection<Document> coll = database.getCollection(VIEW_PLAYER_EXP);
        coll.find(new Document(FIELD_ID, player))
                .map(d -> d == null ? null : d.getLong(ExpRecord.FIELD_EXP))
                .first(new MongoDBCallback<>(callback));
    }

    @Override
    public void loadPlayerVibe(UUID player, DatabaseCallback<Double, DatabaseException> callback) {
        if (database == null) {
            callback.call(DatabaseResult.fail(new DatabaseException("Not connected")));
            return;
        }
        MongoCollection<Document> coll = database.getCollection(VIEW_PLAYER_VIBE);
        coll.find(new Document(FIELD_ID, player))
                .map(d -> d == null ? null : d.getDouble(PlayerResultRecord.FIELD_VIBE))
                .first(new MongoDBCallback<>(callback));
    }

    @Override
    public void loadStageStat(String stage, DatabaseCallback<Map.Entry<RecordType, StatEntry>, RuntimeException> consumer, DatabaseCallback<Void, DatabaseException> callback) {
        if (database == null) {
            callback.call(DatabaseResult.fail(new DatabaseException("Not connected")));
            return;
        }
        MongoCollection<Document> coll = database.getCollection(VIEW_STAGE_STATS);
        coll.find(new Document(FIELD_ID + FIELD_SEPARATOR + GameStartRecord.FIELD_STAGE, stage))
                .map(d -> MapUtils.tuple(
                RecordType.of(d.get(FIELD_ID, Document.class).getString(RecordType.FIELD_TYPE)),
                new StatEntry(d.getInteger(FIELD_COUNT, 0), getDouble(d, ScoreRecord.FIELD_SCORE))
        )).forEach(new MongoDBResultCallback<>(consumer), new MongoDBErrorCallback<>(callback));
    }

    private MongoCollection<BsonValue> getAccountCollection() throws DatabaseException {
        if (database == null) {
            throw new DatabaseException("Not connected");
        }
        return database.getCollection(COLLECTION_ACCOUNT).withDocumentClass(BsonValue.class);
    }

    @Override
    public void loadPlayerAccount(UUID player, DatabaseCallback<Account, DatabaseException> callback) {
        try {
            getAccountCollection().find(new Document(FIELD_ID, player))
                    .map(BsonDocument.class::cast)
                    .map(AccountDeserializer.Version::readAccount)
                    .first(new MongoDBCallback<>(callback));
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    @Override
    public void savePlayerAccount(Account account, DatabaseCallback<Void, DatabaseException> callback) {
        try {
            MongoCollection<BsonValue> coll = getAccountCollection();
            BsonDocument bson = AccountSerializer.getInstance().writeAccount(account);
            coll.updateOne(Filters.eq(FIELD_ID, bson.get(FIELD_ID)),
                    new BsonDocument(SET, bson),
                    new UpdateOptions().upsert(true),
                    new MongoDBErrorCallback<>(callback)
            );
        } catch (UncheckedIOException ex) {
            callback.call(DatabaseResult.fail(new DatabaseException(ex)));
        } catch (DatabaseException ex) {
            callback.call(DatabaseResult.fail(ex));
        }
    }

    private static Double getDouble(Document doc, String key) {
        Object obj = doc.get(key);
        if (obj instanceof Integer) {
            return ((Integer) obj).doubleValue();
        } else if (obj instanceof Double) {
            return (Double) obj;
        } else {
            return null;
        }
    }

}
