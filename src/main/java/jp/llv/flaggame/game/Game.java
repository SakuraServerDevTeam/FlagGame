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
package jp.llv.flaggame.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;
import jp.llv.flaggame.reception.GameReception;
import jp.llv.flaggame.reception.Team;
import jp.llv.flaggame.reception.TeamColor;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;

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

    GameReception getReception();

    Team getTeam(TeamColor color);

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

    static Collection<GamePlayer> getPlayersIn(Team... teams) {
        return Arrays.stream(teams).flatMap(t -> t.getPlayers().stream()).collect(Collectors.toSet());
    }

    default Collection<GamePlayer> getPlayersNotIn(Team... teams) {
        Collection<GamePlayer> result = new HashSet<>(getPlayers());
        result.removeAll(getPlayersIn(teams));
        return result;
    }

}
