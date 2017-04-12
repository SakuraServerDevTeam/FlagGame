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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import jp.llv.flaggame.game.Game;
import syam.flaggame.FlagGame;

/**
 *
 * @author Toyblocks
 */
public class ReceptionManager implements Iterable<GameReception> {

    private final FlagGame plugin;

    private final Map<UUID, GameReception> receptions = new HashMap<>();

    public ReceptionManager(FlagGame plugin) {
        this.plugin = plugin;
    }

    public Collection<GameReception> getReceptions() {
        return Collections.unmodifiableCollection(this.receptions.values());
    }

    public Collection<GameReception> getReceptions(GameReception.State state) {
        return this.receptions.values().stream()
                .filter(r -> r.getState() == state).collect(Collectors.toSet());
    }

    public Collection<GameReception> getReceptions(Game.State state) {
        return this.receptions.values().stream()
                .filter(r -> r.getState().toGameState() == state).collect(Collectors.toSet());
    }

    @Deprecated
    public Optional<GameReception> getReception(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
        return getReception(uuid);
    }

    public Optional<GameReception> getReception(UUID uuid) {
        return Optional.ofNullable(this.receptions.get(uuid));
    }

    public <R extends GameReception> R newReception(ReceptionType<? extends R> type, UUID id, List<String> args) {
        if (this.receptions.containsKey(id)) {
            throw new IllegalStateException("A reception with the id already exists");
        }
        R reception = type.newInstance(plugin, id, args);
        this.receptions.put(id, reception);
        return reception;
    }

    public <R extends GameReception> R newReception(ReceptionType<? extends R> type, List<String> args) {
        UUID id;
        do {
            id = generateNewID();
        } while (this.receptions.containsKey(id));
        return this.newReception(type, id, args);
    }

    /*package*/ void remove(GameReception reception) {
        for (Iterator<Map.Entry<UUID, GameReception>> it = this.receptions.entrySet().iterator(); it.hasNext();) {
            if (it.next().getValue() == reception) {
                it.remove();
            }
        }
    }

    public void stopAll(String reason) {
        for (Iterator<Map.Entry<UUID, GameReception>> it = this.receptions.entrySet().iterator(); it.hasNext();) {
            it.next().getValue().stop(reason);
        }
    }

    public void closeAll(String reason) {
        for (Iterator<Map.Entry<UUID, GameReception>> it = this.receptions.entrySet().iterator(); it.hasNext();) {
            it.next().getValue().close(reason);
        }
    }

    @Override
    public Iterator<GameReception> iterator() {
        return this.receptions.values().iterator();
    }

    private static UUID generateNewID() {
        return UUID.randomUUID();
    }

}
