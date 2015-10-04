/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
 */
package jp.llv.flaggame.event;

import jp.llv.flaggame.reception.GameReception;
import org.bukkit.event.Event;

/**
 *
 * @author Toyblocks
 */
public abstract class ReceptionEvent extends Event {
    
    private final GameReception reception;

    public ReceptionEvent(GameReception reception) {
        this.reception = reception;
    }

    public GameReception getReception() {
        return reception;
    }
    
}
