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
package syam.flaggame.game;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import jp.llv.flaggame.reception.GameReception;
import org.bukkit.Location;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.reception.TeamType;
import jp.llv.flaggame.rollback.QueuedSerializeTask;
import jp.llv.flaggame.rollback.RollbackException;
import jp.llv.flaggame.rollback.SerializeTask;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.ObjectiveCollisionException;
import syam.flaggame.exception.StageReservedException;
import syam.flaggame.game.objective.Objective;
import syam.flaggame.util.Cuboid;

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
    private final String stageName;
    private int teamPlayerLimit = 16;
    
    private long gameTime = 6 * 60 * 1000;
    private long cooldown = 0;
    
    private boolean available = false;
    
    //キルデススコア
    private double killScore = 0, deathScore = 0;
    
    //賞金
    private double entryFee = 0;
    private double prize = 0;

    // フラッグ・チェスト
    private final Map<Location, Objective> objectives = new HashMap<>();

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
    
    public void addObjective(Objective objective) throws ObjectiveCollisionException {
        if (objectives.containsKey(objective.getLocation())) {
            throw new ObjectiveCollisionException();
        }
        objectives.put(objective.getLocation(), objective);
    }
    
    public Optional<Objective> getObjective(Location loc) {
        return Optional.ofNullable(objectives.get(loc));
    }
    
    public <O extends Objective> Optional<O> getObjective(Location loc, Class<? extends O> clazz) {
        Objective objective = objectives.get(loc);
        if (clazz.isInstance(objective)) {
            return Optional.of(clazz.cast(objective));
        } else {
            return Optional.empty();
        }
    }
    
    public boolean isObjective(Location loc) {
        return getObjective(loc).isPresent();
    }
    
    public boolean isObjective(Class<? extends Objective> clazz, Location loc) {
        return getObjective(loc, clazz).isPresent();
    }
    
    public void removeObjective(Location loc) {
        objectives.remove(loc);
    }
    
    public void removeObjective(Objective objective) {
        objectives.remove(objective.getLocation(), objective);
    }
    
    public Map<Location, Objective> getObjectives() {
        return Collections.unmodifiableMap(objectives);
    }
    
    public <O> Map<Location, O> getObjectives(Class<? extends O> clazz) {
        Map<Location, O> result = new HashMap<>();
        objectives.forEach((l, o) -> {
            if (clazz.isInstance(o)) {
                result.put(l, clazz.cast(o));
            }
        });
        return Collections.unmodifiableMap(result);
    }
    
    public void addObjectives(Collection<? extends Objective> objectives) throws StageReservedException, ObjectiveCollisionException {
        for (Objective objective : objectives) {
            addObjective(objective);
        }
    }

    /* ***** スポーン地点関係 ***** */

    /*
     * チームのスポーン地点を設置/取得する
     * 
     * @param loc
     * @throws syam.flaggame.exception.StageReservedException when this stage is being used
     */
    public void setSpawn(TeamColor team, Location loc) {
        if (loc == null) {
            spawnMap.remove(team);
        } else {
            spawnMap.put(team, loc);
        }
    }
    
    public Location getSpawn(TeamType team) {
        if (team == null || !spawnMap.containsKey(team.toColor())) {
            return null;
        }
        return spawnMap.get(team.toColor()).clone();
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
        this.specSpawn = loc != null ? loc.clone() : null;
    }

    public AreaSet getAreas() {
        return areas;
    }
    
    public void setAreas(AreaSet areas) {
        this.areas.setAreaInfoMap(areas.getAreaInfoMap());
        this.areas.setAreaMap(areas.getAreaMap());
    }
    
    public void setProtected(boolean protect) {
        this.protect = protect;
    }
    
    public boolean isProtected() {
        return this.protect;
    }
    
    public Set<TeamColor> getTeams() {
        return Collections.unmodifiableSet(this.getSpawns().keySet());
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
        gameTime = sec * 1000;
    }
    
    public void setGameTime(long sec) {
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

    public void setDeathScore(double deathScore)  {
        this.deathScore = deathScore;
    }

    public double getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(double entryFee) {
        this.entryFee = entryFee;
    }

    public double getPrize() {
        return prize;
    }

    public void setPrize(double prize)  {
        this.prize = prize;
    }
    
    public SerializeTask getInitialTask(FlagGame plugin, Consumer<RollbackException> callback) {
        QueuedSerializeTask task = new QueuedSerializeTask(callback);
        for (String id : getAreas().getAreas()) {
            Cuboid area = getAreas().getArea(id);
            AreaInfo info = getAreas().getAreaInfo(id);
            for (AreaInfo.RollbackData rollback : info.getInitialRollbacks()) {
                task.offer(rollback.getTarget().load(plugin, this, area, t -> {}));
            }
        }
        return task;
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
    
    public boolean isReserved() {
        return this.reception != null;
    }
    
    /**
     * Reserve this reception.
     * @param reception reception going to use this reception, or null.
     * @return reservation object for release this stage.
     * @throws StageReservedException if this stage has already reserved.
     */
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
    
    public void validate() throws NullPointerException {
        if (!available || spawnMap.isEmpty()) {
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
