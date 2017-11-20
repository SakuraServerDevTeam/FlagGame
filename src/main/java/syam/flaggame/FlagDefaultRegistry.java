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
package syam.flaggame;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.api.reception.Teaming;
import jp.llv.flaggame.reception.BasicGameReception;
import jp.llv.flaggame.reception.TeamType;
import jp.llv.flaggame.reception.teaming.SuccessiveTeaming;
import jp.llv.flaggame.reception.teaming.VibeBasedTeaming;
import jp.llv.flaggame.api.util.function.ThrowingBiFunction;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.FlagGameRegistry;
import jp.llv.flaggame.api.exception.NotRegisteredException;
import jp.llv.flaggame.api.session.Reservable;
import jp.llv.flaggame.api.player.SetupReservation;
import jp.llv.flaggame.api.player.SetupSession;
import jp.llv.flaggame.api.player.StageSetupSession;
import jp.llv.flaggame.api.player.TrophieSetupSession;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.trophie.Trophie;
import jp.llv.flaggame.trophie.ImpossibleTrophie;
import jp.llv.flaggame.trophie.ProfileTrophie;
import jp.llv.flaggame.trophie.RecordTrophie;
import jp.llv.flaggame.trophie.StreamTrophie;
import syam.flaggame.player.StageSetupReservation;
import syam.flaggame.player.TrophieSetupReservation;

/**
 *
 * @author toyblocks
 */
public final class FlagDefaultRegistry implements FlagGameRegistry {

    private final Map<String, ThrowingBiFunction<? super FlagGameAPI, ? super UUID, ? extends Reception, FlagGameException>> receptions = new HashMap<>();

    {
        receptions.put(null, BasicGameReception::new);
    }

    private final Map<String, ThrowingBiFunction<? super FlagGameAPI, ? super TeamType[], ? extends Teaming, FlagGameException>> teamings = new HashMap<>();

    {
        teamings.put(null, VibeBasedTeaming::new);
        teamings.put("successive", SuccessiveTeaming::new);
    }

    private final Map<Class<? extends SetupSession<?>>, Function<? extends Reservable.Reservation<?>, ? extends SetupReservation<?>>> sessions = new HashMap<>();

    {
        sessions.put(StageSetupSession.class, (Function<Reservable.Reservation<Stage>, StageSetupReservation>) StageSetupReservation::new);
        sessions.put(TrophieSetupSession.class, (Function<Reservable.Reservation<Trophie>, TrophieSetupReservation>) TrophieSetupReservation::new);
    }
    
    private final Map<String, Function<String, ? extends Trophie>> trophies = new HashMap<>();
    
    {
        trophies.put(ImpossibleTrophie.TYPE_NAME, ImpossibleTrophie::new);
        trophies.put(RecordTrophie.TYPE_NAME, RecordTrophie::new);
        trophies.put(StreamTrophie.TYPE_NAME, StreamTrophie::new);
        trophies.put(ProfileTrophie.TYPE_NAME, ProfileTrophie::new);
    }

    /*package*/ FlagDefaultRegistry() {
    }

    @Override
    public void registerReception(String key, ThrowingBiFunction<? super FlagGameAPI, ? super UUID, ? extends Reception, FlagGameException> factory) {
        if (receptions.containsKey(Objects.requireNonNull(key))) {
            throw new IllegalStateException("key duplication");
        }
        receptions.put(key, Objects.requireNonNull(factory));
    }

    @Override
    public void registerTeaming(String key, ThrowingBiFunction<? super FlagGameAPI, ? super TeamType[], ? extends Teaming, FlagGameException> factory) {
        if (teamings.containsKey(Objects.requireNonNull(key))) {
            throw new IllegalStateException("key duplication");
        }
        teamings.put(key, Objects.requireNonNull(factory));
    }

    @Override
    public <R extends Reservable<R>, S extends SetupReservation<R>>
            void registerSession(Class<S> type, Function<Reservable.Reservation<R>, S> factory) {
        if (sessions.containsKey(Objects.requireNonNull(type))) {
            throw new IllegalStateException("key duplication");
        }
        sessions.put(type, Objects.requireNonNull(factory));
    }

    @Override
    public void registerTrophie(String key, Function<String, ? extends Trophie> factory) {
        if (trophies.containsKey(Objects.requireNonNull(key))) {
            throw new IllegalStateException("key duplication");
        }
        trophies.put(key, Objects.requireNonNull(factory));
    }

    @Override
    public Collection<String> getReceptions() {
        return Collections.unmodifiableSet(receptions.keySet());
    }

    @Override
    public Collection<String> getTeamings() {
        return Collections.unmodifiableSet(teamings.keySet());
    }
    
    @Override
    public Collection<String> getTrophies() {
        return Collections.unmodifiableSet(trophies.keySet());
    }

    @Override
    public ThrowingBiFunction<? super FlagGameAPI, ? super UUID, ? extends Reception, FlagGameException> getReception(String key) throws NotRegisteredException {
        if (receptions.containsKey(key)) {
            return receptions.get(key);
        } else {
            throw new NotRegisteredException();
        }
    }

    @Override
    public ThrowingBiFunction<? super FlagGameAPI, ? super TeamType[], ? extends Teaming, FlagGameException> getTeaming(String key) throws NotRegisteredException {
        if (teamings.containsKey(key)) {
            return teamings.get(key);
        } else {
            throw new NotRegisteredException();
        }
    }

    @Override
    public <R extends Reservable<? super R>, S extends SetupSession<? super R>>
            Function<? super Reservable.Reservation<? super R>, ? extends S>
            getSession(Class<S> type) throws NotRegisteredException {
        for (Class<? extends SetupSession<?>> key : sessions.keySet()) {
            if (type.isAssignableFrom(key)) {
                return (Function<? super Reservable.Reservation<? super R>, ? extends S>) sessions.get(key);
            }
        }
        throw new NotRegisteredException();
    }

    @Override
    public Function<String, ? extends Trophie> getTrophie(String key) throws NotRegisteredException {
        if (trophies.containsKey(key)) {
            return trophies.get(key);
        } else {
            throw new NotRegisteredException();
        }
    }

}
