/* 
 * Copyright (C) 2015 Toyblocks, SakuraServerDev
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
@FunctionalInterface
public interface TeamOrganizer {

    Map<TeamColor, ? extends Collection<GamePlayer>> teaming(TeamColor[] teams, GamePlayer[] players);

    public static final TeamOrganizer RANDOM = (teams, players) -> {
        Map<TeamColor, Set<GamePlayer>> result = new HashMap<>();
        for (TeamColor c : teams) {
            result.put(c, new HashSet<>());
        }
        for (int i = 0; i < players.length; i++) {
            result.get(teams[i % teams.length]).add(players[i]);
        }
        return result;
    };
    
    public static final TeamOrganizer AVERAGING = (teams, players) -> {
        Arrays.sort(players, (p1, p2) -> {
            return Double.compare(p1.getProfile().getKD(), p2.getProfile().getKD());
        });
        return RANDOM.teaming(teams, players);
    };

}