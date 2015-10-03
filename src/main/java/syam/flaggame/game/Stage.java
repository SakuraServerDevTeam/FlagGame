/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.game;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import syam.flaggame.FlagGame;
import syam.flaggame.enums.GameTeam;
import syam.flaggame.manager.StageManager;
import syam.flaggame.util.Actions;
import syam.flaggame.util.Cuboid;

/**
 * Stage (Stage.java)
 *
 * @author syam(syamn)
 */
public class Stage {

    // Logger

    public static final Logger log = FlagGame.logger;
    private static final String logPrefix = FlagGame.logPrefix;

    // ステージ情報
    private final GameProfile profile;

    // 開始中のゲーム
    private Game game = null;

    // ***** ステージデータ *****
    private String fileName;
    private final String stageName;
    private int teamPlayerLimit = 16;

    private int gameTimeInSec = 6 * 60;

    private int award = 300;
    private int entryFee = 100;

    private boolean using = false;
    private boolean available = false;

    // フラッグ・チェスト
    private final Map<Location, Flag> flags = Collections.synchronizedMap(new HashMap<>());
    private final Set<Location> chests = Collections.newSetFromMap(new ConcurrentHashMap<Location, Boolean>());

    // 地点・エリア
    private Cuboid stageArea = null;
    private boolean stageProtect = true;
    private final Map<GameTeam, Location> spawnMap = Collections.synchronizedMap(new EnumMap<>(GameTeam.class));
    private final Map<GameTeam, Cuboid> baseMap = Collections.synchronizedMap(new EnumMap<>(GameTeam.class));
    private Location specSpawn = null;

    /**
     * コンストラクタ
     *
     * @param plugin
     * @param name
     */
    public Stage(final FlagGame plugin, final String name) {
        this.stageName = name;
        this.fileName = this.stageName + ".yml";

        this.profile = new GameProfile(name);

        // ステージマネージャにステージ登録
        StageManager.addStage(stageName, this);
    }

    /* ロールバックメソッド */
    /**
     * このゲームの全ブロックをロールバックする
     *
     * @return
     */
    
    public int rollbackFlags() {
        int count = 0;
        for (Flag flag : flags.values()) {
            if (flag.rollback()) {
                count++;
            }
        }
        return count;
    }

    /*
     * コンテナブロックを2ブロック下の同じコンテナから要素をコピーする
     */
    public int rollbackChests(CommandSender sender) {
        int count = 0;
        Player player = null;

        if (sender != null) {
            if (sender instanceof Player) {
                player = (Player) sender;
            }
        }

        for (Location loc : chests) {
            Block toBlock = loc.getBlock();
            Block fromBlock = toBlock.getRelative(BlockFace.DOWN, 2);

            // インベントリインターフェースを持たないブロックはスキップ
            if (!(toBlock.getState() instanceof InventoryHolder)) {
                log.log(Level.WARNING, logPrefix + "Block is not InventoryHolder!Rollback skipping.. Block: {0}", Actions.getBlockLocationString(toBlock.getLocation()));
                if (player != null) {
                    Actions.message(player, "&6" + this.getName() + "エラー:&c インベントリホルダではありません: " + Actions.getBlockLocationString(toBlock.getLocation()));
                }
                continue;
            }
            // 2ブロック下とブロックIDが違えばスキップ
            if (toBlock.getTypeId() != fromBlock.getTypeId()) {
                log.log(Level.WARNING, logPrefix + "BlockID unmatched!Rollback skipping.. Block: {0}", Actions.getBlockLocationString(toBlock.getLocation()));
                if (player != null) {
                    Actions.message(player, "&6" + this.getName() + "エラー:&c 2ブロック下と違うブロックです: " + Actions.getBlockLocationString(toBlock.getLocation()));
                }
                continue;
            }

            // 各チェストがインベントリホルダにキャスト出来ない場合例外にならないようtryで囲う
            InventoryHolder toContainer;
            InventoryHolder fromContainer; // チェストでなければここで例外 修正予定 →
            // 7/22修正済み
            try {
                toContainer = (InventoryHolder) toBlock.getState();
                fromContainer = (InventoryHolder) fromBlock.getState();
            } catch (ClassCastException ex) {
                log.log(Level.WARNING, logPrefix + "Container can''t cast to InventoryHolder! Rollback skipping.. Block: {0}", Actions.getBlockLocationString(toBlock.getLocation()));
                if (player != null) {
                    Actions.message(player, "&6" + this.getName() + "エラー:&c イベントリホルダにキャストできません: " + Actions.getBlockLocationString(toBlock.getLocation()));
                }
                continue;
            }

            // チェスト内容コピー
            try {
                ItemStack[] oldIs = fromContainer.getInventory().getContents().clone();
                ItemStack[] newIs = new ItemStack[oldIs.length];
                for (int i = 0; i < oldIs.length; i++) {
                    if (oldIs[i] == null) {
                        continue;
                    }
                    // newIs[i] = oldIs[i].clone(); // ItemStackシャローコピー不可
                    newIs[i] = new ItemStack(oldIs[i]); // ディープコピー
                }

                toContainer.getInventory().setContents(newIs);
            } catch (NullPointerException npe) {
                Actions.message(player, "&6" + this.getName() + "エラー:&c Occurred NullPointerException: " + Actions.getBlockLocationString(toBlock.getLocation()));
                npe.printStackTrace();
                continue;
            } catch (Exception ex) {
                Actions.message(player, "&6" + this.getName() + "エラー:&c Occurred Exception: " + Actions.getBlockLocationString(toBlock.getLocation()));
                ex.printStackTrace();
                continue;
            }
            count++;
        }
        return count;
    }

    
    public int rollbackChests() {
        return this.rollbackChests(null);
    }

    /* ***** フラッグ関係 ***** */

    /*
     * フラッグブロックとそのチームを設定する
     * 
     * @param loc
     *            設定するブロック座標
     * @param team
     *            設定するGameTeam
     */
    
    public void addFlag(Flag flag) {
        flags.put(flag.getLocation(), flag);
    }

    /**
     * フラッグブロックのフラッグを返す
     *
     * @param loc 調べるブロックの座標
     * @return GameTeam または存在しない場合 null
     */
    
    public Flag getFlag(Location loc) {
        return flags.get(loc);
    }

    /**
     * フラッグブロックかどうか返す
     *
     * @param loc location
     * @return boolean
     */
    public boolean isFlag(Location loc) {
        return flags.containsKey(loc);
    }

    /**
     * フラッグブロックを削除する
     *
     * @param loc 削除するフラッグのブロック座標
     */
    
    public void removeFlag(Location loc) {
        flags.remove(loc);
    }

    /*
     * フラッグマップを一括取得
     * 
     * @return
     */
    
    public Map<Location, Flag> getFlags() {
        return flags;
    }

    /**
     * フラッグマップを一括設定
     *
     * @param flags
     */
    
    public void setFlags(Map<Location, Flag> flags) {
        this.flags.clear();
        this.flags.putAll(flags);
    }

    /**
     * すべてのフラッグの状態をチェックする
     *
     * @return {@code Map<FlagState, HashMap<FlagType, Integer>>}
     */
    
    public Map<GameTeam, Map<Byte, Integer>> checkFlag() {
        // 各チームのポイントを格納する
        Map<GameTeam,  Map<Byte, Integer>> ret = new EnumMap<>(GameTeam.class);

        // 全フラッグを回す
        flags.values().forEach(flag -> {
            GameTeam state = flag.getOwner(); // フラッグの現在状態
            Map<Byte, Integer> score = ret.get(state);
            if (score == null) {
                ret.put(state, score = new HashMap<>());
            }
            Integer count = score.get(flag.getFlagPoint());
            score.put(flag.getFlagPoint(), count != null ? count + 1 : 1);
        });
        return ret;
    }

    /* ***** チェスト関係 ***** */
    /**
     * チェストを設定する
     *
     * @param loc チェストの座標
     */
    
    public void setChest(Location loc) {
        chests.add(loc);
    }

    /**
     * チェストブロックを返す
     *
     * @param loc 調べるブロックの座標
     * @return GameTeam または存在しない場合 null
     */
    
    public Block getChest(Location loc) {
        if (chests.contains(loc)) {
            return loc.getBlock();
        } else {
            return null;
        }
    }

    public boolean isChest(Location loc) {
        return chests.contains(loc);
    }

    /**
     * チェストブロックを削除する
     *
     * @param loc 削除するチェストのブロック座標
     */
    
    public void removeChest(Location loc) {
        chests.remove(loc);
    }

    /**
     * チェストブロックマップを一括取得する
     *
     * @return チェストブロックマップ Map<Location, Block>
     */
    
    public Set<Location> getChests() {
        return chests;
    }

    /**
     * チェストブロックマップを一括設定する
     *
     * @param chests 設定する元のLocation, Blockマップ
     */
    
    public void setChests(Set<Location> chests) {
        this.chests.clear();
        this.chests.addAll(chests);
    }

    /* ***** スポーン地点関係 ***** */

    /*
     * チームのスポーン地点を設置/取得する
     * 
     * @param loc
     */
    
    public void setSpawn(GameTeam team, Location loc) {
        spawnMap.put(team, loc);
    }

    
    public Location getSpawn(GameTeam team) {
        if (team == null || !spawnMap.containsKey(team)) {
            return null;
        }
        return spawnMap.get(team).clone();
    }

    
    public Map<GameTeam, Location> getSpawns() {
        return Collections.unmodifiableMap(spawnMap);
    }

    
    public void setSpawns(Map<GameTeam, Location> spawns) {
        this.spawnMap.clear();
        this.spawnMap.putAll(spawns);
    }

    
    public Location getSpecSpawn() {
        return this.specSpawn != null ? this.specSpawn.clone() : null;
    }

    
    public void setSpecSpawn(Location loc) {
        this.specSpawn = loc != null ? loc.clone() : null;
    }

    /* ***** エリア関係 ***** */
    // ステージ
    
    public void setStage(Location pos1, Location pos2) {
        stageArea = new Cuboid(pos1, pos2);
    }

    
    public void setStage(Cuboid cuboid) {
        this.stageArea = cuboid;
    }

    
    public Cuboid getStage() {
        return this.stageArea;
    }

    public boolean hasStage() {
        return this.stageArea != null;
    }

    
    public void setStageProtected(boolean protect) {
        this.stageProtect = protect;
    }

    
    public boolean isStageProtected() {
        return this.stageProtect;
    }

    // 拠点
    
    public void setBase(GameTeam team, Location pos1, Location pos2) {
        baseMap.put(team, new Cuboid(pos1, pos2));
    }

    
    public void setBase(GameTeam team, Cuboid cuboid) {
        baseMap.put(team, cuboid);
    }

    
    public Cuboid getBase(GameTeam team) {
        if (team == null || !baseMap.containsKey(team)) {
            return null;
        }
        return baseMap.get(team);
    }

    
    public Map<GameTeam, Cuboid> getBases() {
        return baseMap;
    }

    
    public void setBases(Map<GameTeam, Cuboid> bases) {
        this.baseMap.clear();
        this.baseMap.putAll(bases);
    }

    /* getter / setter */
    /**
     * ゲームステージプロファイルを返す
     *
     * @return GameProfile
     */
    
    public GameProfile getProfile() {
        return this.profile;
    }

    /**
     * ファイル名を設定
     *
     * @param filename
     */
    
    public void setFileName(String filename) {
        this.fileName = filename;
    }

    /**
     * ファイル名を取得
     *
     * @return
     */
    
    public String getFileName() {
        return fileName;
    }

    /**
     * ゲーム名を返す
     *
     * @return このゲームの名前
     */
    
    public String getName() {
        return stageName;
    }

    /**
     * このゲームの制限時間(秒)を設定する
     *
     * @param sec 制限時間(秒)
     */
    
    public void setGameTime(int sec) {
        gameTimeInSec = sec;
    }

    /**
     * このゲームの制限時間(秒)を返す
     *
     * @return
     */
    
    public int getGameTime() {
        return gameTimeInSec;
    }

    /**
     * チーム毎の人数上限を設定する
     *
     * @param limit チーム毎の人数上限
     */
    
    public void setTeamLimit(int limit) {
        this.teamPlayerLimit = limit;
    }

    /**
     * チーム毎の人数上限を取得
     *
     * @return チーム毎の人数上限
     */
    
    public int getTeamLimit() {
        return teamPlayerLimit;
    }

    /**
     * 賞金を設定する
     *
     * @param award 賞金
     */
    
    public void setAward(int award) {
        if (award < 0) {
            award = 0;
        }
        this.award = award;
    }

    /**
     * 賞金を取得する
     *
     * @return 賞金
     */
    
    public int getAward() {
        return award;
    }

    /**
     * 参加料を設定する
     *
     * @param entryFee 参加料
     */
    
    public void setEntryFee(int entryFee) {
        if (entryFee < 0) {
            entryFee = 0;
        }
        this.entryFee = entryFee;
    }

    /**
     * 参加料を取得する
     *
     * @return 参加料
     */
    
    public int getEntryFee() {
        return entryFee;
    }

    /**
     * ステージの有効/無効を設定する
     *
     * @param available
     */
    
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * ステージの有効/無効を取得する
     *
     * @return available
     */
    
    public boolean isAvailable() {
        return this.available;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }

    public boolean isUsing() {
        return this.using;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return this.game;
    }
}
