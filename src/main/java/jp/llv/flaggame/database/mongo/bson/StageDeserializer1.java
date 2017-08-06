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

import jp.llv.flaggame.api.exception.ObjectiveCollisionException;
import jp.llv.flaggame.api.exception.ReservedException;
import jp.llv.flaggame.api.stage.objective.Spawn;
import jp.llv.flaggame.api.stage.objective.SpecSpawn;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.stage.BasicStage;
import org.bson.BsonDocument;
import org.bukkit.Location;

/**
 *
 * @author toyblocks
 */
public class StageDeserializer1 extends StageDeserializer0 {

    Spawn readSpawn(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        TeamColor color = readEnum(section, "color", TeamColor.class);
        return new Spawn(loc, color);
    }

    SpecSpawn readSpecSpawn(BsonDocument bson, String key) {
        Location loc = readLocation(bson, key);
        return new SpecSpawn(loc);
    }

    @Override
    public BasicStage readStage(BsonDocument bson) {
        try {
            BasicStage stage = new BasicStage(bson.getString("_id").getValue());
            stage.setGameTime(bson.getInt64("time").getValue());
            stage.setTeamLimit(bson.getInt32("teamlimit").getValue());
            stage.setProtected(bson.getBoolean("protected").getValue());
            stage.setAvailable(bson.getBoolean("available").getValue());
            stage.setKillScore(bson.getDouble("kill").getValue());
            stage.setDeathScore(bson.getDouble("death").getValue());
            stage.setEntryFee(bson.getDouble("entryfee").getValue());
            stage.setPrize(bson.getDouble("prize").getValue());
            stage.setCooldown(bson.getInt64("cooldown").getValue());
            stage.addObjectives(readList(bson, "spawns", this::readSpawn));
            stage.addObjectives(readList(bson, "specspawns", this::readSpecSpawn));
            stage.addObjectives(readList(bson, "flags", this::readFlag));
            stage.addObjectives(readList(bson, "nexuses", this::readNexus));
            stage.addObjectives(readList(bson, "banner-spawners", this::readBannerSpawner));
            stage.addObjectives(readList(bson, "banner-slots", this::readBannerSlot));
            stage.addObjectives(readList(bson, "containers", this::readGameChest));
            stage.addObjectives(readList(bson, "superjumps", this::readSuperJump));
            stage.setAreas(readAreaSet(bson, "areas"));
            stage.setAuthor(bson.getString("author").getValue());
            stage.setDescription(bson.getString("description").getValue());
            stage.setGuide(bson.getString("guide").getValue());
            stage.setTags(readSet(bson, "tags", (b, k) -> b.getString(k).getValue()));
            return stage;
        } catch (ReservedException | ObjectiveCollisionException ex) {
            throw new RuntimeException(ex);
        }
    }

}
