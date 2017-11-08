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
package jp.llv.flaggame.database.mongo.bson;

import java.util.Collection;
import java.util.Map;
import jp.llv.flaggame.util.function.TriConsumer;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;

/**
 *
 * @author toyblocks
 */
public class BaseSerializer {

    BaseSerializer() {
    }

    void writeString(BsonDocument bson, String key, String value) {
        bson.append(key, new BsonString(value));
    }
    
    void writeInt(BsonDocument bson, String key, Integer value) {
        bson.append(key, new BsonInt32(value));
    }

    <E extends Enum<E>> void writeEnum(BsonDocument bson, String key, E value) {
        if (value == null) {
            return;
        }
        bson.append(key, new BsonString(value.name()));
    }
    <K extends Enum<K>, V> void writeEnumMap(BsonDocument bson, String key, Map<K, ? extends V> value, TriConsumer<BsonDocument, String, V> writer) {
        BsonDocument section = new BsonDocument();
        for (Map.Entry<K, ? extends V> entry : value.entrySet()) {
            writer.accept(section, entry.getKey().toString(), entry.getValue());
        }
        bson.append(key, section);
    }

    <V> void writeMap(BsonDocument bson, String key, Map<String, ? extends V> value, TriConsumer<BsonDocument, String, V> writer) {
        BsonDocument section = new BsonDocument();
        for (Map.Entry<String, ? extends V> entry : value.entrySet()) {
            writer.accept(section, entry.getKey(), entry.getValue());
        }
        bson.append(key, section);
    }

    <T> void writeCollection(BsonDocument bson, String key, Collection<? extends T> value, TriConsumer<BsonDocument, String, T> writer) {
        BsonDocument section = new BsonDocument();
        int i = 0;
        for (T t : value) {
            writer.accept(section, Integer.toString(i++), t);
        }
        bson.append(key, section);
    }
    
}
