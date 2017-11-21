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
package jp.llv.flaggame.stage;

import java.util.ArrayList;
import jp.llv.flaggame.api.stage.Stage;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.exception.InvalidNameException;
import org.bukkit.Location;
import jp.llv.flaggame.api.reception.TeamColor;
import jp.llv.flaggame.stage.rollback.QueuedSerializeTask;
import jp.llv.flaggame.api.exception.RollbackException;
import jp.llv.flaggame.api.stage.rollback.SerializeTask;
import jp.llv.flaggame.api.session.SimpleReservable;
import jp.llv.flaggame.api.exception.ObjectiveCollisionException;
import jp.llv.flaggame.api.exception.ReservedException;
import syam.flaggame.util.Cuboid;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.api.stage.area.StageAreaInfo;
import jp.llv.flaggame.api.stage.objective.Spawn;
import org.bukkit.plugin.Plugin;
import jp.llv.flaggame.api.stage.objective.StageObjective;

/**
 * Stage (Stage.java)
 *
 * @author syam(syamn)
 */
public class BasicStage extends SimpleReservable<Stage> implements Stage {

    public static final Pattern NAME_REGEX = Pattern.compile("^[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

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
    private final List<StageObjective> objectives = new ArrayList<>();

    // 地点・エリア
    private boolean protect = true;
    private final AreaSet areas = new AreaSet();

    // stage description
    private String guide = "";
    private String description = "";
    private String author = "";
    private final Set<String> tags = new HashSet<>();

    /**
     * コンストラクタ
     *
     * @param name a name of a stage
     */
    public BasicStage(String name) {
        this.stageName = name;
    }

    @Override
    public void addObjective(StageObjective objective) throws ObjectiveCollisionException {
        if (objective.isBlock()) {
            if (objectives.stream()
                    .filter(StageObjective::isBlock)
                    .anyMatch(other -> other.getLocation().equals(objective.getLocation()))) {
                throw new ObjectiveCollisionException();
            }
        }
        objectives.add(objective);
    }

    @Override
    public Optional<StageObjective> getObjective(Location loc) {
        return objectives.stream()
                .filter(StageObjective::isBlock)
                .filter(objective -> objective.getLocation().equals(loc))
                .findAny();
    }

    @Override
    public <O extends StageObjective> Optional<O> getObjective(Location loc, Class<? extends O> clazz) {
        return getObjective(loc).map(obj -> clazz.isInstance(obj) ? clazz.cast(obj) : null);
    }

    @Override
    public boolean isObjective(Location loc) {
        return getObjective(loc).isPresent();
    }

    @Override
    public boolean isObjective(Class<? extends StageObjective> clazz, Location loc) {
        return getObjective(loc, clazz).isPresent();
    }

    @Override
    public void removeObjective(Location loc) {
        objectives.removeIf(obj -> obj.isBlock() && obj.getLocation().equals(loc));
    }

    @Override
    public void removeObjective(StageObjective objective) {
        objectives.remove(objective);
    }

    @Override
    public List<StageObjective> getObjectives() {
        return Collections.unmodifiableList(objectives);
    }

    @Override
    public <O> List<O> getObjectives(Class<? extends O> clazz) {
        return objectives.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    @Override
    public void addObjectives(Collection<? extends StageObjective> objectives) throws ReservedException, ObjectiveCollisionException {
        for (StageObjective objective : objectives) {
            addObjective(objective);
        }
    }

    @Override
    public AreaSet getAreas() {
        return areas;
    }

    public void setAreas(AreaSet areas) {
        this.areas.setAreaInfoMap(areas.getAreaInfoMap());
        this.areas.setAreaMap(areas.getAreaMap());
    }

    @Override
    public void setProtected(boolean protect) {
        this.protect = protect;
    }

    @Override
    public boolean isProtected() {
        return this.protect;
    }

    @Override
    public Set<TeamColor> getTeams() {
        return getObjectives(Spawn.class).stream().map(Spawn::getColor).collect(Collectors.toSet());
    }

    /**
     * ゲーム名を返す
     *
     * @return このゲームの名前
     */
    @Override
    public String getName() {
        return stageName;
    }

    /**
     * このゲームの制限時間(秒)を設定する
     *
     * @param sec 制限時間(秒)
     */
    @Deprecated
    @Override
    public void setGameTimeInSec(int sec) {
        gameTime = sec * 1000;
    }

    @Override
    public void setGameTime(long sec) {
        gameTime = sec;
    }

    /**
     * このゲームの制限時間(秒)を返す
     *
     * @return gametime in seconds
     */
    @Deprecated
    @Override
    public int getGameTimeInSec() {
        return (int) (gameTime / 1000);
    }

    @Override
    public long getGameTime() {
        return this.gameTime;
    }

    /**
     * チーム毎の人数上限を設定する
     *
     * @param limit チーム毎の人数上限
     */
    @Override
    public void setTeamLimit(int limit) {
        this.teamPlayerLimit = limit;
    }

    /**
     * チーム毎の人数上限を取得
     *
     * @return チーム毎の人数上限
     */
    @Override
    public int getTeamLimit() {
        return teamPlayerLimit;
    }

    @Override
    public double getKillScore() {
        return killScore;
    }

    @Override
    public void setKillScore(double killScore) {
        this.killScore = killScore;
    }

    @Override
    public double getDeathScore() {
        return deathScore;
    }

    @Override
    public void setDescription(String value) {
        description = value;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setAuthor(String value) {
        author = value;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setGuide(String value) {
        guide = value;
    }

    @Override
    public String getGuide() {
        return guide;
    }

    @Override
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public void setTags(Set<String> tags) {
        tags.forEach((tag) -> {
            try {
                this.addTag(tag);
            } catch (InvalidNameException ex) {
            }
        });
    }

    @Override
    public void addTag(String tag) throws InvalidNameException {
        if (!NAME_REGEX.matcher(tag).matches()) {
            throw new InvalidNameException();
        }
        tags.add(tag);
    }

    @Override
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    @Override
    public void setCooldown(long value) {
        cooldown = value;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public void setDeathScore(double deathScore) {
        this.deathScore = deathScore;
    }

    @Override
    public double getEntryFee() {
        return entryFee;
    }

    @Override
    public void setEntryFee(double entryFee) {
        this.entryFee = entryFee;
    }

    @Override
    public double getPrize() {
        return prize;
    }

    @Override
    public void setPrize(double prize) {
        this.prize = prize;
    }

    @Override
    public SerializeTask getInitialTask(Plugin plugin, Consumer<RollbackException> callback) {
        QueuedSerializeTask task = new QueuedSerializeTask(callback);
        for (String id : getAreas().getAreas()) {
            Cuboid area = getAreas().getArea(id);
            StageAreaInfo info = getAreas().getAreaInfo(id);
            for (StageAreaInfo.StageRollbackData rollback : info.getInitialRollbacks()) {
                task.offer(rollback.getTarget().load(this, area, t -> {
                }));
            }
        }
        return task;
    }

    /**
     * ステージの有効/無効を設定する
     *
     * @param available whether this stage is available
     */
    @Override
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * ステージの有効/無効を取得する
     *
     * @return available
     */
    @Override
    public boolean isAvailable() {
        return this.available;
    }

    @Override
    public Optional<Reception> getReception() {
        if (isReserved() && getReserver() instanceof Reception) {
            return Optional.of((Reception) getReserver());
        } else {
            return Optional.empty();
        }
    }

}
