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
package jp.llv.flaggame.reception.teaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.reception.Teaming;
import jp.llv.flaggame.reception.TeamType;
import jp.llv.flaggame.api.exception.InvalidTeamException;

/**
 *
 * @author toyblocks
 */
public class SuccessiveTeaming implements Teaming {

    private final Map<TeamType, Set<GamePlayer>> players = new HashMap<>();

    public SuccessiveTeaming(FlagGameAPI api, TeamType... teams) {
        for (TeamType team : teams) {
            Objects.requireNonNull(team);
            players.put(team, new HashSet<>());
        }
        if (players.size() < 2) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Optional<TeamType> join(GamePlayer player) {
        //人数でチームをマッピング
        Map<Integer, List<TeamType>> m = new HashMap<>();
        for (Map.Entry<TeamType, Set<GamePlayer>> e : this.players.entrySet()) {
            if (!m.containsKey(e.getValue().size())) {
                m.put(e.getValue().size(), new ArrayList<>());
            }
            m.get(e.getValue().size()).add(e.getKey());
        }
        int min = m.keySet().stream().mapToInt(i -> i).min().getAsInt();
        List<TeamType> can = m.get(min);
        TeamType type = can.get((int) (Math.random() * can.size()));
        this.players.get(type).add(player);
        return Optional.of(type);
    }

    @Override
    public void leave(GamePlayer player) {
        players.values().forEach(t -> t.remove(player));
    }

    @Override
    public Collection<GamePlayer> getPlayers() {
        return players.values().stream().flatMap(t -> t.stream()).collect(Collectors.toSet());
    }

    @Override
    public Map<TeamType, ? extends Collection<GamePlayer>> build() throws InvalidTeamException {
        if (players.values().stream().anyMatch(t -> t.isEmpty())) {
            throw new InvalidTeamException();
        }
        return Collections.unmodifiableMap(players);
    }

    @Override
    public int size() {
        return this.players.values().stream().mapToInt(Set::size).sum();
    }

}
