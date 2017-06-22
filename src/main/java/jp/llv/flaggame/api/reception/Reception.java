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
package jp.llv.flaggame.api.reception;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.game.Game;
import jp.llv.flaggame.profile.RecordStream;
import jp.llv.flaggame.api.session.Reserver;
import jp.llv.flaggame.util.OptionSet;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.stage.Stage;

/**
 *
 * @author Toyblocks
 */
public interface Reception extends Reserver, Iterable<GamePlayer> {

    void open(OptionSet option) throws FlagGameException;

    void close(String reason);

    void join(GamePlayer player, OptionSet options) throws FlagGameException;

    void leave(GamePlayer player);

    default boolean hasReceived(GamePlayer player) {
        return this.getPlayers().contains(player);
    }

    void start(OptionSet options) throws FlagGameException;

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

    Reception.State getState();

    Collection<GamePlayer> getPlayers();

    default int size() {
        return this.getPlayers().size();
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
        CLOSED(Game.State.FINISHED),;

        private final Game.State state;

        private State(Game.State state) {
            this.state = state;
        }

        public Game.State toGameState() {
            return state;
        }

    }

}
