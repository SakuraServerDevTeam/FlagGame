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

import jp.llv.flaggame.api.FlagGameRegistry;
import jp.llv.flaggame.api.exception.NotRegisteredException;
import jp.llv.flaggame.api.profile.RecordType;
import jp.llv.flaggame.api.trophie.Trophie;
import jp.llv.flaggame.trophie.ImpossibleTrophie;
import jp.llv.flaggame.trophie.NashornTrophie;
import jp.llv.flaggame.trophie.RecordTrophie;
import org.bson.BsonDocument;

/**
 *
 * @author toyblocks
 */
public class TrophieDeserializer0 extends BaseDeserializer implements TrophieDeserializer {

    void readRecordTrophie(RecordTrophie trophie, BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        trophie.setTarget(readEnum(section, "target", RecordType.class));
    }
    
    void readNashornTrophie(NashornTrophie trophie, BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        trophie.setScript(section.get("script").asJavaScript().getCode());
        if (trophie instanceof RecordTrophie) {
            readRecordTrophie((RecordTrophie) trophie, section, "record");
        }
    }
    
    @Override
    public Trophie readTrophie(FlagGameRegistry registry, BsonDocument bson) {
        Trophie trophie;
        try {
            trophie = registry
                    .getTrophie(readString(bson, "_id"))
                    .apply(readString(bson, "type"));
        } catch (NotRegisteredException ex) {
            trophie = new ImpossibleTrophie(readString(bson, "type"));
        }
        trophie.addRewardKits(readSet(bson, "reward-kits", super::readString));
        trophie.addRewardNicks(0, readSet(bson, "reward-nick0", super::readString));
        trophie.addRewardNicks(1, readSet(bson, "reward-nick1", super::readString));
        trophie.addRewardNicks(2, readSet(bson, "reward-nick2", super::readString));
        trophie.setRewardMoney(bson.getDouble("reward-money").getValue());
        trophie.setRewardBits(bson.getDouble("reward-bits").getValue());
        if (trophie instanceof NashornTrophie) {
            readNashornTrophie((NashornTrophie) trophie, bson, "nashorn");
        }
        return trophie;
    }

}
