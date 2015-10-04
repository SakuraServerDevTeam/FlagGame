/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import syam.flaggame.enums.TeamColor;

/**
 * GameJoinEvent (GameJoinEvent.java)
 * 
 * @author syam(syamn)
 * @deprecated this won't be called, use {@link jp.llv.flaggame.event.TeamJoinedEvent} instead of.
 */
@Deprecated
public class GameJoinEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;

    private final Player player;
    private double entryFee;

    private TeamColor team = null;

    public GameJoinEvent(Player player, double entryFee) {
        this.player = player;
        this.entryFee = entryFee;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getEntryFee() {
        return this.entryFee;
    }

    public void setEntryFee(double cost) {
        this.entryFee = cost;
    }

    public void setGameTeam(TeamColor team) {
        this.team = team;
    }

    public TeamColor getGameTeam() {
        return this.team;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    /* ******************** */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
