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
package jp.llv.flaggame.api.stage;

import jp.llv.flaggame.api.stage.area.StageAreaSet;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.reception.TeamType;
import jp.llv.flaggame.api.exception.RollbackException;
import jp.llv.flaggame.api.stage.rollback.SerializeTask;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import jp.llv.flaggame.api.exception.ObjectiveCollisionException;
import jp.llv.flaggame.api.exception.ReservedException;
import jp.llv.flaggame.api.session.Reservable;
import jp.llv.flaggame.api.stage.objective.StageObjective;

/**
 *
 * @author toyblocks
 */
public interface Stage extends Reservable<Stage> {

    void addObjective(StageObjective objective) throws ObjectiveCollisionException;

    void addObjectives(Collection<? extends StageObjective> objectives) throws ReservedException, ObjectiveCollisionException;

    StageAreaSet getAreas();

    String getAuthor();

    long getCooldown();

    double getDeathScore();

    String getDescription();

    double getEntryFee();

    long getGameTime();

    /**
     * このゲームの制限時間(秒)を返す
     *
     * @return gametime in seconds
     */
    @Deprecated
    int getGameTimeInSec();

    String getGuide();

    SerializeTask getInitialTask(Plugin plugin, Consumer<RollbackException> callback);

    double getKillScore();

    /**
     * ゲーム名を返す
     *
     * @return このゲームの名前
     */
    String getName();

    Optional<StageObjective> getObjective(Location loc);

    <O extends StageObjective> Optional<O> getObjective(Location loc, Class<? extends O> clazz);

    Map<Location, StageObjective> getObjectives();

    <O> Map<Location, O> getObjectives(Class<? extends O> clazz);

    double getPrize();

    Optional<Reception> getReception();

    Location getSpawn(TeamType team);

    Map<TeamColor, Location> getSpawns();

    Optional<Location> getSpecSpawn();

    /**
     * チーム毎の人数上限を取得
     *
     * @return チーム毎の人数上限
     */
    int getTeamLimit();

    Set<TeamColor> getTeams();

    /**
     * ステージの有効/無効を取得する
     *
     * @return available
     */
    boolean isAvailable();

    boolean isObjective(Location loc);

    boolean isObjective(Class<? extends StageObjective> clazz, Location loc);

    boolean isProtected();

    void removeObjective(Location loc);

    void removeObjective(StageObjective objective);

    void setAuthor(String value);

    /**
     * ステージの有効/無効を設定する
     *
     * @param available
     */
    void setAvailable(boolean available);

    void setCooldown(long value);

    void setDeathScore(double deathScore);

    void setDescription(String value);

    void setEntryFee(double entryFee);

    void setGameTime(long sec);

    /**
     * このゲームの制限時間(秒)を設定する
     *
     * @param sec 制限時間(秒)
     */
    @Deprecated
    void setGameTimeInSec(int sec);

    void setGuide(String value);

    void setKillScore(double killScore);

    void setPrize(double prize);

    void setProtected(boolean protect);

    /* ***** スポーン地点関係 ***** */
 /*
     * チームのスポーン地点を設置/取得する
     *
     * @param loc
     * @throws syam.flaggame.exception.StageReservedException when this stage is being used
     */
    void setSpawn(TeamColor team, Location loc);

    void setSpawns(Map<TeamColor, Location> spawns);

    void setSpecSpawn(Location loc);

    /**
     * チーム毎の人数上限を設定する
     *
     * @param limit チーム毎の人数上限
     */
    void setTeamLimit(int limit);

}
