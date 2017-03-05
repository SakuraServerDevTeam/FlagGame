/* 
 * Copyright (C) 2017 Toyblocks, SakuraServerDev
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
import jp.llv.flaggame.game.basic.objective.BannerSlot;
import jp.llv.flaggame.game.basic.objective.BannerSpawner;
import jp.llv.flaggame.game.basic.objective.Nexus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import syam.flaggame.enums.TeamColor;
import jp.llv.flaggame.game.basic.objective.Flag;
import org.bukkit.block.BlockFace;
import syam.flaggame.exception.StageReservedException;
import syam.flaggame.game.Stage;
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

    public static void writeNexus(ConfigurationSection section, String key, Nexus nexus) {
        if (nexus == null) {
            section.set(key, null);
        } else {
            ConfigurationSection ns = section.createSection(key);
            writeLocation(ns, "loc", nexus.getLocation());
            ns.set("color", nexus.getColor().toString());
            ns.set("point", nexus.getPoint());
        }
    }

    public static Nexus readNexus(ConfigurationSection section, String key) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        } else {
            try {
                ConfigurationSection ns = section.getConfigurationSection(key);
                Location loc = readLocation(ns, "loc");
                TeamColor tc = TeamColor.valueOf(ns.getString("color").toUpperCase());
                double p = ns.getDouble("point", 1.0D);
                return new Nexus(loc, tc, p);
            } catch (IllegalArgumentException | NullPointerException ex) {
                return null;
            }
        }
    }

    public static void writeBannerSpawner(ConfigurationSection section, String key, BannerSpawner spawner) {
        if (spawner == null) {
            section.set(key, null);
        } else {
            ConfigurationSection ns = section.createSection(key);
            writeLocation(ns, "loc", spawner.getLocation());
            ns.set("point", spawner.getPoint());
            ns.set("hp", spawner.getHp());
            ns.set("wall", spawner.isWall());
            ns.set("face", spawner.getFace().toString());
        }
    }

    public static BannerSpawner readBannerSpawner(ConfigurationSection section, String key) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        } else {
            ConfigurationSection ns = section.getConfigurationSection(key);
            Location loc = readLocation(section, "loc");
            byte point = (byte) section.getInt("point");
            byte hp = (byte) section.getInt("hp");
            boolean wall = section.getBoolean("wall");
            BlockFace face = BlockFace.valueOf(section.getString("face"));
            return new BannerSpawner(loc, point, hp, wall, face);
        }
    }

    public static void writeBannerSlot(ConfigurationSection section, String key, BannerSlot slot) {
        if (slot == null) {
            section.set(key, null);
        } else {
            ConfigurationSection ns = section.createSection(key);
            writeLocation(ns, "loc", slot.getLocation());
            ns.set("color", slot.getColor().toString());
        }
    }

    public static BannerSlot readBannerSlot(ConfigurationSection section, String key) {
        if (section.getConfigurationSection(key) == null) {
            return null;
        } else {
            ConfigurationSection ns = section.getConfigurationSection(key);
            Location loc = readLocation(ns, "loc");
            TeamColor tc = TeamColor.valueOf(ns.getString("color"));
            return new BannerSlot(loc, tc);
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
            ns.set("kill", stage.getDeathScore());
            ns.set("death", stage.getKillScore());
            writeCuboid(ns, "area", stage.getStageArea());
            writeEnumMap(ns, "spawn", stage.getSpawns(), ConfigUtils::writeLocation);
            writeLocation(ns, "specspawn", stage.getSpecSpawn().orElse(null));
            writeList(ns, "flags", stage.getFlags().values(), ConfigUtils::writeFlag);
            writeList(ns, "nexuses", stage.getNexuses().values(), ConfigUtils::writeNexus);
            writeList(ns, "bannerspawners", stage.getBannerSpawners().values(), ConfigUtils::writeBannerSpawner);
            writeList(ns, "bannerslots", stage.getBannerSlots().values(), ConfigUtils::writeBannerSlot);
            writeEnumMap(ns, "bases", stage.getBases(), ConfigUtils::writeCuboid);
            writeList(ns, "containers", stage.getChests(), ConfigUtils::writeLocation);
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
            double killScore = ns.getDouble("kill", 0);
            double deathScore = ns.getDouble("death", 0);
            Cuboid area = readCuboid(ns, "area");
            EnumMap<TeamColor, Location> spawn = readEnumMap(ns, "spawn", TeamColor.class, ConfigUtils::readLocation);
            Location specspawn = readLocation(ns, "specspawn");
            List<Flag> flags = readList(ns, "flags", ConfigUtils::readFlag);
            List<Nexus> nexuses = readList(ns, "nexuses", ConfigUtils::readNexus);
            List<BannerSpawner> spawners = readList(ns, "bannerspawners", ConfigUtils::readBannerSpawner);
            List<BannerSlot> slots = readList(ns, "bannerslots", ConfigUtils::readBannerSlot);
            EnumMap<TeamColor, Cuboid> bases = readEnumMap(ns, "bases", TeamColor.class, ConfigUtils::readCuboid);
            List<Location> containers = readList(ns, "containers", ConfigUtils::readLocation);

            Stage stage = new Stage(name);
            try {
                if (time > 0) {
                    stage.setGameTime(time);
                }
                stage.setTeamLimit(teamlimit);
                stage.setProtected(protect);
                stage.setAvailable(available);
                stage.setKillScore(killScore);
                stage.setDeathScore(deathScore);
                if (area != null) {
                    stage.setStageArea(area);
                }
                if (spawn != null) {
                    stage.setSpawns(spawn);
                }
                if (specspawn != null) {
                    stage.setSpecSpawn(specspawn);
                }
                if (flags != null) {
                    stage.setFlags(flags);
                }
                if (nexuses != null) {
                    stage.setNexuses(nexuses);
                }
                if (bases != null) {
                    stage.setBases(bases);
                }
                if (containers != null) {
                    stage.setChests(containers);
                }
                if (spawners != null) {
                    stage.setBannerSpawners(spawners);
                }
                if (slots != null) {
                    stage.setBannerSlots(slots);
                }
                return stage;
            } catch (StageReservedException ex) {
                throw new RuntimeException("Is illegal operation executed? Report this!");
            }
        }
    }

}
