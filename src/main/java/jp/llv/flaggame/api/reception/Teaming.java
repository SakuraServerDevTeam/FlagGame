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
package jp.llv.flaggame.api.reception;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.exception.InvalidTeamException;

/**
 *
 * @author toyblocks
 */
public interface Teaming {

    Optional<TeamType> join(GamePlayer player) throws FlagGameException;

    void leave(GamePlayer player);

    Collection<GamePlayer> getPlayers();

    Map<TeamType, ? extends Collection<GamePlayer>> build() throws InvalidTeamException;

    default int size() {
        return getPlayers().size();
    }

}
