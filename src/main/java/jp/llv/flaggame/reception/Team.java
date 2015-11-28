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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import jp.llv.flaggame.events.TeamJoinedEvent;
import jp.llv.flaggame.events.TeamLeftEvent;
import jp.llv.flaggame.game.Game;
import org.bukkit.Bukkit;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class Team implements Iterable<GamePlayer> {

    private final GameReception reception;
    private final Set<GamePlayer> players;
    private final TeamColor color;

    public Team(GameReception reception, TeamColor color) {
        if (reception == null || color == null) {
            throw new NullPointerException();
        }
        this.reception = reception;
        this.color = color;
        this.players = Collections.synchronizedSet(new HashSet<>());
    }
    
    public Team(GameReception reception, TeamColor color, Collection<? extends GamePlayer> players) {
        if (reception == null || color == null) {
            throw new NullPointerException();
        }
        this.reception = reception;
        this.color = color;
        this.players = Collections.synchronizedSet(new HashSet<>(players));
        this.players.remove(null);
        for (GamePlayer player : this.players) {
            Bukkit.getServer().getPluginManager().callEvent(new TeamJoinedEvent(player, this));
        }
    }
    
    public Team(GameReception reception, TeamColor color, GamePlayer ... players) {
        this(reception, color, Arrays.asList(players));
    }
    
    public TeamColor getColor() {
        return this.color;
    }
    
    public GameReception getReception() {
        return this.reception;
    }
    
    public void add(GamePlayer player) {
        if (this.reception.getState().toGameState() != Game.State.PREPARATION) {
            throw new IllegalStateException();
        }
        this.players.add(player);
        Bukkit.getServer().getPluginManager().callEvent(new TeamJoinedEvent(player, this));
    }
    
    public void remove(GamePlayer player) {
        if (this.reception.getState().toGameState() != Game.State.PREPARATION) {
            throw new IllegalStateException();
        }
        this.players.remove(player);
        Bukkit.getServer().getPluginManager().callEvent(new TeamLeftEvent(player, this));
    }
    
    public Collection<? extends GamePlayer> getPlayers() {
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
