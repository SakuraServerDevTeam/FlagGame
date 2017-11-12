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
import java.util.UUID;
import jp.llv.flaggame.util.function.TriConsumer;
import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
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
        if (value != null) {
            bson.append(key, new BsonString(value));
        }
    }

    void writeInt(BsonDocument bson, String key, Integer value) {
        if (value != null) {
            bson.append(key, new BsonInt32(value));
        }
    }

    /**
     * Write UUID IN STANDARD ENCODING.
     *
     * @param bson a bson document to write in
     * @param key an element name
     * @param value a UUID to be written
     */
    void writeUUID(BsonDocument bson, String key, UUID value) {
        byte[] binary = new byte[16];
        long most = value.getMostSignificantBits();
        for (int i = 0; i < 0b1000; i++) {
            binary[i] = (byte) (most >> (56 - (i << 3)));
        }
        long least = value.getLeastSignificantBits();
        for (int i = 0; i < 0b1000; i++) {
            binary[0b1000 | i] = (byte) (least >> (56 - (i << 3)));
        }
        bson.append(key, new BsonBinary(BsonBinarySubType.UUID_STANDARD, binary));
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
