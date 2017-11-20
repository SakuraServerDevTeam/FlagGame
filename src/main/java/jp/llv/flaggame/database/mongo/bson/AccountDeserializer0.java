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

import syam.flaggame.player.CachedAccount;
import jp.llv.flaggame.api.player.Account;
import org.bson.BsonDocument;

/**
 *
 * @author toyblocks
 */
public class AccountDeserializer0 extends BaseDeserializer implements AccountDeserializer {

    @Override
    public Account readAccount(BsonDocument bson) {
        CachedAccount result = new CachedAccount(readUUID(bson, "_id"));
        result.setName(readString(bson, "name"));
        result.setNick(0, readString(bson, "nick0"));
        result.setNick(1, readString(bson, "nick1"));
        result.setNick(2, readString(bson, "nick2"));
        result.getBalance().set(bson.getDouble("balance").doubleValue());
        result.setKit(readString(bson, "kit"));
        
        result.unlockNicks(0, readSet(bson, "unlocked_nick0", super::readString));
        result.unlockNicks(1, readSet(bson, "unlocked_nick1", super::readString));
        result.unlockNicks(2, readSet(bson, "unlocked_nick2", super::readString));
        result.unlockKits(readSet(bson, "unlocked_kits", super::readString));
        result.unlockTrophies(readSet(bson, "unlocked_torophies", super::readString));
        return result;
    }
    
}
