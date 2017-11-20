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

import jp.llv.flaggame.api.player.Account;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;

/**
 *
 * @author toyblocks
 */
public class AccountSerializer extends BaseSerializer {

    public static final int VERSION = 0;
    private static AccountSerializer instance;

    AccountSerializer() {
    }

    public BsonDocument writeAccount(Account value) {
        BsonDocument section = new BsonDocument();
        writeUUID(section, "_id", value.getUUID());
        writeString(section, "name", value.getName());
        section.put(AccountDeserializer.Version.FIELD_NAME, new BsonInt32(VERSION));
        writeString(section, "nick0", value.getNick(0));
        writeString(section, "nick1", value.getNick(1));
        writeString(section, "nick2", value.getNick(2));
        section.append("balance", new BsonDouble(value.getBalance().get()));
        writeString(section, "kit", value.getKit());
        
        writeCollection(section, "unlocked_nick0", value.getUnlockedNicks(0), super::writeString);
        writeCollection(section, "unlocked_nick1", value.getUnlockedNicks(1), super::writeString);
        writeCollection(section, "unlocked_nick2", value.getUnlockedNicks(2), super::writeString);
        writeCollection(section, "unlocked_kits", value.getUnlockedKits(), super::writeString);
        writeCollection(section, "unlocked_torophies", value.getUnlockedTrophies(), super::writeString);
        return section;
    }

    public static AccountSerializer getInstance() {
        if (instance == null) {
            instance = new AccountSerializer();
        }
        return instance;
    }
    
}
