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

import java.io.IOException;
import java.io.UncheckedIOException;
import jp.llv.flaggame.api.kit.Kit;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;

/**
 *
 * @author toyblocks
 */
public final class KitSerializer extends BaseSerializer {

    public static final int VERSION = 0;
    private static KitSerializer instance;

    KitSerializer() {
    }

    public BsonDocument writeKit(Kit value) throws UncheckedIOException {
        BsonDocument section = new BsonDocument();
        section.append("_id", new BsonString(value.getName()));
        section.append(KitDeserializer.Version.FIELD_NAME, new BsonInt32(VERSION));
        try {
            section.append("icon", new BsonBinary(value.getIcon().write(true)));
            section.append("inventory", new BsonBinary(value.getInventory().write(true)));
            section.append("enderchest", new BsonBinary(value.getEnderchest().write(true)));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        writeMap(section, "effects", value.getEffects(), this::writeInt);
        return section;
    }

    public static KitSerializer getInstance() {
        if (instance == null) {
            instance = new KitSerializer();
        }
        return instance;
    }

}
