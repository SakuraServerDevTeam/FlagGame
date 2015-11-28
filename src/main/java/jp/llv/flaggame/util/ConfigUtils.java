/* 
 * Copyright (C) 2015 Toyblocks, SakuraServerDev
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
package jp.llv.flaggame.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.game.Flag;
import syam.flaggame.game.Stage;
import syam.flaggame.game.StageProfile;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author Toyblocks
 */
public final class ConfigUtils {

    private ConfigUtils() {
        throw new RuntimeException();
    }
    
    public static void writeUUID(ConfigurationSection section, String key, UUID uuid) {
        if (uuid == null) {
            section.set(key, null);
        } else {
            section.set(key, uuid.toString());
        }
    }

    public static UUID readUUID(ConfigurationSection section, String key) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        }
        return UUID.fromString(section.getString(key));
    }

    public static void writeLocation(ConfigurationSection section, String key, Location loc) {
        if (loc == null) {
            section.set(key, null);
        } else {
            ConfigurationSection ns = section.createSection(key);
            ns.set("world", loc.getWorld().getName());
            ns.set("yaw", loc.getYaw());
            ns.set("pitch", loc.getPitch());
            ns.set("x", loc.getX());
            ns.set("y", loc.getY());
            ns.set("z", loc.getZ());
        }
    }

    public static Location readLocation(ConfigurationSection section, String key) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        }
        ConfigurationSection ns = section.getConfigurationSection(key);
        String world = ns.getString("world");
        float yaw = (float) ns.getDouble("yaw", 0D);
        float pitch = (float) ns.getDouble("pitch", 0D);
        double x = ns.getDouble("x", 0D);
        double y = ns.getDouble("y", 0D);
        double z = ns.getDouble("z", 0D);
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public static void writeCuboid(ConfigurationSection section, String key, Cuboid cuboid) {
        if (cuboid == null) {
            section.set(key, null);
        } else {
            ConfigurationSection ns = section.createSection(key);
            writeLocation(ns, "p1", cuboid.getPos1());
            writeLocation(ns, "p2", cuboid.getPos2());
        }
    }

    public static Cuboid readCuboid(ConfigurationSection section, String key) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        }
        ConfigurationSection ns = section.getConfigurationSection(key);
        Location p1 = readLocation(ns, "p1");
        Location p2 = readLocation(ns, "p2");
        return new Cuboid(p1, p2);
    }

    public static <T> void writeList(ConfigurationSection section, String key, Collection<? extends T> data,
            TriConsumer<ConfigurationSection, String, T> writer) {
        if (data == null) {
            section.set(key, null);
        } else {
            ConfigurationSection ns = section.createSection(key);
            int i = 0;
            for (T t : data) {
                writer.accept(ns, Integer.toString(i), t);
                i++;
            }
        }
    }

    public static <T> List<T> readList(ConfigurationSection section, String key,
            BiFunction<ConfigurationSection, String, ? extends T> reader) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        } else {
            ConfigurationSection ns = section.getConfigurationSection(key);
            List<T> result = new ArrayList<>();
            for (String nk : ns.getKeys(false)) {
                result.add(reader.apply(ns, nk));
            }
            return Collections.unmodifiableList(result);
        }
    }

    public static <V> void writeMap(ConfigurationSection section, String key, Map<String, ? extends V> data,
            TriConsumer<ConfigurationSection, String, V> writer) {
        if (data == null) {
            section.set(key, null);
        } else {
            ConfigurationSection ns = section.createSection(key);
            for (Map.Entry<String, ? extends V> entry : data.entrySet()) {
                writer.accept(ns, entry.getKey(), entry.getValue());
            }
        }
    }

    public static <K extends Enum, V> void writeEnumMap(ConfigurationSection section, String key, Map<K, ? extends V> data,
            TriConsumer<ConfigurationSection, String, V> writer) {
        if (data == null) {
            section.set(key, null);
        } else {
            ConfigurationSection ns = section.createSection(key);
            for (Map.Entry<K, ? extends V> entry : data.entrySet()) {
                writer.accept(ns, entry.getKey().toString(), entry.getValue());
            }
        }
    }

    public static <V> Map<String, V> readMap(ConfigurationSection section, String key,
            BiFunction<ConfigurationSection, String, ? extends V> reader) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        } else {
            ConfigurationSection ns = section.getConfigurationSection(key);
            Map<String, V> result = new HashMap<>();
            for (String nk : ns.getKeys(false)) {
                result.put(nk, reader.apply(ns, nk));
            }
            return Collections.unmodifiableMap(result);
        }
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> readEnumMap(ConfigurationSection section, String key,
            Class<K> clazz, BiFunction<ConfigurationSection, String, ? extends V> reader) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        } else {
            ConfigurationSection ns = section.getConfigurationSection(key);
            EnumMap<K, V> result = new EnumMap<>(clazz);
            for (String nk : ns.getKeys(false)) {
                result.put(Enum.valueOf(clazz, nk), reader.apply(ns, nk));
            }
            return result;
        }
    }

    public static void writeFlag(ConfigurationSection section, String key, Flag flag) {
        if (flag == null) {
            section.set(key, null);
        } else {
            ConfigurationSection ns = section.createSection(key);
            writeLocation(ns, "loc", flag.getLocation());
            ns.set("point", flag.getFlagPoint());
        }
    }

    public static Flag readFlag(ConfigurationSection section, String key) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        } else {
            ConfigurationSection ns = section.getConfigurationSection(key);
            Location loc = readLocation(ns, "loc");
            byte p = (byte) ns.getInt("point");
            return new Flag(loc, p);
        }
    }
    
    public static void writeStageProfile(ConfigurationSection section, String key, StageProfile profile) {
        if (profile == null) {
            section.set(key, null);
        } else {
            ConfigurationSection ns = section.createSection(key);
            ns.set("lastplayed", profile.getLastPlayedAt());
            ns.set("kill", profile.getKill());
            ns.set("death", profile.getDeath());
            ns.set("placedflag", profile.getPlacedFlag());
            ns.set("brokenflag", profile.getBrokenFlag());
        }
    }
    
    public static StageProfile readStageProfile(ConfigurationSection section, String key) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        } else {
            ConfigurationSection ns = section.getConfigurationSection(key);
            Long lastPlayed = ns.getLong("lastplayed", -1);
            lastPlayed = lastPlayed == -1 ? null : lastPlayed;
            int kill = ns.getInt("kill");
            int death = ns.getInt("death");
            int placedFlag = ns.getInt("placedflag");
            int brokenFlag = ns.getInt("brokenflag");
            
            StageProfile profile = new StageProfile();
            profile.setLastPlayedAt(lastPlayed);
            profile.setKill(kill);
            profile.setDeath(death);
            profile.setPlacedFlag(placedFlag);
            profile.setBrokenFlag(brokenFlag);
            return profile;
        }
    }
    
    public static void writeStage(ConfigurationSection section, String key, Stage stage) {
        if (stage == null) {
            section.set(key, null);
        } else {
            ConfigurationSection ns = section.createSection(key);
            ns.set("name", stage.getName());
            ns.set("time", stage.getGameTime());
            ns.set("teamlimit", stage.getTeamLimit());
            ns.set("protected", stage.isStageProtected());
            ns.set("available", stage.isAvailable());
            writeCuboid(ns, "area", stage.getStageArea());
            writeEnumMap(ns, "spawn", stage.getSpawns(), ConfigUtils::writeLocation);
            writeLocation(ns, "specspawn", stage.getSpecSpawn().orElse(null));
            writeList(ns, "flags", stage.getFlags().values(), ConfigUtils::writeFlag);
            writeEnumMap(ns, "bases", stage.getBases(), ConfigUtils::writeCuboid);
            writeList(ns, "containers", stage.getChests(), ConfigUtils::writeLocation);
            writeStageProfile(ns, "profile", stage.getProfile());
        }
    }
    
    public static Stage readStage(ConfigurationSection section, String key) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        } else {
            ConfigurationSection ns = section.getConfigurationSection(key);
            String name = ns.getString("name");
            long time = ns.getLong("time", -1);
            int teamlimit = ns.getInt("teamlimit", Integer.MAX_VALUE);
            boolean protect = ns.getBoolean("protected", true);
            boolean available = ns.getBoolean("available", false);
            Cuboid area = readCuboid(ns, "area");
            EnumMap<TeamColor, Location> spawn = readEnumMap(ns, "spawn", TeamColor.class, ConfigUtils::readLocation);
            Location specspawn = readLocation(ns, "specspawn");
            List<Flag> flags = readList(ns, "flags", ConfigUtils::readFlag);
            EnumMap<TeamColor, Cuboid> bases = readEnumMap(ns, "bases", TeamColor.class, ConfigUtils::readCuboid);
            List<Location> containers = readList(ns, "containers", ConfigUtils::readLocation);
            
            StageProfile profile = readStageProfile(ns, "profile");
            Stage stage = new Stage(name, profile);
            if (time > 0) stage.setGameTime(time);
            stage.setTeamLimit(teamlimit);
            stage.setStageProtected(protect);
            stage.setAvailable(available);
            if (area != null) stage.setStageArea(area);
            if (spawn != null) stage.setSpawns(spawn);
            if (specspawn != null) stage.setSpecSpawn(specspawn);
            if (flags != null) stage.setFlags(flags);
            if (bases != null) stage.setBases(bases);
            if (containers != null) stage.setChests(containers);
            return stage;
        }
    }

}