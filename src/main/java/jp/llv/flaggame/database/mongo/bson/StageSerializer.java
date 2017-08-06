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

import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.stage.area.StageAreaInfo;
import jp.llv.flaggame.api.stage.area.StageAreaSet;
import jp.llv.flaggame.api.stage.objective.BannerSlot;
import jp.llv.flaggame.api.stage.objective.BannerSpawner;
import jp.llv.flaggame.api.stage.objective.Flag;
import jp.llv.flaggame.api.stage.objective.GameChest;
import jp.llv.flaggame.api.stage.objective.Nexus;
import jp.llv.flaggame.api.stage.objective.SuperJump;
import jp.llv.flaggame.api.stage.permission.StagePermissionStateSet;
import org.bson.BsonBinary;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author toyblocks
 */
public final class StageSerializer extends BaseSerializer {

    public static final int VERSION = 1;
    private static StageSerializer instance;

    StageSerializer() {
    }

    void writeVector(BsonDocument bson, String key, Vector value) {
        if (value == null) {
            return;
        }
        BsonDocument section = new BsonDocument();
        section.append("x", new BsonDouble(value.getX()));
        section.append("y", new BsonDouble(value.getY()));
        section.append("z", new BsonDouble(value.getZ()));
        bson.append(key, section);
    }

    void writeLocation(BsonDocument bson, String key, Location value) {
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

    void writeCuboid(BsonDocument bson, String key, Cuboid value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "pos1", value.getPos1());
        writeLocation(section, "pos2", value.getPos2());
        bson.append(key, section);
    }

    void writeRollback(BsonDocument bson, String key, StageAreaInfo.StageRollbackData value) {
        BsonDocument section = new BsonDocument();
        section.append("timing", new BsonInt64(value.getTiming()));
        writeEnum(section, "target", value.getTarget().getType());
        section.append("data", new BsonBinary(value.getData()));
        bson.append(key, section);
    }

    void writeGamePermissionStateSet(BsonDocument bson, String key, StagePermissionStateSet value) {
        writeEnumMap(bson, key, value.getState(), (b, k, s) -> writeEnum(b, k, s));
    }

    void writeMessageData(BsonDocument bson, String key, StageAreaInfo.StageMessageData value) {
        BsonDocument section = new BsonDocument();
        section.append("timing", new BsonInt64(value.getTiming()));
        writeEnum(section, "type", value.getType());
        section.append("message", new BsonString(value.getMessage()));
        bson.append(key, section);
    }

    void writeAreaInfo(BsonDocument bson, String key, StageAreaInfo value) {
        BsonDocument section = new BsonDocument();
        writeMap(section, "rollbacks", value.getRollbacks(), this::writeRollback);
        writeEnumMap(section, "permissions", value.getPermissions(), this::writeGamePermissionStateSet);
        writeList(section, "messages", value.getMessages(), this::writeMessageData);
        bson.append(key, section);
    }

    void writeAreaSet(BsonDocument bson, String key, StageAreaSet value) {
        BsonDocument section = new BsonDocument();
        writeMap(section, "areas", value.getAreaMap(), this::writeCuboid);
        writeMap(section, "info", value.getAreaInfoMap(), this::writeAreaInfo);
        bson.append(key, section);
    }

    void writeFlag(BsonDocument bson, String key, Flag value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        section.append("point", new BsonDouble(value.getFlagPoint()));
        section.append("producing", new BsonBoolean(value.isProducing()));
        bson.append(key, section);
    }

    void writeNexus(BsonDocument bson, String key, Nexus value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        writeEnum(section, "color", value.getColor());
        section.append("point", new BsonDouble(value.getPoint()));
        bson.append(key, section);
    }

    void writeBannerSlot(BsonDocument bson, String key, BannerSlot value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        writeEnum(section, "color", value.getColor());
        bson.append(key, section);
    }

    void writeBannerSpawner(BsonDocument bson, String key, BannerSpawner value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        section.append("point", new BsonInt32(value.getPoint()));
        section.append("hp", new BsonInt32(value.getHp()));
        section.append("producing", new BsonBoolean(value.isProducing()));
        section.append("wall", new BsonBoolean(value.isWall()));
        writeEnum(section, "face", value.getFace());
        bson.append(key, section);
    }

    void writeGameChest(BsonDocument bson, String key, GameChest value) {
        writeLocation(bson, key, value.getLocation());
    }

    void writeSuperJump(BsonDocument bson, String key, SuperJump value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        writeVector(section, "velocity", value.getVelocity());
        section.append("range", new BsonDouble(value.getRange()));
        bson.append(key, section);
    }

    public BsonDocument writeStage(Stage value) {
        BsonDocument section = new BsonDocument();
        section.append("_id", new BsonString(value.getName()));
        section.append(StageDeserializer.Version.FIELD_NAME, new BsonInt32(VERSION));
        section.append("time", new BsonInt64(value.getGameTime()));
        section.append("teamlimit", new BsonInt32(value.getTeamLimit()));
        section.append("protected", new BsonBoolean(value.isProtected()));
        section.append("available", new BsonBoolean(value.isAvailable()));
        section.append("kill", new BsonDouble(value.getKillScore()));
        section.append("death", new BsonDouble(value.getDeathScore()));
        section.append("entryfee", new BsonDouble(value.getEntryFee()));
        section.append("prize", new BsonDouble(value.getPrize()));
        section.append("cooldown", new BsonInt64(value.getCooldown()));
        writeEnumMap(section, "spawn", value.getSpawns(), this::writeLocation);
        writeLocation(section, "specspawn", value.getSpecSpawn().orElse(null));

        writeList(section, "flags", value.getObjectives(Flag.class).values(), this::writeFlag);
        writeList(section, "nexuses", value.getObjectives(Nexus.class).values(), this::writeNexus);
        writeList(section, "banner-spawners", value.getObjectives(BannerSlot.class).values(), this::writeBannerSlot);
        writeList(section, "banner-slots", value.getObjectives(BannerSpawner.class).values(), this::writeBannerSpawner);
        writeList(section, "containers", value.getObjectives(GameChest.class).values(), this::writeGameChest);
        writeList(section, "superjumps", value.getObjectives(SuperJump.class).values(), this::writeSuperJump);

        writeAreaSet(section, "areas", value.getAreas());
        section.append("author", new BsonString(value.getAuthor()));
        section.append("description", new BsonString(value.getDescription()));
        section.append("guide", new BsonString(value.getGuide()));
        return section;
    }

    public static StageSerializer getInstance() {
        if (instance == null) {
            instance = new StageSerializer();
        }
        return instance;
    }

}
