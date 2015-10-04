/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
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
