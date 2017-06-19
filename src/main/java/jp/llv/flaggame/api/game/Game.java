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
package jp.llv.flaggame.api.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.reception.Team;
import jp.llv.flaggame.reception.TeamType;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.api.stage.Stage;

/**
 *
 * @author Toyblocks
 */
public interface Game extends Iterable<GamePlayer> {

    public enum State {

        PREPARATION, STARTED, FINISHED,;

    }

    default String getName() {
        return this.getReception().getID().toString();
    }

    void startNow() throws CommandException;

    void startLater(long ms) throws CommandException;

    void stopForcibly(String message);

    Reception getReception();

    Team getTeam(TeamType type);

    Collection<Team> getTeams();

    Stage getStage();

    State getState();

    @Override
    default Iterator<GamePlayer> iterator() {
        return this.getReception().iterator();
    }

    default Collection<GamePlayer> getPlayers() {
        return getReception().getPlayers();
    }

    static Collection<? extends GamePlayer> getPlayersIn(Iterable<? extends GamePlayer>... teams) {
        return Arrays.stream(teams).flatMap(t -> StreamSupport.stream(t.spliterator(), false)).collect(Collectors.toSet());
    }

    default Collection<? extends GamePlayer> getPlayersNotIn(Iterable<? extends GamePlayer>... teams) {
        Collection<? extends GamePlayer> result = new HashSet<>(getPlayers());
        result.removeAll(getPlayersIn(teams));
        return result;
    }

}
