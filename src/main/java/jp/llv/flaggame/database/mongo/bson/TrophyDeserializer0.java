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
import jp.llv.flaggame.api.player.NickPosition;
import jp.llv.flaggame.api.profile.RecordType;
import jp.llv.flaggame.trophy.ImpossibleTrophy;
import jp.llv.flaggame.trophy.NashornTrophy;
import jp.llv.flaggame.trophy.RecordTrophy;
import org.bson.BsonDocument;
import jp.llv.flaggame.api.trophy.Trophy;

/**
 *
 * @author toyblocks
 */
public class TrophyDeserializer0 extends BaseDeserializer implements TrophyDeserializer {

    void readRecordTrophy(RecordTrophy trophy, BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        trophy.setTarget(readEnum(section, "target", RecordType.class));
    }
    
    void readNashornTrophy(NashornTrophy trophy, BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        trophy.setScript(section.get("script").asJavaScript().getCode());
        if (trophy instanceof RecordTrophy) {
            readRecordTrophy((RecordTrophy) trophy, section, "record");
        }
    }
    
    @Override
    public Trophy readTrophy(FlagGameRegistry registry, BsonDocument bson) {
        Trophy trophy;
        try {
            trophy = registry
                    .getTrophy(readString(bson, "_id"))
                    .apply(readString(bson, "type"));
        } catch (NotRegisteredException ex) {
            trophy = new ImpossibleTrophy(readString(bson, "type"));
        }
        trophy.addRewardKits(readSet(bson, "reward-kits", super::readString));
        trophy.addRewardNicks(NickPosition.COLOR, readSet(bson, "reward-nick0", super::readString));
        trophy.addRewardNicks(NickPosition.ADJ, readSet(bson, "reward-nick1", super::readString));
        trophy.addRewardNicks(NickPosition.NOUN, readSet(bson, "reward-nick2", super::readString));
        trophy.setRewardMoney(bson.getDouble("reward-money").getValue());
        trophy.setRewardBits(bson.getDouble("reward-bits").getValue());
        if (trophy instanceof NashornTrophy) {
            readNashornTrophy((NashornTrophy) trophy, bson, "nashorn");
        }
        return trophy;
    }

}
