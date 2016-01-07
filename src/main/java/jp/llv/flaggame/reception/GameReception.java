/* 
 * Copyright (C) 2015 Toyblocks, SakuraServerDev
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
import jp.llv.flaggame.game.Game;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public interface GameReception extends Iterable<GamePlayer> {

    void open(List<String> args) throws CommandException;

    void close(String reason);

    void join(GamePlayer player, List<String> args) throws CommandException;

    void leave(GamePlayer player);

    default boolean hasReceived(GamePlayer player) {
        return this.getPlayers().contains(player);
    }

    void start(List<String> args) throws CommandException;

    Optional<Game> getGame();

    default Optional<Stage> getStage() {
        return this.getGame().map(Game::getStage);
    }

    default Optional<Collection<Team>> getTeams() {
        return this.getGame().map(Game::getTeams);
    }

    void stop(String reason) throws IllegalStateException;

    UUID getID();

    String getName();

    GameReception.State getState();

    double getEntryFee();

    double getMaxAward();

    Collection<GamePlayer> getPlayers();

    default Optional<Class<? extends Game>> getGameType() {
        return  Optional.ofNullable(this.getClass().getAnnotation(ReceptionFor.class)).map(ReceptionFor::value);
    }
    
    @Override
    public default Iterator<GamePlayer> iterator() {
        return this.getPlayers().iterator();
    }

    public static enum State {
        READY(Game.State.PREPARATION),
        OPENED(Game.State.PREPARATION),
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
