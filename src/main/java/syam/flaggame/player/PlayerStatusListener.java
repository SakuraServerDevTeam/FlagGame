/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
 */
package syam.flaggame.player;

import jp.llv.flaggame.event.PlayerEntryCancelledEvent;
import jp.llv.flaggame.event.PlayerEntryEvent;
import jp.llv.flaggame.event.TeamJoinedEvent;
import jp.llv.flaggame.event.TeamLeftEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import syam.flaggame.FlagGame;

/**
 *
 * @author Toyblocks
 */
public class PlayerStatusListener implements Listener {
    
    private final FlagGame plugin;

    public PlayerStatusListener(FlagGame plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerEntryEvent event) {
        event.getPlayer().setEntry(event.getReception());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerEntryCancelledEvent event) {
        event.getPlayer().setEntry(null);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(TeamJoinedEvent event) {
        event.getPlayer().setTeam(event.getTeam());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(TeamLeftEvent event) {
        event.getPlayer().setTeam(null);
    }
    
}
