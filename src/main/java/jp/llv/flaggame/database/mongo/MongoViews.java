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

import static jp.llv.flaggame.profile.record.GameRecord.FIELD_TIME;
import static jp.llv.flaggame.profile.record.GameRecord.FIELD_GAME;
import static jp.llv.flaggame.profile.record.GameStartRecord.FIELD_STAGE;
import static jp.llv.flaggame.profile.record.PlayerRecord.FIELD_PLAYER;
import static jp.llv.flaggame.profile.record.ScoreRecord.FIELD_SCORE;
import static jp.llv.flaggame.profile.record.ExpRecord.FIELD_EXP;
import static jp.llv.flaggame.profile.record.PlayerResultRecord.FIELD_VIBE;
import static jp.llv.flaggame.profile.record.RecordType.FIELD_TYPE;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Accumulators.push;
import static com.mongodb.client.model.Accumulators.max;
import static com.mongodb.client.model.Accumulators.first;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.orderBy;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Projections.fields;
import static jp.llv.flaggame.database.mongo.MongoDB.FIELD_ID;
import static jp.llv.flaggame.database.mongo.MongoDB.COLLECTION_RECORD;
import static jp.llv.flaggame.database.mongo.MongoDB.FIELD_COUNT;
import static jp.llv.flaggame.database.mongo.MongoDB.VIEW_GAME_HISTORY;
import static jp.llv.flaggame.database.mongo.MongoDB.VIEW_PLAYER_EXP;
import static jp.llv.flaggame.database.mongo.MongoDB.VIEW_PLAYER_STATS;
import static jp.llv.flaggame.database.mongo.MongoDB.VIEW_PLAYER_VIBE;
import static jp.llv.flaggame.database.mongo.MongoDB.VIEW_STAGE_STATS;
import com.mongodb.async.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.profile.record.RecordType;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author SakuraServerDev
 */
public final class MongoViews {

    private static final int VIBE_SLICE_COUNT = 10;
    private static final char FIELD_PREFIX = '$';
    private static final char FIELD_SEPARATOR = '.';
    private static final String FIELD_HISTORY = "history";

    static final List<Bson> PIPELINE_PLAYER_STATS = Collections.unmodifiableList(Arrays.asList(
            match(ne(FIELD_PLAYER, null)),
            group(
                    new Document(FIELD_PLAYER, FIELD_PREFIX + FIELD_PLAYER)
                    .append(RecordType.FIELD_TYPE, FIELD_PREFIX + FIELD_TYPE),
                    sum(FIELD_COUNT, 1),
                    sum(FIELD_SCORE, FIELD_PREFIX + FIELD_SCORE)
            )
    ));
    static final List<Bson> PIPELINE_PLAYER_EXP = Collections.unmodifiableList(Arrays.asList(
            match(ne(FIELD_EXP, null)),
            group(
                    FIELD_PREFIX + FIELD_PLAYER,
                    sum(FIELD_EXP, FIELD_PREFIX + FIELD_EXP)
            )
    ));
    static final List<Bson> PIPELINE_PLAYER_VIBE = Collections.unmodifiableList(Arrays.asList(match(ne(FIELD_VIBE, null)),
            sort(orderBy(descending(FIELD_TIME))),
            group(
                    FIELD_PREFIX + FIELD_PLAYER,
                    push(FIELD_VIBE, FIELD_PREFIX + FIELD_VIBE)
            ),
            project(fields(slice(FIELD_VIBE, VIBE_SLICE_COUNT))),
            unwind(FIELD_PREFIX + FIELD_VIBE),
            group(FIELD_PREFIX + FIELD_ID,
                    sum(FIELD_VIBE, FIELD_PREFIX + FIELD_VIBE)
            )
    ));
    static final List<Bson> PIPELINE_GAME_HISTORY = Collections.unmodifiableList(Arrays.asList(
            match(eq(FIELD_TYPE, RecordType.GAME_START.toString())),
            group(
                    FIELD_PREFIX + FIELD_GAME,
                    max(FIELD_STAGE, FIELD_PREFIX + FIELD_STAGE),
                    first(FIELD_TIME, FIELD_PREFIX + FIELD_TIME)
            )
    ));
    static final List<Bson> PIPELINE_STAGE_STATS = Collections.unmodifiableList(Arrays.asList(group(
            new Document(FIELD_GAME, FIELD_PREFIX + FIELD_GAME)
            .append(RecordType.FIELD_TYPE, FIELD_PREFIX + FIELD_TYPE),
            sum(FIELD_COUNT, 1),
            sum(FIELD_SCORE, FIELD_PREFIX + FIELD_SCORE)
    ),
            lookup(VIEW_GAME_HISTORY, FIELD_ID + FIELD_SEPARATOR + FIELD_GAME, FIELD_ID, FIELD_HISTORY),
            unwind(FIELD_PREFIX + FIELD_HISTORY),
            group(new Document(FIELD_STAGE, FIELD_PREFIX + FIELD_HISTORY + FIELD_SEPARATOR + FIELD_STAGE)
                    .append(RecordType.FIELD_TYPE, FIELD_PREFIX + FIELD_ID + FIELD_SEPARATOR + FIELD_TYPE),
                    sum(FIELD_COUNT, FIELD_PREFIX + FIELD_COUNT),
                    sum(FIELD_SCORE, FIELD_PREFIX + FIELD_SCORE)
            )
    ));

    private MongoViews() {
        throw new RuntimeException();
    }

    private static Bson slice(String field, int limit) {
        return new BsonDocument(field, new BsonDocument("$slice", new BsonArray(Arrays.asList(
                new BsonString(FIELD_PREFIX + field),
                new BsonInt32(limit)
        ))));
    }

    static void createViews(MongoDatabase database, Logger logger) throws DatabaseException {
        List<String> names = new ArrayList<>();
        database.listCollectionNames().forEach(names::add, (result, ex0) -> {
            if (ex0 != null) {
                logger.log(Level.WARNING, "Failed to check namespace", ex0);
                return;
            }
            if (!names.contains(VIEW_PLAYER_STATS)) {
                database.createView(VIEW_PLAYER_STATS, COLLECTION_RECORD, PIPELINE_PLAYER_STATS, (r, ex1) -> {
                    if (ex1 != null) {
                        logger.log(Level.WARNING, "Failed to create player stats view", ex1);
                    }
                });

            }
            if (!names.contains(VIEW_PLAYER_EXP)) {
                database.createView(VIEW_PLAYER_EXP, COLLECTION_RECORD, PIPELINE_PLAYER_EXP, (r, ex1) -> {
                    if (ex1 != null) {
                        logger.log(Level.WARNING, "Failed to create player ex1p view", ex1);
                    }
                });

            }
            if (!names.contains(VIEW_PLAYER_VIBE)) {
                database.createView(VIEW_PLAYER_VIBE, COLLECTION_RECORD, PIPELINE_PLAYER_VIBE, (r, ex1) -> {
                    if (ex1 != null) {
                        logger.log(Level.WARNING, "Failed to create player vibe view", ex1);
                    }
                });

            }
            if (!names.contains(VIEW_GAME_HISTORY)) {
                database.createView(VIEW_GAME_HISTORY, COLLECTION_RECORD, PIPELINE_GAME_HISTORY, (r, ex1) -> {
                    if (ex1 != null) {
                        logger.log(Level.WARNING, "Failed to create game history view", ex1);
                    }
                });

            }
            if (!names.contains(VIEW_STAGE_STATS)) {
                database.createView(VIEW_STAGE_STATS, COLLECTION_RECORD, PIPELINE_STAGE_STATS, (r, ex1) -> {
                    if (ex1 != null) {
                        logger.log(Level.WARNING, "Failed to create stage stats view", ex1);
                    }
                });
            }
        });
    }
}
