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
package jp.llv.flaggame.reception;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.profile.RecordStream;
import jp.llv.flaggame.api.session.Reserver;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public interface GameReception extends Reserver, Iterable<GamePlayer> {

    void open(List<String> args) throws FlagGameException;

    void close(String reason);

    void join(GamePlayer player, List<String> args) throws FlagGameException;

    void leave(GamePlayer player);

    default boolean hasReceived(GamePlayer player) {
        return this.getPlayers().contains(player);
    }

    void start(List<String> args) throws FlagGameException;
    
    Optional<? extends Game> getGame(GamePlayer player);
    
    Collection<? extends Game> getGames();

    default Optional<Stage> getStage(GamePlayer player) {
        return this.getGame(player).map(Game::getStage);
    }
    
    default Collection<Stage> getStages() {
        return getGames().stream().map(g -> g.getStage()).collect(Collectors.toSet());
    }

    void stop(String reason) throws IllegalStateException;

    UUID getID();

    GameReception.State getState();

    Collection<GamePlayer> getPlayers();

    default int size() {
        return this.getPlayers().size();
    }

    default Optional<Class<? extends Game>> getGameType() {
        return Optional.ofNullable(this.getClass().getAnnotation(ReceptionFor.class)).map(ReceptionFor::value);
    }

    @Override
    public default Iterator<GamePlayer> iterator() {
        return this.getPlayers().iterator();
    }

    RecordStream getRecordStream();

    public static enum State {
        READY(Game.State.PREPARATION),
        OPENED(Game.State.PREPARATION),
        STARTING(Game.State.PREPARATION),
        STARTED(Game.State.STARTED),
        FINISHED(Game.State.FINISHED),
        CLOSED(Game.State.FINISHED),
        ;

        private final Game.State state;

        private State(Game.State state) {
            this.state = state;
        }

        public Game.State toGameState() {
            return state;
        }

    }

}
