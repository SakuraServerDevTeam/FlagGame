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
import jp.llv.flaggame.api.trophie.Trophie;
import jp.llv.flaggame.trophie.NashornTrophie;
import jp.llv.flaggame.trophie.RecordTrophie;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonJavaScript;
import org.bson.BsonString;

/**
 *
 * @author toyblocks
 */
public class TrophieSerializer extends BaseSerializer {

    public static final int VERSION = 0;
    private static TrophieSerializer instance;

    private TrophieSerializer() {
    }

    void writeRecordTrophie(BsonDocument bson, String key, RecordTrophie value) {
        BsonDocument section = new BsonDocument();
        writeEnum(section, "target", value.getTarget());
        bson.put(key, section);
    }
    
    void writeNashornTrophie(BsonDocument bson, String key, NashornTrophie value) {
        BsonDocument section = new BsonDocument();
        section.put("script", new BsonJavaScript(value.getScript()));
        if (value instanceof RecordTrophie) {
            writeRecordTrophie(section, "record", (RecordTrophie) value);
        }
        bson.put(key, section);
    }
    
    public BsonDocument writeTrophie(Trophie value) throws UncheckedIOException {
        BsonDocument section = new BsonDocument();
        section.append("_id", new BsonString(value.getName()));
        section.append(TrophieDeserializer.Version.FIELD_NAME, new BsonInt32(VERSION));
        writeString(section, "type", value.getType());
        writeCollection(section, "reward-kits", value.getRewardKits(), super::writeString);
        writeCollection(section, "reward-nick0", value.getRewardNicks(0), super::writeString);
        writeCollection(section, "reward-nick1", value.getRewardNicks(1), super::writeString);
        writeCollection(section, "reward-nick2", value.getRewardNicks(2), super::writeString);
        section.append("reward-money", new BsonDouble(value.getRewardMoney()));
        section.append("reward-bits", new BsonDouble(value.getRewardBits()));
        if (value instanceof NashornTrophie) {
            writeNashornTrophie(section, "nashorn", (NashornTrophie) value);
        }
        return section;
    }

    public static TrophieSerializer getInstance() {
        if (instance == null) {
            instance = new TrophieSerializer();
        }
        return instance;
    }
    
}
