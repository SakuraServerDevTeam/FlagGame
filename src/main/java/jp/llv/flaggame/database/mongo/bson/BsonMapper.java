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
package jp.llv.flaggame.database.mongo.bson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import jp.llv.flaggame.util.function.TriConsumer;
import org.bson.BsonDocument;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonString;

/**
 *
 * @author SakuraServerDev
 */
public final class BsonMapper {
    
    private BsonMapper() {
        throw new UnsupportedOperationException();
    }

    static void writeString(BsonDocument bson, String key, String value) {
        bson.append(key, new BsonString(value));
    }
    
    static String readString(BsonDocument bson, String key) {
        return bson.getString(key).getValue();
    }
    
    static <E extends Enum<E>> void writeEnum(BsonDocument bson, String key, E value) {
        if (value == null) {
            return;
        }
        bson.append(key, new BsonString(value.name()));
    }

    static <E extends Enum<E>> E readEnum(BsonDocument bson, String key, Class<E> clazz) {
        try {
            return Enum.valueOf(clazz, bson.getString(key).getValue());
        } catch (BsonInvalidOperationException ex) {
            return null;
        }
    }

    static <K extends Enum<K>, V> void writeEnumMap(BsonDocument bson, String key, Map<K, ? extends V> value, TriConsumer<BsonDocument, String, V> writer) {
        BsonDocument section = new BsonDocument();
        for (Map.Entry<K, ? extends V> entry : value.entrySet()) {
            writer.accept(section, entry.getKey().toString(), entry.getValue());
        }
        bson.append(key, section);
    }

    static <K extends Enum<K>, V> EnumMap<K, V> readEnumMap(BsonDocument bson, String key, Class<K> clazz, BiFunction<BsonDocument, String, ? extends V> reader) {
        BsonDocument section = bson.getDocument(key);
        EnumMap<K, V> result = new EnumMap<>(clazz);
        for (String k : section.keySet()) {
            result.put(Enum.valueOf(clazz, k), reader.apply(section, k));
        }
        return result;
    }

    static <V> void writeMap(BsonDocument bson, String key, Map<String, ? extends V> value, TriConsumer<BsonDocument, String, V> writer) {
        BsonDocument section = new BsonDocument();
        for (Map.Entry<String, ? extends V> entry : value.entrySet()) {
            writer.accept(section, entry.getKey(), entry.getValue());
        }
        bson.append(key, section);
    }

    static <V> Map<String, V> readMap(BsonDocument bson, String key, BiFunction<BsonDocument, String, ? extends V> reader) {
        BsonDocument section = bson.getDocument(key);
        Map<String, V> result = new HashMap<>();
        for (String k : section.keySet()) {
            result.put(k, reader.apply(section, k));
        }
        return result;
    }

    static <T> void writeList(BsonDocument bson, String key, Collection<? extends T> value, TriConsumer<BsonDocument, String, T> writer) {
        BsonDocument section = new BsonDocument();
        int i = 0;
        for (T t : value) {
            writer.accept(section, Integer.toString(i++), t);
        }
        bson.append(key, section);
    }

    static <T> List<T> readList(BsonDocument bson, String key, BiFunction<BsonDocument, String, ? extends T> reader) {
        BsonDocument section = bson.getDocument(key);
        List<T> result = new ArrayList<>();
        for (String k : section.keySet()) {
            result.add(reader.apply(section, k));
        }
        return result;
    }
    
}
