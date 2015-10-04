/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
 */
package jp.llv.flaggame.event;

import org.bukkit.event.Event;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public abstract class GamePlayerEvent extends Event {
    
    private final GamePlayer player;

    public GamePlayerEvent(GamePlayer player) {
        this.player = player;
    }

    public GamePlayer getPlayer() {
        return player;
    }
    
}
