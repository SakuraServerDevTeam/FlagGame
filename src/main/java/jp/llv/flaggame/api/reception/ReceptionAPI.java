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
import java.util.Optional;
import java.util.UUID;
import jp.llv.flaggame.game.Game;
import syam.flaggame.exception.FlagGameException;

/**
 *
 * @author toyblocks
 */
public interface ReceptionAPI extends Iterable<Reception> {

    void closeAll(String reason);

    @Deprecated
    Optional<Reception> getReception(String id);

    Optional<Reception> getReception(UUID uuid);

    Collection<Reception> getReceptions();

    Collection<Reception> getReceptions(Reception.State state);

    Collection<Reception> getReceptions(Game.State state);

    Reception newReception(String type, UUID id) throws FlagGameException;

    Reception newReception(String type) throws FlagGameException;
    
    default Reception newReception() throws FlagGameException {
        return newReception(null);
    }

    void remove(Reception reception);
    
    void stopAll(String reason);
    
}
