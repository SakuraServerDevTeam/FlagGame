/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
 */
package jp.llv.flaggame.event;

import org.bukkit.event.HandlerList;
import syam.flaggame.game.Team;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class TeamJoinedEvent extends GamePlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Team team;
    
    public TeamJoinedEvent(GamePlayer player, Team team) {
        super(player);
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
