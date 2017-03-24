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

import jp.llv.flaggame.game.basic.objective.Flag;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import jp.llv.flaggame.game.basic.objective.*;
import jp.llv.flaggame.reception.GameReception;

import org.bukkit.Location;
import org.bukkit.block.Block;

import jp.llv.flaggame.reception.TeamColor;
import syam.flaggame.exception.StageReservedException;

/**
 * Stage (Stage.java)
 *
 * @author syam(syamn)
 */
public class Stage {
    
    public static final Pattern NAME_REGEX = Pattern.compile("^[a-z0-9](?:[a-z0-9_-]*[a-z0-9])?$");

    // 開始中のゲーム
    private GameReception reception;

    // ***** ステージデータ *****
    private String fileName;
    private final String stageName;
    private int teamPlayerLimit = 16;
    
    private long gameTime = 6 * 60 * 1000;
    private long cooldown = 0;
    
    private boolean available = false;
    
    //キルデススコア
    private double killScore = 0, deathScore = 0;

    // フラッグ・チェスト
    private final Map<Location, Flag> flags = Collections.synchronizedMap(new HashMap<>());
    private final Map<Location, Nexus> nexuses = Collections.synchronizedMap(new HashMap<>());
    private final Map<Location, BannerSpawner> bannerSpawners = Collections.synchronizedMap(new HashMap<>());
    private final Map<Location, BannerSlot> bannerSlots = Collections.synchronizedMap(new HashMap<>());
    private final Set<Location> chests = Collections.newSetFromMap(new ConcurrentHashMap<Location, Boolean>());

    // 地点・エリア
    private boolean protect = true;
    private final Map<TeamColor, Location> spawnMap = Collections.synchronizedMap(new EnumMap<>(TeamColor.class));
    private AreaSet areas = new AreaSet();
    private Location specSpawn = null;
    
    // stage description
    private String guide = "";
    private String description = "";
    private String author = "";

    /**
     * コンストラクタ
     *
     * @param name
     */
    public Stage(String name) {
        this.stageName = name;
    }
    
    public void addFlag(Flag flag) throws StageReservedException {
        checkEditable();
        flags.put(flag.getLocation(), flag);
    }
    
    public Optional<Flag> getFlag(Location loc) {
        return Optional.ofNullable(flags.get(loc));
    }
    
    public void removeFlag(Location loc) throws StageReservedException {
        checkEditable();
        flags.remove(loc);
    }
    
    public Map<Location, Flag> getFlags() {
        return Collections.unmodifiableMap(flags);
    }
    
    public void setFlags(Collection<Flag> flags) throws StageReservedException {
        checkEditable();
        this.flags.clear();
        flags.stream().forEach(f -> this.flags.put(f.getLocation(), f));
    }
    
    public void addNexus(Nexus nexus) throws StageReservedException {
        checkEditable();
        this.nexuses.put(nexus.getLocation(), nexus);
    }
    
    public void removeNexus(Location loc) throws StageReservedException {
        checkEditable();
        this.nexuses.remove(loc);
    }
    
    public Optional<Nexus> getNexus(Location location) {
        return Optional.ofNullable(this.nexuses.get(location));
    }
    
    public Map<Location, Nexus> getNexuses() {
        return Collections.unmodifiableMap(this.nexuses);
    }
    
    public void setNexuses(Collection<Nexus> nexuses) throws StageReservedException {
        checkEditable();
        this.nexuses.clear();
        nexuses.stream().forEach(f -> this.nexuses.put(f.getLocation(), f));
    }
    
    public void addBannerSpawner(BannerSpawner spawner) throws StageReservedException {
        checkEditable();
        this.bannerSpawners.put(spawner.getLocation(), spawner);
    }
    
    public void removeBannerSpawner(Location loc) throws StageReservedException {
        checkEditable();
        this.bannerSpawners.remove(loc);
    }
    
    public Optional<BannerSpawner> getBannerSpawner(Location loc) {
        return Optional.ofNullable(this.bannerSpawners.get(loc));
    }
    
    public Map<Location, BannerSpawner> getBannerSpawners() {
        return Collections.unmodifiableMap(this.bannerSpawners);
    }
    
    public void setBannerSpawners(Collection<BannerSpawner> spawners) throws StageReservedException {
        checkEditable();
        this.bannerSpawners.clear();
        spawners.forEach(s -> this.bannerSpawners.put(s.getLocation(), s));
    }
    
    public void addBannerSlot(BannerSlot slot) throws StageReservedException {
        checkEditable();
        this.bannerSlots.put(slot.getLocation(), slot);
    }
    
    public void removeBannerSlot(Location loc) throws StageReservedException {
        checkEditable();
        this.bannerSlots.remove(loc);
    }
    
    public Optional<BannerSlot> getBannerSlot(Location loc) {
        return Optional.ofNullable(this.bannerSlots.get(loc));
    }
    
    public Map<Location, BannerSlot> getBannerSlots() {
        return Collections.unmodifiableMap(this.bannerSlots);
    }
    
    public void setBannerSlots(Collection<BannerSlot> slots) throws StageReservedException {
        checkEditable();
        this.bannerSlots.clear();
        slots.forEach(s -> this.bannerSlots.put(s.getLocation(), s));
    }

    /* ***** チェスト関係 ***** */
    /**
     * チェストを設定する
     *
     * @param loc チェストの座標
     * @throws syam.flaggame.exception.StageReservedException when this stage is
     * being used
     */
    public void setChest(Location loc) throws StageReservedException {
        checkEditable();
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
     * @throws syam.flaggame.exception.StageReservedException when this stage is
     * being used
     */
    public void removeChest(Location loc) throws StageReservedException {
        checkEditable();
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
     * @throws syam.flaggame.exception.StageReservedException when this stage is
     * being used
     */
    public void setChests(Collection<Location> chests) throws StageReservedException {
        checkEditable();
        this.chests.clear();
        this.chests.addAll(chests);
    }

    /* ***** スポーン地点関係 ***** */

    /*
     * チームのスポーン地点を設置/取得する
     * 
     * @param loc
     * @throws syam.flaggame.exception.StageReservedException when this stage is being used
     */
    public void setSpawn(TeamColor team, Location loc) throws StageReservedException {
        checkEditable();
        if (loc == null) {
            spawnMap.remove(team);
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
    
    public void setSpecSpawn(Location loc) throws StageReservedException {
        checkEditable();
        this.specSpawn = loc != null ? loc.clone() : null;
    }

    public AreaSet getAreas() {
        return areas;
    }
    
    /*package*/ void setAreas(AreaSet areas) {
        this.areas = areas;
    }
    
    public void setProtected(boolean protect) throws StageReservedException {
        checkEditable();
        this.protect = protect;
    }
    
    public boolean isProtected() {
        return this.protect;
    }
    
    public Set<TeamColor> getTeams() {
        return Collections.unmodifiableSet(this.getSpawns().keySet());
    }

    /**
     * ファイル名を取得
     *
     * @return name of the stage configuration file
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
     * @throws syam.flaggame.exception.StageReservedException when this stage is
     * being used
     */
    @Deprecated
    public void setGameTimeInSec(int sec) throws StageReservedException {
        checkEditable();
        gameTime = sec * 1000;
    }
    
    public void setGameTime(long sec) throws StageReservedException {
        checkEditable();
        gameTime = sec;
    }

    /**
     * このゲームの制限時間(秒)を返す
     *
     * @return gametime in seconds
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
     * @throws syam.flaggame.exception.StageReservedException when this stage is
     * being used
     */
    public void setTeamLimit(int limit) throws StageReservedException {
        checkEditable();
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

    public double getKillScore() {
        return killScore;
    }

    public void setKillScore(double killScore) {
        this.killScore = killScore;
    }

    public double getDeathScore() {
        return deathScore;
    }
    
    public void setDescription(String value) {
        description = value;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setAuthor(String value) {
        author = value;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setGuide(String value) {
        guide = value;
    }
    
    public String getGuide() {
        return guide;
    }
    
    public void setCooldown(long value) {
        cooldown = value;
    }
    
    public long getCooldown() {
        return cooldown;
    }

    public void setDeathScore(double deathScore) {
        this.deathScore = deathScore;
    }

    /**
     * ステージの有効/無効を設定する
     *
     * @param available
     * @throws syam.flaggame.exception.StageReservedException when this stage is
     * being used
     */
    public void setAvailable(boolean available) throws StageReservedException {
        checkEditable();
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
        checkEditable();
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
        throw new UnsupportedOperationException("WIP");
    }
    
    public void validate() throws NullPointerException {
        if (!available || spawnMap.isEmpty()) {
            throw new NullPointerException();
        }
    }
    
    private void checkEditable() throws StageReservedException {
        if (this.isReserved()) {
            throw new StageReservedException();
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
