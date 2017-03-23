/* 
 * Copyright (C) 2017 Syamn, SakuraServerDev
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
package syam.flaggame.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import jp.llv.flaggame.game.basic.objective.BannerSlot;
import jp.llv.flaggame.game.basic.objective.BannerSpawner;
import jp.llv.flaggame.game.basic.objective.Flag;
import jp.llv.flaggame.game.basic.objective.Nexus;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.rollback.RollbackTarget;
import jp.llv.flaggame.util.TriConsumer;
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
import syam.flaggame.FlagGame;
import syam.flaggame.exception.StageReservedException;
import syam.flaggame.util.Cuboid;

public class StageFileManager {

    private final FlagGame plugin;

    public StageFileManager(final FlagGame plugin) {
        this.plugin = plugin;
    }

    public void saveStages() {
        throw new UnsupportedOperationException("WIP");
    }

    public void loadStages() {
        throw new UnsupportedOperationException("WIP");
    }

    private <E extends Enum<E>> void writeEnum(BsonDocument bson, String key, E value) {
        if (value == null) {
            return;
        }
        bson.append(key, new BsonString(value.toString()));
    }

    private <E extends Enum<E>> E readEnum(BsonDocument bson, String key, Class<E> clazz) {
        try {
            return Enum.valueOf(clazz, bson.getString(key).getValue());
        } catch (BsonInvalidOperationException ex) {
            return null;
        }
    }

    private <K extends Enum<K>, V> void writeEnumMap(BsonDocument bson, String key, Map<K, ? extends V> value, TriConsumer<BsonDocument, String, V> writer) {
        BsonDocument section = new BsonDocument();
        for (Map.Entry<K, ? extends V> entry : value.entrySet()) {
            writer.accept(section, entry.getKey().toString(), entry.getValue());
        }
        bson.append(key, section);
    }

    private <K extends Enum<K>, V> EnumMap<K, V> readEnumMap(BsonDocument bson, String key, Class<K> clazz, BiFunction<BsonDocument, String, ? extends V> reader) {
        BsonDocument section = bson.getDocument(key);
        EnumMap<K, V> result = new EnumMap<>(clazz);
        for (String k : section.keySet()) {
            result.put(Enum.valueOf(clazz, k), reader.apply(section, k));
        }
        return result;
    }

    public <V> void writeMap(BsonDocument bson, String key, Map<String, ? extends V> value, TriConsumer<BsonDocument, String, V> writer) {
        BsonDocument section = new BsonDocument();
        for (Map.Entry<String, ? extends V> entry : value.entrySet()) {
            writer.accept(section, entry.getKey(), entry.getValue());
        }
        bson.append(key, section);
    }

    public <V> Map<String, V> readMap(BsonDocument bson, String key, BiFunction<BsonDocument, String, ? extends V> reader) {
        BsonDocument section = bson.getDocument(key);
        Map<String, V> result = new HashMap<>();
        for (String k : section.keySet()) {
            result.put(k, reader.apply(section, k));
        }
        return result;
    }

    public static <T> void writeList(BsonDocument bson, String key, Collection<? extends T> value, TriConsumer<BsonDocument, String, T> writer) {
        BsonDocument section = new BsonDocument();
        int i = 0;
        for (T t : value) {
            writer.accept(section, Integer.toString(i++), t);
        }
        bson.append(key, section);
    }

    public static <T> List<T> readList(BsonDocument bson, String key, BiFunction<BsonDocument, String, ? extends T> reader) {
        BsonDocument section = bson.getDocument(key);
        List<T> result = new ArrayList<>();
        for (String k : section.keySet()) {
            result.add(reader.apply(section, k));
        }
        return result;
    }

    private void writeLocation(BsonDocument bson, String key, Location value) {
        if (key == null) {
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

    private Location readLocation(BsonDocument bson, String key) {
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

    private void writeCuboid(BsonDocument bson, String key, Cuboid value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "pos1", value.getPos1());
        writeLocation(section, "pos2", value.getPos2());
        bson.append(key, section);
    }

    private Cuboid readCuboid(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        return new Cuboid(readLocation(section, "pos1"), readLocation(section, "pos2"));
    }

    private void writeRollback(BsonDocument bson, String key, AreaInfo.RollbackData value) {
        BsonDocument section = new BsonDocument();
        section.append("timing", new BsonInt64(value.getTiming()));
        writeEnum(section, "target", value.getTarget());
        section.append("data", new BsonBinary(value.getData()));
        bson.append(key, section);
    }

    private AreaInfo.RollbackData readRollback(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        AreaInfo.RollbackData result = new AreaInfo.RollbackData();
        result.setTiming(section.getInt64("timing").getValue());
        result.setTarget(readEnum(section, "target", RollbackTarget.class));
        result.setData(section.getBinary("data").getData());
        return result;
    }

    private void writeAreaInfo(BsonDocument bson, String key, AreaInfo value) {
        BsonDocument section = new BsonDocument();
        writeMap(section, "rollbacks", value.getRollbacks(), this::writeRollback);
        writeEnumMap(section, "godmode", value.getGodmodeMap(), this::writeEnum);
        writeEnumMap(section, "regeneration", value.getRegenerationMap(), this::writeEnum);
        writeEnumMap(section, "protection", value.getProtectionMap(), this::writeEnum);
        bson.append(key, section);
    }

    private AreaInfo readAreaInfo(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        AreaInfo result = new AreaInfo();
        result.setRollbacks(readMap(section, "rolbacks", this::readRollback));
        result.setGodmodeMap(readEnumMap(section, "godmode", TeamColor.class, (b, k) -> readEnum(b, k, AreaInfo.State.class)));
        result.setRegenerationMap(readEnumMap(section, "regeneration", TeamColor.class, (b, k) -> readEnum(b, k, AreaInfo.State.class)));
        result.setProtectionMap(readEnumMap(section, "protection", Protection.class, (b, k) -> readEnum(b, k, AreaInfo.State.class)));
        return result;
    }

    private void writeAreaSet(BsonDocument bson, String key, AreaSet value) {
        BsonDocument section = new BsonDocument();
        writeMap(section, "areas", value.getAreaMap(), this::writeCuboid);
        writeMap(section, "info", value.getAreaInfoMap(), this::writeAreaInfo);
        section.append(key, section);
    }

    private AreaSet readAreaSet(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        AreaSet result = new AreaSet();
        result.setAreaMap(readMap(section, "areas", this::readCuboid));
        result.setAreaInfoMap(readMap(section, "info", this::readAreaInfo));
        return result;
    }

    private void writeFlag(BsonDocument bson, String key, Flag value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        section.append("point", new BsonDouble(value.getFlagPoint()));
        section.append("producing", new BsonBoolean(value.isProducing()));
        bson.append(key, section);
    }

    private Flag readFlag(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        double point = section.getDouble("point").getValue();
        boolean producing = section.getBoolean("producing").getValue();
        return new Flag(loc, point, producing);
    }

    private void writeNexus(BsonDocument bson, String key, Nexus value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        writeEnum(section, "color", value.getColor());
        section.append("point", new BsonDouble(value.getPoint()));
        bson.append(key, section);
    }

    private Nexus readNexus(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        TeamColor color = readEnum(section, "color", TeamColor.class);
        double point = section.getDouble("point").getValue();
        return new Nexus(loc, color, point);
    }

    private void writeBannerSlot(BsonDocument bson, String key, BannerSlot value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        writeEnum(section, "color", value.getColor());
        bson.append(key, section);
    }

    private BannerSlot readBannerSlot(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        TeamColor color = readEnum(section, "color", TeamColor.class);
        return new BannerSlot(loc, color);
    }

    private void writeBannerSpawner(BsonDocument bson, String key, BannerSpawner value) {
        BsonDocument section = new BsonDocument();
        writeLocation(section, "loc", value.getLocation());
        section.append("point", new BsonInt32(value.getPoint()));
        section.append("hp", new BsonInt32(value.getHp()));
        section.append("producing", new BsonBoolean(value.isProducing()));
        section.append("wall", new BsonBoolean(value.isWall()));
        writeEnum(section, "face", value.getFace());
        bson.append(key, section);
    }

    private BannerSpawner readBannerSpawner(BsonDocument bson, String key) {
        BsonDocument section = bson.getDocument(key);
        Location loc = readLocation(section, "loc");
        byte point = (byte) section.getInt32("point").getValue();
        byte hp = (byte) section.getInt32("hp").getValue();
        boolean producing = section.getBoolean("producing").getValue();
        boolean wall = section.getBoolean("wall").getValue();
        BlockFace face = readEnum(section, "face", BlockFace.class);
        return new BannerSpawner(loc, point, hp, producing, wall, face);
    }

    private BsonDocument writeStage(Stage value) {
        BsonDocument section = new BsonDocument();
        section.append("name", new BsonString(value.getName()));
        section.append("time", new BsonInt64(value.getGameTime()));
        section.append("teamlimit", new BsonInt32(value.getTeamLimit()));
        section.append("protected", new BsonBoolean(value.isProtected()));
        section.append("available", new BsonBoolean(value.isAvailable()));
        section.append("kill", new BsonDouble(value.getKillScore()));
        section.append("death", new BsonDouble(value.getDeathScore()));
        writeEnumMap(section, "spawn", value.getSpawns(), this::writeLocation);
        writeLocation(section, "specspawn", value.getSpecSpawn().orElse(null));
        writeList(section, "flags", value.getFlags().values(), this::writeFlag);
        writeList(section, "nexuses", value.getNexuses().values(), this::writeNexus);
        writeList(section, "banner-spawners", value.getBannerSlots().values(), this::writeBannerSlot);
        writeList(section, "banner-slots", value.getBannerSpawners().values(), this::writeBannerSpawner);
        writeList(section, "containers", value.getChests(), this::writeLocation);
        writeAreaSet(section, "areas", value.getAreas());
        return section;
    }

    private Stage readStage(BsonDocument bson) {
        try {
            Stage stage = new Stage(bson.getString("name").getValue());
            stage.setGameTime(bson.getInt64("time").getValue());
            stage.setTeamLimit(bson.getInt32("teamlimit").getValue());
            stage.setProtected(bson.getBoolean("protected").getValue());
            stage.setAvailable(bson.getBoolean("available").getValue());
            stage.setKillScore(bson.getDouble("kill").getValue());
            stage.setDeathScore(bson.getDouble("death").getValue());
            stage.setSpawns(readEnumMap(bson, "spawn", TeamColor.class, this::readLocation));
            stage.setSpecSpawn(readLocation(bson, "specspawn"));
            stage.setFlags(readList(bson, "flags", this::readFlag));
            stage.setNexuses(readList(bson, "nexuses", this::readNexus));
            stage.setBannerSpawners(readList(bson, "banner-spawners", this::readBannerSpawner));
            stage.setBannerSlots(readList(bson, "banner-slots", this::readBannerSlot));
            stage.setChests(readList(bson, "containers", this::readLocation));
            stage.setAreas(readAreaSet(bson, "areas"));
            return stage;
        } catch (StageReservedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
