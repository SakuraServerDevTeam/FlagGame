/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
 */
package jp.llv.flaggame.event;

import jp.llv.flaggame.reception.GameReception;
import org.bukkit.event.HandlerList;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class PlayerEntryCancelledEvent extends GamePlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    
    private final GameReception reception;

    public PlayerEntryCancelledEvent(GamePlayer player, GameReception reception) {
        super(player);
        this.reception = reception;
    }

    public GameReception getReception() {
        return reception;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
