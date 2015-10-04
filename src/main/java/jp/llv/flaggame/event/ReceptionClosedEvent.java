/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
 */
package jp.llv.flaggame.event;

import jp.llv.flaggame.reception.GameReception;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Toyblocks
 */
public class ReceptionClosedEvent extends ReceptionEvent {
    
    private static final HandlerList handlers = new HandlerList();

    private final Cause cause;

    public ReceptionClosedEvent(Cause cause, GameReception reception) {
        super(reception);
        this.cause = cause;
    }

    public Cause getCause() {
        return cause;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public enum Cause {
        CANCELLED, FAILED, SUSPENDED, FINISHED;
    }
    
}
