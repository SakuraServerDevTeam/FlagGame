/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
 */
package jp.llv.flaggame.event;

import jp.llv.flaggame.reception.GameReception;
import org.bukkit.event.HandlerList;
import syam.flaggame.game.Stage;

/**
 *
 * @author Toyblocks
 */
public class StageReleasedEvent extends ReceptionEvent {

    private static final HandlerList handlers = new HandlerList();
    
    private final Stage stage;
    
    public StageReleasedEvent(GameReception reception, Stage stage) {
        super(reception);
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
