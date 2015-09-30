package syam.flaggame.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import syam.flaggame.FlagGame;
import syam.flaggame.enums.FlagType;
import syam.flaggame.enums.GameTeam;
import syam.flaggame.game.Flag;
import syam.flaggame.game.Stage;
import syam.flaggame.util.Cuboid;

public class StageFileManager {
    // Logger
    public static final Logger log = FlagGame.logger;
    private static final String logPrefix = FlagGame.logPrefix;
    private static final String msgPrefix = FlagGame.msgPrefix;

    private final FlagGame plugin;

    public StageFileManager(final FlagGame plugin) {
        this.plugin = plugin;
    }

    /* ステージデータ保存/読み出し */
    public void saveStages() {
        String fileDir = plugin.getDataFolder() + System.getProperty("file.separator") +

        "stageData" + System.getProperty("file.separator");
        FileConfiguration confFile;
        for (Stage stage : StageManager.getStages().values()) {
            confFile = new YamlConfiguration();

            File file = new File(fileDir + stage.getName() + ".yml");

            // マップデータをリストに変換
            String stageArea = null;
            if (stage.getStage() != null) stageArea = convertStageCuboidToString(stage.getStage());
            List<String> flagList = convertFlagMapToList(stage.getFlags());
            List<String> spawnList = convertSpawnMapToList(stage.getSpawns());
            List<String> baseList = convertBaseMapToList(stage.getBases());
            List<String> chestList = convertChestMapToList(stage.getChests());

            // 保存するデータをここに
            confFile.set("GameName", stage.getName());
            confFile.set("GameTime", stage.getGameTime());
            confFile.set("TeamLimit", stage.getTeamLimit());
            confFile.set("Award", stage.getAward());
            confFile.set("EntryFee", stage.getEntryFee());
            confFile.set("StageProtected", stage.isStageProtected());

            confFile.set("Stage", stageArea);
            confFile.set("Spawns", spawnList);
            confFile.set("SpecSpawn", convertPlayerLocation(stage.getSpecSpawn()));
            confFile.set("Flags", flagList);
            confFile.set("Bases", baseList);
            confFile.set("Chests", chestList);

            confFile.set("Available", stage.isAvailable());

            try {
                confFile.save(file);
            } catch (IOException ex) {
                log.warning(logPrefix + "Couldn't write Stage data!");
                ex.printStackTrace();
            }
        }
    }

    public void loadStages() {
        FileConfiguration confFile = new YamlConfiguration();
        String fileDir = plugin.getDataFolder() + System.getProperty("file.separator") + "stageData";

        File dir = new File(fileDir);
        File[] files = dir.listFiles();

        // ステージデータクリア
        StageManager.removeStages();

        // ファイルなし
        if (files == null || files.length == 0) return;

        // 取得データ
        String name;
        for (File file : files) {
            try {
                confFile.load(file);

                // 読むデータキー
                name = confFile.getString("GameName", null);

                // ステージ追加
                Stage stage = new Stage(plugin, name);

                // ファイル名設定
                stage.setFileName(file.getName());

                // 各設定やマップを追加
                stage.setGameTime(confFile.getInt("GameTime", 60 * 10));
                stage.setTeamLimit(confFile.getInt("TeamLimit", 8));
                stage.setAward(confFile.getInt("Award", 1000));
                stage.setEntryFee(confFile.getInt("EntryFee", 100));
                stage.setStageProtected(confFile.getBoolean("StageProtected", true));

                Cuboid stageArea = convertStageStringToCuboid(confFile.getString("Stage")); // ステージエリア
                if (stageArea != null) stage.setStage(stageArea);
                stage.setSpawns(convertSpawnListToMap(confFile.getStringList("Spawns"))); // スポーン地点
                stage.setSpecSpawn(convertPlayerLocation(confFile.getString("SpecSpawn", null))); // 観戦者スポーン地点
                stage.setFlags(convertFlagListToMap(confFile.getStringList("Flags"), stage)); // フラッグ
                stage.setBases(convertBaseListToMap(confFile.getStringList("Bases"))); // 拠点エリア
                stage.setChests(convertChestListToMap(confFile.getStringList("Chests"))); // チェスト

                // 有効かどうか
                stage.setAvailable(confFile.getBoolean("Available", true));

                log.log(Level.INFO,logPrefix + "Loaded Game: {0} ({1})", new Object[]{file.getName(), name});

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /* ステージ領域を変換 */
    private String convertStageCuboidToString(Cuboid stage) {
        String ret;

        // x,y,z@x,y,z
        Location pos1 = stage.getPos1();
        Location pos2 = stage.getPos2();

        ret = pos1.getBlockX() + "," + pos1.getBlockY() + "," + pos1.getBlockZ() + "@";
        ret = ret + pos2.getBlockX() + "," + pos2.getBlockY() + "," + pos2.getBlockZ();

        return ret;
    }

    private Cuboid convertStageStringToCuboid(String stageArea) {
        if (stageArea == null) return null;

        String[] data;
        String[] pos1;
        String[] pos2;

        // デリミタ分割
        data = stageArea.split("@");
        if (data.length != 2) {
            log.warning(logPrefix + "Skipping StageLine: incorrect format (@)");
            return null;
        }

        // data[0] : 座標形式チェック
        pos1 = data[0].split(",");
        if (pos1.length != 3) {
            log.warning(logPrefix + "Skipping StageLine: incorrect 1st coord format (,)");
            return null;
        }

        // data[1] : 座標形式チェック
        pos2 = data[1].split(",");
        if (pos1.length != 3) {
            log.warning(logPrefix + "Skipping StageLine: incorrect 2nd coord format (,)");
            return null;
        }

        World world = Bukkit.getWorld(plugin.getConfigs().getGameWorld());
        return new Cuboid(new Location(world, Double.parseDouble(pos1[0]), Double.parseDouble(pos1[1]), Double.parseDouble(pos1[2])), new Location(world, Double.parseDouble(pos2[0]), Double.parseDouble(pos2[1]), Double.parseDouble(pos2[2])));
    }

    /* フラッグデータを変換 */
    /**
     * フラッグデータをハッシュマップからリストに変換
     * 
     * @param flags
     *            フラッグマップ
     * @return フラッグ情報文字列のリスト
     */
    private List<String> convertFlagMapToList(Map<Location, Flag> flags) {
        List<String> ret = new ArrayList<>();
        ret.clear();

        for (Flag flag : flags.values()) {
            // 331,41,213@IRON@44:3 みたいな感じに
            // → GOLD@44:3@331,41,213に修正
            String s = flag.getFlagType().name() + "@";
            s = s + flag.getOriginBlockID() + ":" + flag.getOriginBlockData() + "@";

            Location loc = flag.getLocation();
            s = s + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();

            // フラッグ追加
            ret.add(s);
        }

        return ret;
    }

    /**
     * フラッグデータをリストからハッシュマップに変換
     * 
     * @param flags
     * @param stage
     * @return
     */
    private Map<Location, Flag> convertFlagListToMap(List<String> flags, Stage stage) {
        Map<Location, Flag> ret = new HashMap<>();
        ret.clear();

        String[] data;
        String[] block;
        String[] coord;

        World world = Bukkit.getWorld(plugin.getConfigs().getGameWorld());

        int line = 0;
        for (String s : flags) {
            line++;
            // デリミタで分ける
            data = s.split("@");
            if (data.length != 3) {
                log.log(Level.WARNING,logPrefix + "Skipping FlagLine {0}: incorrect format (@)", line);
                continue;
            }

            // data[0] : フラッグ種類チェック
            FlagType type = null;
            for (FlagType ft : FlagType.values()) {
                if (ft.name().equalsIgnoreCase(data[0])) {
                    type = ft;
                }
            }
            if (type == null) {
                log.log(Level.WARNING,logPrefix + "Skipping FlagLine {0}: undefined FlagType", line);
                continue;
            }

            // data[1] : ブロックID・データ値チェック
            block = data[1].split(":");
            if (block.length != 2) {
                log.log(Level.WARNING,logPrefix + "Skipping FlagLine {0}: incorrect block format (:)", line);
                continue;
            }

            // data[2] : 座標形式チェック
            coord = data[2].split(",");
            if (coord.length != 3) {
                log.log(Level.WARNING,logPrefix + "Skipping FlagLine {0}: incorrect coord format (,)", line);
                continue;
            }

            Location loc = new Location(world, new Double(coord[0]), new Double(coord[1]), new Double(coord[2])).getBlock().getLocation();
            ret.put(loc, new Flag(plugin, stage, loc, type, Integer.parseInt(block[0]), Byte.parseByte(block[1])));
        }

        return ret;
    }

    /* スポーン地点データを変換 */
    private List<String> convertSpawnMapToList(Map<GameTeam, Location> spawns) {
        List<String> ret = new ArrayList<>();
        ret.clear();

        for (Map.Entry<GameTeam, Location> entry : spawns.entrySet()) {
            // RED@x,y,z,pitch,yaw
            String s = entry.getKey().name() + "@";
            Location loc = entry.getValue();
            s = s + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();

            // リストに追加
            ret.add(s);
        }

        return ret;
    }

    private Map<GameTeam, Location> convertSpawnListToMap(List<String> spawns) {
        Map<GameTeam, Location> ret = new EnumMap<>(GameTeam.class);
        ret.clear();

        String[] data;
        String[] coord;

        World world = Bukkit.getWorld(plugin.getConfigs().getGameWorld());

        int line = 0;
        for (String s : spawns) {
            line++;
            // デリミタ分割
            data = s.split("@");
            if (data.length != 2) {
                log.log(Level.WARNING,logPrefix + "Skipping SpawnLine {0}: incorrect format (@)", line);
                continue;
            }

            // data[0] : チームチェック
            GameTeam team = null;
            for (GameTeam gt : GameTeam.values()) {
                if (gt.name().equalsIgnoreCase(data[0])) {
                    team = gt;
                }
            }
            if (team == null) {
                log.log(Level.WARNING,logPrefix + "Skipping SpawnLine {0}: undefined TeamName", line);
                continue;
            }

            // data[1] : 座標形式チェック
            coord = data[1].split(",");
            if (coord.length != 5) {
                log.log(Level.WARNING,logPrefix + "Skipping SpawnLine {0}: incorrect coord format (,)", line);
                continue;
            }

            Location loc = new Location(world, Double.valueOf(coord[0]), Double.valueOf(coord[1]), Double.valueOf(coord[2]), Float.valueOf(coord[3]), Float.valueOf(coord[4]));
            ret.put(team, loc);
        }

        return ret;
    }

    /* 拠点データを変換 */
    private List<String> convertBaseMapToList(Map<GameTeam, Cuboid> bases) {
        List<String> ret = new ArrayList<>();
        ret.clear();

        for (Map.Entry<GameTeam, Cuboid> entry : bases.entrySet()) {
            // RED@x,y,z@x,y,z
            String s = entry.getKey().name() + "@";

            Cuboid cuboid = entry.getValue();
            Location pos1 = cuboid.getPos1();
            Location pos2 = cuboid.getPos2();

            s = s + pos1.getBlockX() + "," + pos1.getBlockY() + "," + pos1.getBlockZ() + "@";
            s = s + pos2.getBlockX() + "," + pos2.getBlockY() + "," + pos2.getBlockZ();

            // リストに追加
            ret.add(s);
        }

        return ret;
    }

    private Map<GameTeam, Cuboid> convertBaseListToMap(List<String> bases) {
        Map<GameTeam, Cuboid> ret = new EnumMap<>(GameTeam.class);
        ret.clear();

        String[] data;
        String[] pos1;
        String[] pos2;

        World world = Bukkit.getWorld(plugin.getConfigs().getGameWorld());

        int line = 0;
        for (String s : bases) {
            line++;
            // デリミタ分割
            data = s.split("@");
            if (data.length != 3) {
                log.log(Level.WARNING,logPrefix + "Skipping BaseLine {0}: incorrect format (@)", line);
                continue;
            }

            // data[0] : チームチェック
            GameTeam team = null;
            for (GameTeam gt : GameTeam.values()) {
                if (gt.name().equalsIgnoreCase(data[0])) {
                    team = gt;
                }
            }
            if (team == null) {
                log.log(Level.WARNING,logPrefix + "Skipping BaseLine {0}: undefined TeamName", line);
                continue;
            }

            // data[1] : 座標形式チェック
            pos1 = data[1].split(",");
            if (pos1.length != 3) {
                log.log(Level.WARNING,logPrefix + "Skipping BaseLine {0}: incorrect 1st coord format (,)", line);
                continue;
            }

            // data[2] : 座標形式チェック
            pos2 = data[2].split(",");
            if (pos2.length != 3) {
                log.log(Level.WARNING,logPrefix + "Skipping BaseLine {0}: incorrect 2nd coord format (,)", line);
                continue;
            }

            ret.put(team, new Cuboid(new Location(world, Double.parseDouble(pos1[0]), Double.parseDouble(pos1[1]), Double.parseDouble(pos1[2])), new Location(world, Double.parseDouble(pos2[0]), Double.parseDouble(pos2[1]), Double.parseDouble(pos2[2]))));
        }

        return ret;
    }

    /* チェストデータを変換 */
    private List<String> convertChestMapToList(Set<Location> chests) {
        List<String> ret = new ArrayList<>();
        ret.clear();

        for (Location loc : chests) {
            // x,y,z
            String s = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();

            // リストに追加
            ret.add(s);
        }

        return ret;
    }

    private Set<Location> convertChestListToMap(List<String> chests) {
        Set<Location> ret = new HashSet<>();
        ret.clear();

        String[] coord;

        World world = Bukkit.getWorld(plugin.getConfigs().getGameWorld());

        int line = 0;
        for (String s : chests) {
            line++;
            // 座標形式チェック
            coord = s.split(",");
            if (coord.length != 3) {
                log.log(Level.WARNING,logPrefix + "Skipping ChestLine {0}: incorrect coord format (,)", line);
                continue;
            }

            ret.add(new Location(world, Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), Double.parseDouble(coord[2])));
        }

        return ret;
    }

    // プレイヤーのLocationオブジェクトから文字列に変換
    private String convertPlayerLocation(Location loc) {
        if (loc == null) return null;
        return loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }

    // convertPlayerLocationToStringで変換したプレイヤーLocationに戻す
    private Location convertPlayerLocation(String loc) {
        if (loc == null) return null;
        String[] coord = loc.split(",");
        if (coord.length != 5) return null;
        return new Location(Bukkit.getWorld(plugin.getConfigs().getGameWorld()), Double.valueOf(coord[0]), Double.valueOf(coord[1]), Double.valueOf(coord[2]), Float.valueOf(coord[3]), Float.valueOf(coord[4]));
    }
}
