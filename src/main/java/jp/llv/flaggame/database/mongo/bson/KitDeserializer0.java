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
import java.util.Map;
import jp.llv.flaggame.api.kit.Kit;
import jp.llv.nbt.tag.TagCompound;
import org.bson.BsonDocument;

/**
 *
 * @author toyblocks
 */
public class KitDeserializer0 extends BaseDeserializer implements KitDeserializer {

    KitDeserializer0() {
    }

    @Override
    public Kit readKit(BsonDocument bson) throws UncheckedIOException {
        String name = bson.getString("_id").getValue();

        TagCompound icon = new TagCompound();
        TagCompound inventory = new TagCompound();
        TagCompound enderchest = new TagCompound();
        try {
            icon.read(bson.getBinary("icon").getData(), true);
            inventory.read(bson.getBinary("inventory").getData(), true);
            enderchest.read(bson.getBinary("enderchest").getData(), true);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        Map<String, Integer> effects = readMap(bson, "effects", this::readInt);
        return new Kit(name, icon, inventory, enderchest, effects);
    }
}
