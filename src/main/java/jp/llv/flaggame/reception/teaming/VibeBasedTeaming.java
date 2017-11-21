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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.reception.Teaming;
import jp.llv.flaggame.api.reception.TeamType;
import jp.llv.flaggame.util.ValueSortedMap;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.exception.InvalidTeamException;

/**
 *
 * @author toyblocks
 */
public class VibeBasedTeaming implements Teaming {

    private final FlagGameAPI api;
    private final Set<TeamType> teamTypes;
    private final Set<GamePlayer> players = new HashSet<>();

    public VibeBasedTeaming(FlagGameAPI api, TeamType... teams) {
        this.api = api;
        this.teamTypes = new HashSet<>(Arrays.asList(teams));
    }

    @Override
    public Optional<TeamType> join(GamePlayer player) throws FlagGameException {
        players.add(player);
        return Optional.empty();
    }

    @Override
    public void leave(GamePlayer player) {
        players.remove(player);
    }

    @Override
    public Collection<GamePlayer> getPlayers() {
        return Collections.unmodifiableCollection(players);
    }

    @Override
    public Map<TeamType, ? extends Collection<GamePlayer>> build() throws InvalidTeamException {
        //-stage1; get all players' vibe
        ValueSortedMap<GamePlayer, Double> vibes = ValueSortedMap.newInstance(
                (v1, v2) -> v1.equals(v2) ? 1 : -v1.compareTo(v2)
        );
        for (GamePlayer player : players) {
            vibes.put(player, api.getProfiles().getProfile(player.getUUID()).getVibe().orElse(0.0));
        }

        //-stage2; prepare collections
        List<TeamType> types = new ArrayList<>(teamTypes);
        if (players.size() < types.size()) {
            throw new InvalidTeamException();
        }
        // without this, the strongest player is always in the fixed team
        Collections.shuffle(types);
        Map<TeamType, List<GamePlayer>> teams = new HashMap<>();
        ValueSortedMap<TeamType, Double> teamVibes = ValueSortedMap.newInstance(
                (v1, v2) -> v1.equals(v2) ? 1 : v1.compareTo(v2)
        );
        for (TeamType type : types) {
            teams.put(type, new ArrayList<>(players.size() / types.size() + 1));
            teamVibes.put(type, 0.0);
        }

        //-stage3; teaming most players
        Iterator<Map.Entry<GamePlayer, Double>> it = vibes.entrySet().iterator();
        int count = players.size();
        int remainder = count % types.size();
        int teamIndex = 0;
        while (it.hasNext() && count-- > remainder) {
            Map.Entry<GamePlayer, Double> entry = it.next();
            TeamType teamColor = types.get(teamIndex++ % types.size());
            teams.get(teamColor).add(entry.getKey());
            teamVibes.put(teamColor, teamVibes.get(teamColor) + entry.getValue());
        }

        //-stage4; teaming the others
        for (Map.Entry<TeamType, Double> teamVibe : teamVibes.entrySet()) {
            if (!it.hasNext()) {
                break;
            }
            teams.get(teamVibe.getKey()).add(it.next().getKey());
        }

        //-stage5; build teams
        for (Map.Entry<TeamType, List<GamePlayer>> entry : teams.entrySet()) {
            Collections.shuffle(entry.getValue());
        }
        return teams;
    }

    @Override
    public int size() {
        return players.size();
    }

}
