/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
 */
package syam.flaggame.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import jp.llv.flaggame.event.TeamJoinedEvent;
import jp.llv.flaggame.event.TeamLeftEvent;
import org.bukkit.Bukkit;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class Team implements Iterable<GamePlayer> {

    private final Game game;
    private final Set<GamePlayer> players;
    private final TeamColor color;

    public Team(Game game, TeamColor color) {
        if (game == null || color == null) {
            throw new NullPointerException();
        }
        this.game = game;
        this.color = color;
        this.players = Collections.synchronizedSet(new HashSet<>());
    }
    
    public Team(Game game, TeamColor color, Collection<? extends GamePlayer> players) {
        if (game == null || color == null) {
            throw new NullPointerException();
        }
        this.game = game;
        this.color = color;
        this.players = Collections.synchronizedSet(new HashSet<>(players));
        this.players.remove(null);
        for (GamePlayer player : this.players) {
            Bukkit.getServer().getPluginManager().callEvent(new TeamJoinedEvent(player, this));
        }
    }
    
    public Team(Game game, TeamColor color, GamePlayer ... players) {
        this(game, color, Arrays.asList(players));
    }
    
    public TeamColor getColor() {
        return this.color;
    }
    
    public Game getGame() {
        return this.game;
    }
    
    public void add(GamePlayer player) {
        if (this.game.getState() != Game.State.PREPARATION) {
            throw new IllegalStateException();
        }
        this.players.add(player);
        Bukkit.getServer().getPluginManager().callEvent(new TeamJoinedEvent(player, this));
    }
    
    public void remove(GamePlayer player) {
        if (this.game.getState() != Game.State.PREPARATION) {
            throw new IllegalStateException();
        }
        this.players.remove(player);
        Bukkit.getServer().getPluginManager().callEvent(new TeamLeftEvent(player, this));
    }
    
    public Collection<? extends GamePlayer> getPlayers() {
        return Collections.unmodifiableCollection(this.players);
    }
    
    @Override
    public Iterator<GamePlayer> iterator() {
        return this.players.iterator();
    }
    
}
