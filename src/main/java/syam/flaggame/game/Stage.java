/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import jp.llv.flaggame.game.basic.objective.Nexus;
import jp.llv.flaggame.reception.GameReception;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import syam.flaggame.FlagGame;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.exception.StageReservedException;
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

    public static final Pattern NAME_REGEX = Pattern.compile("^[a-z0-9](?:[a-z0-9_-]*[a-z0-9])?$");

    // ステージ情報
    private final StageProfile profile;

    // 開始中のゲーム
    private GameReception reception;

    // ***** ステージデータ *****
    private String fileName;
    private final String stageName;
    private int teamPlayerLimit = 16;

    private long gameTime = 6 * 60 * 1000;

    private boolean available = false;

    // フラッグ・チェスト
    private final Map<Location, Flag> flags = Collections.synchronizedMap(new HashMap<>());
    private final Map<Location, Nexus> nexuses = Collections.synchronizedMap(new HashMap<>());
    private final Set<Location> chests = Collections.newSetFromMap(new ConcurrentHashMap<Location, Boolean>());

    // 地点・エリア
    private Cuboid stageArea = null;
    private boolean stageProtect = true;
    private final Map<TeamColor, Location> spawnMap = Collections.synchronizedMap(new EnumMap<>(TeamColor.class));
    private final Map<TeamColor, Cuboid> baseMap = Collections.synchronizedMap(new EnumMap<>(TeamColor.class));
    private Location specSpawn = null;

    /**
     * コンストラクタ
     *
     * @param name
     * @param profile
     */
    public Stage(final String name, StageProfile profile) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.stageName = name;
        this.fileName = this.stageName + ".yml";

        this.profile = profile;
    }

    public Stage(String name) {
        this(name, new StageProfile());
    }

    public StageProfile getProfile() {
        return this.profile;
    }

    /*
     * フラッグブロックとそのチームを設定する
     * 
     * @param loc 設定するブロック座標
     * @param team 設定するGameTeam
     */
    public void addFlag(Flag flag) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
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
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        flags.remove(loc);
    }

    /*
     * フラッグマップを一括取得
     * 
     * @return
     */
    public Map<Location, Flag> getFlags() {
        return Collections.unmodifiableMap(flags);
    }

    /**
     * フラッグマップを一括設定
     *
     * @param flags
     */
    public void setFlags(Collection<Flag> flags) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        this.flags.clear();
        flags.stream().forEach(f -> this.flags.put(f.getLocation(), f));
    }

    public void addNexus(Nexus nexus) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        this.nexuses.put(nexus.getLocation(), nexus);
    }

    public void removeNexus(Nexus nexus) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        for (Iterator<Nexus> it = this.nexuses.values().iterator(); it.hasNext();) {
            if (it.next() == nexus) {
                it.remove();
            }
        }
    }

    public Optional<Nexus> getNexus(Location location) {
        return Optional.ofNullable(this.nexuses.get(location));
    }

    public boolean isNexus(Location location) {
        return this.nexuses.containsKey(location);
    }

    public Map<Location, Nexus> getNexuses() {
        return Collections.unmodifiableMap(this.nexuses);
    }

    public void setNexuses(Collection<Nexus> nexuses) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        this.nexuses.clear();
        nexuses.stream().forEach(f -> this.nexuses.put(f.getLocation(), f));
    }

    /**
     * すべてのフラッグの状態をチェックする
     *
     * @return {@code Map<FlagState, HashMap<FlagType, Integer>>}
     */
    public Map<TeamColor, Map<Byte, Integer>> checkFlag() {
        // 各チームのポイントを格納する
        Map<TeamColor, Map<Byte, Integer>> ret = new EnumMap<>(TeamColor.class);
        for (TeamColor c : this.getSpawns().keySet()) {
            ret.put(c, new HashMap<>());
        }
        // 全フラッグを回す
        flags.values().forEach(flag -> {
            TeamColor state = flag.getOwner(); // フラッグの現在状態
            if (state == null) {
                return;
            }
            Map<Byte, Integer> score = ret.get(state);
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
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
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
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        chests.remove(loc);
    }

    /**
     * チェストブロックマップを一括取得する
     *
     * @return チェストブロックマップ {@code Map<Location, Block>}
     */
    public Set<Location> getChests() {
        return Collections.unmodifiableSet(chests);
    }

    /**
     * チェストブロックマップを一括設定する
     *
     * @param chests 設定する元のLocation, Blockマップ
     */
    public void setChests(Collection<Location> chests) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        this.chests.clear();
        this.chests.addAll(chests);
    }

    /* ***** スポーン地点関係 ***** */

    /*
     * チームのスポーン地点を設置/取得する
     * 
     * @param loc
     */
    public void setSpawn(TeamColor team, Location loc) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        if (loc == null) {
            spawnMap.remove(loc);
        } else {
            spawnMap.put(team, loc);
        }
    }

    public Location getSpawn(TeamColor team) {
        if (team == null || !spawnMap.containsKey(team)) {
            return null;
        }
        return spawnMap.get(team).clone();
    }

    public Map<TeamColor, Location> getSpawns() {
        return Collections.unmodifiableMap(spawnMap);
    }

    public void setSpawns(Map<TeamColor, Location> spawns) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        this.spawnMap.clear();
        this.spawnMap.putAll(spawns);
    }

    public Optional<Location> getSpecSpawn() {
        return Optional.ofNullable(this.specSpawn).map(Location::clone);
    }

    public void setSpecSpawn(Location loc) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        this.specSpawn = loc != null ? loc.clone() : null;
    }

    /* ***** エリア関係 ***** */
    // ステージ
    public void setStageArea(Location pos1, Location pos2) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        this.setStageArea(new Cuboid(pos1, pos2));
    }

    public void setStageArea(Cuboid cuboid) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        this.stageArea = cuboid;
    }

    public Cuboid getStageArea() {
        return this.stageArea;
    }

    public boolean hasStageArea() {
        return this.stageArea != null;
    }

    public void setStageProtected(boolean protect) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        this.stageProtect = protect;
    }

    public boolean isStageProtected() {
        return this.stageProtect;
    }

    // 拠点
    public void setBase(TeamColor team, Location pos1, Location pos2) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        baseMap.put(team, new Cuboid(pos1, pos2));
    }

    public void setBase(TeamColor team, Cuboid cuboid) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        if (cuboid == null) {
            baseMap.remove(team);
        } else {
            baseMap.put(team, cuboid);
        }
    }

    public Cuboid getBase(TeamColor team) {
        if (team == null || !baseMap.containsKey(team)) {
            return null;
        }
        return baseMap.get(team);
    }

    public Map<TeamColor, Cuboid> getBases() {
        return baseMap;
    }

    public void setBases(Map<TeamColor, Cuboid> bases) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        this.baseMap.clear();
        this.baseMap.putAll(bases);
    }

    public Set<TeamColor> getTeams() {
        return Collections.unmodifiableSet(this.getBases().keySet());
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
    @Deprecated
    public void setGameTimeInSec(int sec) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        gameTime = sec * 1000;
    }

    public void setGameTime(long sec) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
        gameTime = sec;
    }

    /**
     * このゲームの制限時間(秒)を返す
     *
     * @return
     */
    @Deprecated
    public int getGameTimeInSec() {
        return (int) (gameTime / 1000);
    }

    public long getGameTime() {
        return this.gameTime;
    }

    /**
     * チーム毎の人数上限を設定する
     *
     * @param limit チーム毎の人数上限
     */
    public void setTeamLimit(int limit) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
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
     * ステージの有効/無効を設定する
     *
     * @param available
     */
    public void setAvailable(boolean available) {
        if (this.isReserved()) {
            throw new IllegalStateException("This stage has been reserved!");
        }
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

    public boolean isReserved() {
        return this.reception != null;
    }

    public Reservation reserve(GameReception reception) throws StageReservedException {
        if (this.isReserved()) {
            throw new StageReservedException();
        }
        this.reception = reception;
        return new Reservation(this);
    }

    private void release() {
        this.reception = null;
    }

    public Optional<GameReception> getReception() {
        return Optional.ofNullable(this.reception);
    }

    public void initialize() {
        this.rollbackChests();
        this.rollbackFlags();
    }

    /* ロールバックメソッド */
    /**
     * このゲームの全ブロックをロールバックする
     *
     * @return
     */
    private void rollbackFlags() {
        for (Flag flag : flags.values()) {
            flag.rollback();
        }
    }

    /*
     * コンテナブロックを2ブロック下の同じコンテナから要素をコピーする
     */
    private void rollbackChests() {
        for (Location loc : chests) {
            Block toBlock = loc.getBlock();
            Block fromBlock = toBlock.getRelative(BlockFace.DOWN, 2);

            // インベントリインターフェースを持たないブロックはスキップ
            if (!(toBlock.getState() instanceof InventoryHolder)) {
                log.log(Level.WARNING, logPrefix + "Block is not InventoryHolder!Rollback skipping.. Block: {0}", Actions.getBlockLocationString(toBlock.getLocation()));
                continue;
            }
            // 2ブロック下とブロックIDが違えばスキップ
            if (toBlock.getType() != fromBlock.getType()) {
                log.log(Level.WARNING, logPrefix + "BlockID unmatched!Rollback skipping.. Block: {0}", Actions.getBlockLocationString(toBlock.getLocation()));
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
                npe.printStackTrace();
            } catch (Exception ex) {
                log.log(Level.WARNING, "Invalid inventory@{0}", toBlock.getLocation());
                log.log(Level.WARNING, ex.getMessage());
            }
        }
    }

    public void validate() throws NullPointerException {
        Objects.requireNonNull(stageArea);
        if (!available || !stageProtect || spawnMap.isEmpty() || baseMap.isEmpty()) {
            throw new NullPointerException();
        } else if (!Objects.equals(spawnMap.keySet(), baseMap.keySet())) {
            throw new NullPointerException();
        }
    }

    public static final class Reservation {

        private Stage reserved;

        private Reservation(Stage stage) {
            this.reserved = stage;
        }

        public void release() {
            if (this.reserved == null) {
                throw new IllegalStateException("This reservation is invalid");
            }
            this.reserved.release();
            this.reserved = null;
        }
    }

}
