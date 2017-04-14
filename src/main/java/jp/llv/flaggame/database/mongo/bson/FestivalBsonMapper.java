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

import static jp.llv.flaggame.database.mongo.bson.BsonMapper.*;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.reception.fest.FestivalMatch;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonString;

/**
 *
 * @author SakuraServerDev
 */
public final class FestivalBsonMapper {

    private FestivalBsonMapper() {
        throw new UnsupportedOperationException();
    }

    private static void writeMatch(BsonDocument bson, String key, FestivalMatch value) {
        BsonDocument section = new BsonDocument();
        section.append("stage", new BsonString(value.getStage()));
        writeEnumMap(section, "colors", value.getColorMapping(), BsonMapper::writeEnum);
        bson.append(key, section);
    }

    private static FestivalMatch readMatch(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        FestivalMatch result = new FestivalMatch();
        result.setStage(section.getString("stage").getValue());
        result.setColorMapping(readEnumMap(section, "colors", TeamColor.class, (b, k) -> readEnum(b, k, TeamColor.class)));
        return result;
    }

    public static BsonDocument writeSchedule(FestivalSchedule value) {
        BsonDocument section = new BsonDocument();
        section.append("_id", new BsonString(value.getName()));
        writeEnumMap(section, "teams", value.getTeams(), BsonMapper::writeString);
        writeList(section, "matches", value.getMatches(), (b, k, v) -> {
            writeList(b, k, v.values(), FestivalBsonMapper::writeMatch);
        });
        section.append("entryfee", new BsonDouble(value.getEntryFee()));
        section.append("prize", new BsonDouble(value.getPrize()));
        return section;
    }

    public static FestivalSchedule readSchedule(BsonDocument bson) {
        FestivalSchedule result = new FestivalSchedule(bson.getString("_id").getValue());
        result.setTeams(readEnumMap(bson, "teams", TeamColor.class, BsonMapper::readString));
        result.setMatches(readList(bson, "matches", (b, k) -> {
            return readList(b, k, FestivalBsonMapper::readMatch);
        }));
        result.setEntryFee(bson.getDouble("entryfee").getValue());
        result.setPrize(bson.getDouble("prize").getValue());
        return result;
    }

}
