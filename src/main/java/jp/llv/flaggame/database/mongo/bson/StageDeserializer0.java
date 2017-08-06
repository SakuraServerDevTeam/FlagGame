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

import java.util.Map;
import jp.llv.flaggame.api.exception.ObjectiveCollisionException;
import jp.llv.flaggame.api.exception.ReservedException;
import jp.llv.flaggame.api.stage.area.GameMessageType;
import jp.llv.flaggame.api.stage.objective.BannerSlot;
import jp.llv.flaggame.api.stage.objective.BannerSpawner;
import jp.llv.flaggame.api.stage.objective.Flag;
import jp.llv.flaggame.api.stage.objective.GameChest;
import jp.llv.flaggame.api.stage.objective.Nexus;
import jp.llv.flaggame.api.stage.objective.Spawn;
import jp.llv.flaggame.api.stage.objective.SpecSpawn;
import jp.llv.flaggame.api.stage.objective.SuperJump;
import jp.llv.flaggame.api.stage.permission.GamePermission;
import jp.llv.flaggame.api.stage.permission.GamePermissionState;
import jp.llv.flaggame.api.stage.rollback.StageDataType;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.stage.AreaInfo;
import jp.llv.flaggame.stage.AreaSet;
import jp.llv.flaggame.stage.BasicStage;
import jp.llv.flaggame.stage.permission.GamePermissionStateSet;
import org.bson.BsonDocument;
import org.bson.BsonInvalidOperationException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author toyblocks
 */
public class StageDeserializer0 extends BaseDeserializer implements StageDeserializer {

    StageDeserializer0() {
    }

    Vector readVector(BsonDocument bson, String key) {
        try {
            BsonDocument section = bson.getDocument(key);
            double x = section.getDouble("x").getValue();
            double y = section.getDouble("y").getValue();
            double z = section.getDouble("z").getValue();
            return new Vector(x, y, z);
        } catch (BsonInvalidOperationException ex) {
            return null;
        }
    }

    Location readLocation(BsonDocument bson, String key) {
        try {
            BsonDocument section = bson.getDocument(key);
            String world = section.getString("world").getValue();
            double x = section.getDouble("x").getValue();
            double y = section.getDouble("y").getValue();
            double z = section.getDouble("z").getValue();
            float yaw = (float) section.getDouble("yaw").getValue();
            float pitch = (float) section.getDouble("pitch").getValue();
            return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        } catch (BsonInvalidOperationException ex) {
            return null;
        }
    }

    Cuboid readCuboid(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        return new Cuboid(readLocation(section, "pos1"), readLocation(section, "pos2"));
    }

    AreaInfo.RollbackData readRollback(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        AreaInfo.RollbackData result = new AreaInfo.RollbackData();
        result.setTiming(section.getInt64("timing").getValue());
        result.setTarget(readEnum(section, "target", StageDataType.class).newInstance());
        result.setData(section.getBinary("data").getData());
        return result;
    }

    GamePermissionStateSet readGamePermissionStateSet(BsonDocument bson, String key) {
        return new GamePermissionStateSet(
                readEnumMap(bson, key, TeamColor.class, (b, k) -> readEnum(b, k, GamePermissionState.class))
        );
    }

    AreaInfo.MessageData readMessageData(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        AreaInfo.MessageData result = new AreaInfo.MessageData();
        result.setTiming(section.getInt64("timing").getValue());
        result.setType(readEnum(section, "type", GameMessageType.class));
        result.setMessage(section.getString("message").getValue());
        return result;
    }

    AreaInfo readAreaInfo(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        AreaInfo result = new AreaInfo();
        result.setRollbacks(readMap(section, "rollbacks", this::readRollback));
        result.setPermissions(readEnumMap(section, "permissions", GamePermission.class, this::readGamePermissionStateSet));
        result.setMessages(readList(section, "messages", this::readMessageData));
        return result;
    }

    AreaSet readAreaSet(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        AreaSet result = new AreaSet();
        result.setAreaMap(readMap(section, "areas", this::readCuboid));
        result.setAreaInfoMap(readMap(section, "info", this::readAreaInfo));
        return result;
    }

    Flag readFlag(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        double point = section.getDouble("point").getValue();
        boolean producing = section.getBoolean("producing").getValue();
        return new Flag(loc, point, producing);
    }

    Nexus readNexus(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        TeamColor color = readEnum(section, "color", TeamColor.class);
        double point = section.getDouble("point").getValue();
        return new Nexus(loc, color, point);
    }

    BannerSlot readBannerSlot(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        TeamColor color = readEnum(section, "color", TeamColor.class);
        return new BannerSlot(loc, color);
    }

    BannerSpawner readBannerSpawner(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        byte point = (byte) section.getInt32("point").getValue();
        byte hp = (byte) section.getInt32("hp").getValue();
        boolean producing = section.getBoolean("producing").getValue();
        boolean wall = section.getBoolean("wall").getValue();
        BlockFace face = readEnum(section, "face", BlockFace.class);
        return new BannerSpawner(loc, point, hp, producing, wall, face);
    }

    GameChest readGameChest(BsonDocument bson, String key) {
        Location loc = readLocation(bson, key);
        return new GameChest(loc);
    }

    SuperJump readSuperJump(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        double range = section.getDouble("range").getValue();
        Vector velocity = readVector(section, "velocity");
        return new SuperJump(loc, range, velocity);
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
            for (Map.Entry<TeamColor, Location> entry : readEnumMap(bson, "spawn", TeamColor.class, this::readLocation).entrySet()) {
                stage.addObjective(new Spawn(entry.getValue(), entry.getKey()));
            }
            stage.addObjective(new SpecSpawn(readLocation(bson, "specspawn")));
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
            return stage;
        } catch (ReservedException | ObjectiveCollisionException ex) {
            throw new RuntimeException(ex);
        }
    }

}
