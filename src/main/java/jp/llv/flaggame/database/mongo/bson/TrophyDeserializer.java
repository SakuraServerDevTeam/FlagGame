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

import java.util.function.Supplier;
import jp.llv.flaggame.api.FlagGameRegistry;
import org.bson.BsonDocument;
import jp.llv.flaggame.api.trophy.Trophy;

/**
 *
 * @author toyblocks
 */
public interface TrophyDeserializer {

    Trophy readTrophy(FlagGameRegistry registry, BsonDocument bson);

    enum Version {

        V0(TrophyDeserializer0::new),;

        public static final String FIELD_NAME = "serial_version";

        private final Supplier<? extends TrophyDeserializer> constructor;
        private TrophyDeserializer instance;

        private Version(Supplier<? extends TrophyDeserializer> constructor) {
            this.constructor = constructor;
        }

        public TrophyDeserializer getInstance() {
            if (instance == null) {
                instance = constructor.get();
            }
            return instance;
        }

        public static Trophy readTrophy(FlagGameRegistry registry, BsonDocument bson) {
            if (bson.containsKey(FIELD_NAME)) {
                return values()[bson.getInt32(FIELD_NAME).getValue()]
                        .getInstance().readTrophy(registry, bson);
            } else {
                return V0.getInstance().readTrophy(registry, bson);
            }
        }

    }

}
