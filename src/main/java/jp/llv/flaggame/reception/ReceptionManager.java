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

import jp.llv.flaggame.api.reception.ReceptionAPI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.game.Game;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.api.exception.FlagGameException;

/**
 *
 * @author Toyblocks
 */
public class ReceptionManager implements ReceptionAPI {

    private final FlagGameAPI api;

    private final Map<UUID, Reception> receptions = new HashMap<>();

    public ReceptionManager(FlagGameAPI api) {
        this.api = api;
    }

    @Override
    public Collection<Reception> getReceptions() {
        return Collections.unmodifiableCollection(this.receptions.values());
    }

    @Override
    public Collection<Reception> getReceptions(Reception.State state) {
        return this.receptions.values().stream()
                .filter(r -> r.getState() == state).collect(Collectors.toSet());
    }

    @Override
    public Collection<Reception> getReceptions(Game.State state) {
        return this.receptions.values().stream()
                .filter(r -> r.getState().toGameState() == state).collect(Collectors.toSet());
    }

    @Deprecated
    @Override
    public Optional<Reception> getReception(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
        return getReception(uuid);
    }

    @Override
    public Optional<Reception> getReception(UUID uuid) {
        return Optional.ofNullable(this.receptions.get(uuid));
    }

    @Override
    public Reception newReception(String type, UUID id) throws FlagGameException {
        if (this.receptions.containsKey(id)) {
            throw new IllegalStateException("A reception with the id already exists");
        }
        Reception reception = api.getRegistry().getReception(type)
                .apply(api, id);
        this.receptions.put(id, reception);
        return reception;
    }

    @Override
    public Reception newReception(String type) throws FlagGameException {
        UUID id;
        do {
            id = generateNewID();
        } while (this.receptions.containsKey(id));
        return this.newReception(type, id);
    }

    @Override
    public void remove(Reception reception) {
        if (reception.getState() != Reception.State.CLOSED) {
            throw new IllegalStateException("The reception is not closed");
        }
        for (Iterator<Map.Entry<UUID, Reception>> it = this.receptions.entrySet().iterator(); it.hasNext();) {
            if (it.next().getValue() == reception) {
                it.remove();
            }
        }
    }

    @Override
    public void stopAll(String reason) {
        for (Iterator<Map.Entry<UUID, Reception>> it = this.receptions.entrySet().iterator(); it.hasNext();) {
            it.next().getValue().stop(reason);
        }
    }

    @Override
    public void closeAll(String reason) {
        for (Iterator<Map.Entry<UUID, Reception>> it = this.receptions.entrySet().iterator(); it.hasNext();) {
            it.next().getValue().close(reason);
        }
    }

    @Override
    public Iterator<Reception> iterator() {
        return this.receptions.values().iterator();
    }

    private static UUID generateNewID() {
        return UUID.randomUUID();
    }

}
