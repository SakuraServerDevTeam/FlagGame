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
package jp.llv.flaggame.api.reception;

import jp.llv.flaggame.api.reception.TeamColor;
import jp.llv.flaggame.api.reception.TeamType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import jp.llv.flaggame.events.TeamJoinedEvent;
import jp.llv.flaggame.events.TeamLeftEvent;
import jp.llv.flaggame.api.game.Game;
import org.bukkit.Bukkit;
import jp.llv.flaggame.api.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public final class Team implements Iterable<GamePlayer> {

    private final Reception reception;
    private final Set<GamePlayer> players;
    private final TeamType type;

    public Team(Reception reception, TeamType type) {
        if (reception == null || type == null) {
            throw new NullPointerException();
        }
        this.reception = reception;
        this.type = type;
        this.players = Collections.synchronizedSet(new HashSet<>());
    }

    public Team(Reception reception, TeamType type, Collection<GamePlayer> players) {
        if (reception == null || type == null) {
            throw new NullPointerException();
        }
        this.reception = reception;
        this.type = type;
        this.players = Collections.synchronizedSet(new HashSet<>(players));
        this.players.remove(null);
        for (GamePlayer player : this.players) {
            Bukkit.getServer().getPluginManager().callEvent(new TeamJoinedEvent(player, this));
        }
    }

    public Team(Reception reception, TeamType type, GamePlayer... players) {
        this(reception, type, Arrays.asList(players));
    }

    public TeamType getType() {
        return this.type;
    }

    public TeamColor getColor() {
        return getType().toColor();
    }

    public Reception getReception() {
        return this.reception;
    }

    public void add(GamePlayer player) {
        if (this.reception.getState().toGameState() != Game.State.INITIAL) {
            throw new IllegalStateException();
        }
        this.players.add(player);
        Bukkit.getServer().getPluginManager().callEvent(new TeamJoinedEvent(player, this));
    }

    public void remove(GamePlayer player) {
        if (this.reception.getState().toGameState() != Game.State.INITIAL) {
            throw new IllegalStateException();
        }
        this.players.remove(player);
        Bukkit.getServer().getPluginManager().callEvent(new TeamLeftEvent(player, this));
    }

    public Collection<GamePlayer> getPlayers() {
        return Collections.unmodifiableCollection(this.players);
    }

    public boolean hasJoined(GamePlayer player) {
        return this.getPlayers().contains(player);
    }

    @Override
    public Iterator<GamePlayer> iterator() {
        return this.players.iterator();
    }

}
