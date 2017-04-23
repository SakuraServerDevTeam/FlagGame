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
package jp.llv.flaggame.reception.fest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import jp.llv.flaggame.api.session.SimpleReservable;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.util.MapUtils;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalSchedule extends SimpleReservable<FestivalSchedule> {
    
    public static final Pattern NAME_REGEX = Pattern.compile("^[a-z0-9](?:[a-z0-9_-]*[a-z0-9])?$");

    private final String name;
    private final Map<TeamColor, String> teams = new HashMap<>();
    private final List<Map<String, FestivalMatch>> matches = new ArrayList<>();
    private double entryFee;
    private double prize;

    public FestivalSchedule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<TeamColor, String> getTeams() {
        return Collections.unmodifiableMap(teams);
    }

    public String getTeam(TeamColor color) {
        return teams.get(color);
    }
    
    public TeamColor getTeam(String team) {
        Iterator<TeamColor> it = MapUtils.getKeyByValue(teams, team).iterator();
        return it.hasNext() ? it.next() : null;
    }

    public void setTeam(TeamColor color, String team) {
        Objects.requireNonNull(color);
        Objects.requireNonNull(team);
        MapUtils.removeValue(teams, team);
        teams.put(color, team);
    }
    
    public void removeTeam(String team) {
        MapUtils.removeValue(teams, team);
    }

    public void setTeams(Map<TeamColor, String> teams) {
        for (Map.Entry<TeamColor, String> entry : teams.entrySet()) {
            setTeam(entry.getKey(), entry.getValue());
        }
    }

    public List<Map<String, FestivalMatch>> getMatches() {
        List<Map<String, FestivalMatch>> result = new ArrayList<>();
        for (Map<String,FestivalMatch> round : matches) {
            result.add(Collections.unmodifiableMap(round));
        }
        return result;
    }
    
    public Map<String, FestivalMatch> getRound(int index) {
        return Collections.unmodifiableMap(matches.get(index));
    }
    
    public FestivalMatch getMatch(int index, String stage) {
        return matches.get(index).get(stage);
    }
    
    public void addMatch(int index, FestivalMatch match) {
        matches.get(index).put(match.getStage(), match);
    }
    
    public void removeMatch(int index, String stage) {
        matches.get(index).remove(stage);
    }
    
    public void addRound(int index) {
        matches.add(index, new HashMap<>());
    }
    
    public void addRound(int index, Collection<FestivalMatch> round) {
        addRound(index);
        for (FestivalMatch match : round) {
            addMatch(index, match);
        }
    }
    
    public void addRound(Collection<FestivalMatch> round) {
        addRound(matches.size(), round);
    }
    
    public void removeRound(int index) {
        matches.remove(index);
    }
    
    public void setMatches(Collection<? extends Collection<FestivalMatch>> matches) {
        matches.clear();
        for (Collection<FestivalMatch> round : matches) {
            addRound(round);
        }
    }

    public double getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(double entryFee) {
        this.entryFee = entryFee;
    }

    public double getPrize() {
        return prize;
    }

    public void setPrize(double prize) {
        this.prize = prize;
    }

}
