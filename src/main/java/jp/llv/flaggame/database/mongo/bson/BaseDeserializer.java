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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import org.bson.BsonDocument;
import org.bson.BsonInvalidOperationException;

/**
 *
 * @author toyblocks
 */
public class BaseDeserializer {

    BaseDeserializer() {
    }

    Integer readInt(BsonDocument bson, String key) {
        return bson.getInt32(key).getValue();
    }
    
    String readString(BsonDocument bson, String key) {
        return bson.getString(key).getValue();
    }

    <E extends Enum<E>> E readEnum(BsonDocument bson, String key, Class<E> clazz) {
        try {
            return Enum.valueOf(clazz, bson.getString(key).getValue());
        } catch (BsonInvalidOperationException ex) {
            return null;
        }
    }

    <K extends Enum<K>, V> EnumMap<K, V> readEnumMap(BsonDocument bson, String key, Class<K> clazz, BiFunction<BsonDocument, String, ? extends V> reader) {
        BsonDocument section = bson.getDocument(key);
        EnumMap<K, V> result = new EnumMap<>(clazz);
        for (String k : section.keySet()) {
            result.put(Enum.valueOf(clazz, k), reader.apply(section, k));
        }
        return result;
    }

    <V> Map<String, V> readMap(BsonDocument bson, String key, BiFunction<BsonDocument, String, ? extends V> reader) {
        if (!bson.containsKey(key)) {
            return Collections.emptyMap();
        }
        BsonDocument section = bson.getDocument(key);
        Map<String, V> result = new HashMap<>();
        for (String k : section.keySet()) {
            result.put(k, reader.apply(section, k));
        }
        return result;
    }

    <T> List<T> readList(BsonDocument bson, String key, BiFunction<BsonDocument, String, ? extends T> reader) {
        if (!bson.containsKey(key)) {
            return Collections.emptyList();
        }
        BsonDocument section = bson.getDocument(key);
        List<T> result = new ArrayList<>();
        for (String k : section.keySet()) {
            result.add(reader.apply(section, k));
        }
        return result;
    }

    <T> Set<T> readSet(BsonDocument bson, String key, BiFunction<BsonDocument, String, ? extends T> reader) {
        if (!bson.containsKey(key)) {
            return Collections.emptySet();
        }
        BsonDocument section = bson.getDocument(key);
        Set<T> result = new HashSet<>();
        for (String k : section.keySet()) {
            result.add(reader.apply(section, k));
        }
        return result;
    }
}
