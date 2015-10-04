/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
 */
package jp.llv.flaggame.event;

import jp.llv.flaggame.reception.GameReception;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Toyblocks
 */
public class ReceptionOpenEvent extends ReceptionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private boolean cancel = false;
    
    public ReceptionOpenEvent(GameReception reception) {
        super(reception);
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean bln) {
        this.cancel = bln;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
