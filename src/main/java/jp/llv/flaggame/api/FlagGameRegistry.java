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
package jp.llv.flaggame.api;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.api.reception.Teaming;
import jp.llv.flaggame.reception.TeamType;
import jp.llv.flaggame.api.util.function.ThrowingBiFunction;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.exception.NotRegisteredException;
import jp.llv.flaggame.api.player.SetupReservation;
import jp.llv.flaggame.api.player.SetupSession;
import jp.llv.flaggame.api.session.Reservable;
import jp.llv.flaggame.api.trophy.Trophy;

/**
 *
 * @author toyblocks
 */
public interface FlagGameRegistry {

    void registerReception(String key, ThrowingBiFunction<? super FlagGameAPI, ? super UUID, ? extends Reception, FlagGameException> factory);

    void registerTeaming(String key, ThrowingBiFunction<? super FlagGameAPI, ? super TeamType[], ? extends Teaming, FlagGameException> factory);

    <R extends Reservable<R>, S extends SetupReservation<R>>
            void registerSession(Class<S> type, Function<Reservable.Reservation<R>, S> factory);

    void registerTrophy(String key, Function<String, ? extends Trophy> factory);
            
    Collection<String> getReceptions();

    Collection<String> getTeamings();
    
    Collection<String> getTrophies();

    ThrowingBiFunction<? super FlagGameAPI, ? super UUID, ? extends Reception, FlagGameException> getReception(String key) throws NotRegisteredException;

    default ThrowingBiFunction<? super FlagGameAPI, ? super UUID, ? extends Reception, FlagGameException> getDefaultReception() {
        try {
            return getReception(null);
        } catch (NotRegisteredException ex) {
            return null;
        }
    }

    ThrowingBiFunction<? super FlagGameAPI, ? super TeamType[], ? extends Teaming, FlagGameException> getTeaming(String key) throws NotRegisteredException;

    default ThrowingBiFunction<? super FlagGameAPI, ? super TeamType[], ? extends Teaming, FlagGameException> getDefaultTeaming() {
        try {
            return getTeaming(null);
        } catch (NotRegisteredException ex) {
            return null;
        }
    }

    <R extends Reservable<? super R>, S extends SetupSession<? super R>>
            Function<? super Reservable.Reservation<? super R>, ? extends S>
            getSession(Class<S> type) throws NotRegisteredException;
            
    Function<String, ? extends Trophy> getTrophy(String key) throws NotRegisteredException;

}
