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
import jp.llv.flaggame.api.stage.permission.GamePermission;
import jp.llv.flaggame.api.stage.objective.BannerSlot;
import jp.llv.flaggame.api.stage.objective.BannerSpawner;
import jp.llv.flaggame.api.stage.objective.Flag;
import jp.llv.flaggame.api.stage.objective.Nexus;
import jp.llv.flaggame.api.stage.permission.GamePermissionState;
import jp.llv.flaggame.stage.permission.GamePermissionStateSet;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.api.stage.rollback.StageDataType;
import org.bson.BsonBinary;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import jp.llv.flaggame.api.exception.ObjectiveCollisionException;
import jp.llv.flaggame.api.exception.ReservedException;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.stage.AreaInfo;
import jp.llv.flaggame.stage.AreaSet;
import jp.llv.flaggame.api.stage.area.GameMessageType;
import jp.llv.flaggame.api.stage.area.StageAreaInfo;
import jp.llv.flaggame.api.stage.area.StageAreaSet;
import jp.llv.flaggame.api.stage.permission.StagePermissionStateSet;
import jp.llv.flaggame.stage.BasicStage;
import jp.llv.flaggame.api.stage.objective.GameChest;
import jp.llv.flaggame.api.stage.objective.SuperJump;
import org.bukkit.util.Vector;
import syam.flaggame.util.Cuboid;

public final class StageBsonMapper {

    private StageBsonMapper() {
        throw new UnsupportedOperationException();
    }

    private static void writeVector(BsonDocument bson, String key, Vector value) {
        if (value == null) {
            return;
        }
        BsonDocument section = new BsonDocument();
        section.append("x", new BsonDouble(value.getX()));
        section.append("y", new BsonDouble(value.getY()));
        section.append("z", new BsonDouble(value.getZ()));
        bson.append(key, section);
    }

    private static Vector readVector(BsonDocument bson, String key) {
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

    private static void writeLocation(BsonDocument bson, String key, Location value) {
        if (value == null) {
            return;
        }
        BsonDocument section = new BsonDocument();
        section.append("world", new BsonString(value.getWorld().getName()));
        section.append("x", new BsonDouble(value.getX()));
        section.append("y", new BsonDouble(value.getY()));
        section.append("z", new BsonDouble(value.getZ()));
        section.append("yaw", new BsonDouble(value.getYaw()));
        section.append("pitch", new BsonDouble(value.getPitch()));
        bson.append(key, section);
    }

    private static Location readLocation(BsonDocument bson, String key) {
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

    private static void writeCuboid(BsonDocument bson, String key, Cuboid value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "pos1", value.getPos1());
        writeLocation(section, "pos2", value.getPos2());
        bson.append(key, section);
    }

    private static Cuboid readCuboid(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        return new Cuboid(readLocation(section, "pos1"), readLocation(section, "pos2"));
    }

    private static void writeRollback(BsonDocument bson, String key, StageAreaInfo.StageRollbackData value) {
        BsonDocument section = new BsonDocument();
        section.append("timing", new BsonInt64(value.getTiming()));
        writeEnum(section, "target", value.getTarget().getType());
        section.append("data", new BsonBinary(value.getData()));
        bson.append(key, section);
    }

    private static AreaInfo.RollbackData readRollback(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        AreaInfo.RollbackData result = new AreaInfo.RollbackData();
        result.setTiming(section.getInt64("timing").getValue());
        result.setTarget(readEnum(section, "target", StageDataType.class).newInstance());
        result.setData(section.getBinary("data").getData());
        return result;
    }

    private static void writeGamePermissionStateSet(BsonDocument bson, String key, StagePermissionStateSet value) {
        writeEnumMap(bson, key, value.getState(), (b, k, s) -> writeEnum(b, k, s));
    }

    private static GamePermissionStateSet readGamePermissionStateSet(BsonDocument bson, String key) {
        return new GamePermissionStateSet(
                readEnumMap(bson, key, TeamColor.class, (b, k) -> readEnum(b, k, GamePermissionState.class))
        );
    }

    private static void writeMessageData(BsonDocument bson, String key, StageAreaInfo.StageMessageData value) {
        BsonDocument section = new BsonDocument();
        section.append("timing", new BsonInt64(value.getTiming()));
        writeEnum(section, "type", value.getType());
        section.append("message", new BsonString(value.getMessage()));
        bson.append(key, section);
    }

    private static AreaInfo.MessageData readMessageData(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        AreaInfo.MessageData result = new AreaInfo.MessageData();
        result.setTiming(section.getInt64("timing").getValue());
        result.setType(readEnum(section, "type", GameMessageType.class));
        result.setMessage(section.getString("message").getValue());
        return result;
    }

    private static void writeAreaInfo(BsonDocument bson, String key, StageAreaInfo value) {
        BsonDocument section = new BsonDocument();
        writeMap(section, "rollbacks", value.getRollbacks(), StageBsonMapper::writeRollback);
        writeEnumMap(section, "permissions", value.getPermissions(), StageBsonMapper::writeGamePermissionStateSet);
        writeList(section, "messages", value.getMessages(), StageBsonMapper::writeMessageData);
        bson.append(key, section);
    }

    private static AreaInfo readAreaInfo(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        AreaInfo result = new AreaInfo();
        result.setRollbacks(readMap(section, "rollbacks", StageBsonMapper::readRollback));
        result.setPermissions(readEnumMap(section, "permissions", GamePermission.class, StageBsonMapper::readGamePermissionStateSet));
        result.setMessages(readList(section, "messages", StageBsonMapper::readMessageData));
        return result;
    }

    private static void writeAreaSet(BsonDocument bson, String key, StageAreaSet value) {
        BsonDocument section = new BsonDocument();
        writeMap(section, "areas", value.getAreaMap(), StageBsonMapper::writeCuboid);
        writeMap(section, "info", value.getAreaInfoMap(), StageBsonMapper::writeAreaInfo);
        bson.append(key, section);
    }

    private static AreaSet readAreaSet(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        AreaSet result = new AreaSet();
        result.setAreaMap(readMap(section, "areas", StageBsonMapper::readCuboid));
        result.setAreaInfoMap(readMap(section, "info", StageBsonMapper::readAreaInfo));
        return result;
    }

    private static void writeFlag(BsonDocument bson, String key, Flag value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        section.append("point", new BsonDouble(value.getFlagPoint()));
        section.append("producing", new BsonBoolean(value.isProducing()));
        bson.append(key, section);
    }

    private static Flag readFlag(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        double point = section.getDouble("point").getValue();
        boolean producing = section.getBoolean("producing").getValue();
        return new Flag(loc, point, producing);
    }

    private static void writeNexus(BsonDocument bson, String key, Nexus value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        writeEnum(section, "color", value.getColor());
        section.append("point", new BsonDouble(value.getPoint()));
        bson.append(key, section);
    }

    private static Nexus readNexus(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        TeamColor color = readEnum(section, "color", TeamColor.class);
        double point = section.getDouble("point").getValue();
        return new Nexus(loc, color, point);
    }

    private static void writeBannerSlot(BsonDocument bson, String key, BannerSlot value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        writeEnum(section, "color", value.getColor());
        bson.append(key, section);
    }

    private static BannerSlot readBannerSlot(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        TeamColor color = readEnum(section, "color", TeamColor.class);
        return new BannerSlot(loc, color);
    }

    private static void writeBannerSpawner(BsonDocument bson, String key, BannerSpawner value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        section.append("point", new BsonInt32(value.getPoint()));
        section.append("hp", new BsonInt32(value.getHp()));
        section.append("producing", new BsonBoolean(value.isProducing()));
        section.append("wall", new BsonBoolean(value.isWall()));
        writeEnum(section, "face", value.getFace());
        bson.append(key, section);
    }

    private static BannerSpawner readBannerSpawner(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        byte point = (byte) section.getInt32("point").getValue();
        byte hp = (byte) section.getInt32("hp").getValue();
        boolean producing = section.getBoolean("producing").getValue();
        boolean wall = section.getBoolean("wall").getValue();
        BlockFace face = readEnum(section, "face", BlockFace.class);
        return new BannerSpawner(loc, point, hp, producing, wall, face);
    }

    private static void writeGameChest(BsonDocument bson, String key, GameChest value) {
        writeLocation(bson, key, value.getLocation());
    }

    private static GameChest readGameChest(BsonDocument bson, String key) {
        Location loc = readLocation(bson, key);
        return new GameChest(loc);
    }
    
    private static void writeSuperJump(BsonDocument bson, String key, SuperJump value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        writeVector(section, "velocity", value.getVelocity());
        section.append("range", new BsonDouble(value.getRange()));
        bson.append(key, section);
    }

    private static SuperJump readSuperJump(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        double range = section.getDouble("range").getValue();
        Vector velocity = readVector(section, "velocity");
        return new SuperJump(loc, range, velocity);
    }

    public static BsonDocument writeStage(Stage value) {
        BsonDocument section = new BsonDocument();
        section.append("_id", new BsonString(value.getName()));
        section.append("time", new BsonInt64(value.getGameTime()));
        section.append("teamlimit", new BsonInt32(value.getTeamLimit()));
        section.append("protected", new BsonBoolean(value.isProtected()));
        section.append("available", new BsonBoolean(value.isAvailable()));
        section.append("kill", new BsonDouble(value.getKillScore()));
        section.append("death", new BsonDouble(value.getDeathScore()));
        section.append("entryfee", new BsonDouble(value.getEntryFee()));
        section.append("prize", new BsonDouble(value.getPrize()));
        section.append("cooldown", new BsonInt64(value.getCooldown()));
        writeEnumMap(section, "spawn", value.getSpawns(), StageBsonMapper::writeLocation);
        writeLocation(section, "specspawn", value.getSpecSpawn().orElse(null));

        writeList(section, "flags", value.getObjectives(Flag.class).values(), StageBsonMapper::writeFlag);
        writeList(section, "nexuses", value.getObjectives(Nexus.class).values(), StageBsonMapper::writeNexus);
        writeList(section, "banner-spawners", value.getObjectives(BannerSlot.class).values(), StageBsonMapper::writeBannerSlot);
        writeList(section, "banner-slots", value.getObjectives(BannerSpawner.class).values(), StageBsonMapper::writeBannerSpawner);
        writeList(section, "containers", value.getObjectives(GameChest.class).values(), StageBsonMapper::writeGameChest);
        writeList(section, "superjumps", value.getObjectives(SuperJump.class).values(), StageBsonMapper::writeSuperJump);

        writeAreaSet(section, "areas", value.getAreas());
        section.append("author", new BsonString(value.getAuthor()));
        section.append("description", new BsonString(value.getDescription()));
        section.append("guide", new BsonString(value.getGuide()));
        return section;
    }

    public static BasicStage readStage(BsonDocument bson) {
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
            stage.setSpawns(readEnumMap(bson, "spawn", TeamColor.class, StageBsonMapper::readLocation));
            stage.setSpecSpawn(readLocation(bson, "specspawn"));
            stage.addObjectives(readList(bson, "flags", StageBsonMapper::readFlag));
            stage.addObjectives(readList(bson, "nexuses", StageBsonMapper::readNexus));
            stage.addObjectives(readList(bson, "banner-spawners", StageBsonMapper::readBannerSpawner));
            stage.addObjectives(readList(bson, "banner-slots", StageBsonMapper::readBannerSlot));
            stage.addObjectives(readList(bson, "containers", StageBsonMapper::readGameChest));
            stage.addObjectives(readList(bson, "superjumps", StageBsonMapper::readSuperJump));
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
