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
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.api.reception.Teaming;
import jp.llv.flaggame.reception.BasicGameReception;
import jp.llv.flaggame.reception.TeamType;
import jp.llv.flaggame.reception.teaming.SuccessiveTeaming;
import jp.llv.flaggame.reception.teaming.VibeBasedTeaming;
import jp.llv.flaggame.util.function.ThrowingBiFunction;
import syam.flaggame.exception.FlagGameException;
import jp.llv.flaggame.api.FlagGameRegistry;
import syam.flaggame.exception.NotRegisteredException;

/**
 *
 * @author toyblocks
 */
public class FlagDefaultRegistry implements FlagGameRegistry {

    private final Map<String, ThrowingBiFunction<? super FlagGameAPI, ? super UUID, ? extends Reception, FlagGameException>> receptions = new HashMap<>();
    {
        receptions.put(null, BasicGameReception::new);
    }
    
    private final Map<String, ThrowingBiFunction<? super FlagGameAPI, ? super TeamType[], ? extends Teaming, FlagGameException>> teamings = new HashMap<>();
    {
        teamings.put(null, VibeBasedTeaming::new);
        teamings.put("successive", SuccessiveTeaming::new);
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
    public Collection<String> getReceptions() {
        return Collections.unmodifiableSet(receptions.keySet());
    }

    @Override
    public Collection<String> getTeamings() {
        return Collections.unmodifiableSet(teamings.keySet());
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
    
}
