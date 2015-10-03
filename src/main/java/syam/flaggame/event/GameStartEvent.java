/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.event;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import syam.flaggame.game.Stage;
import syam.flaggame.player.FGPlayer;

/**
 * GameStartEvent (GameStartEvent.java)
 * 
 * @author syam(syamn)
 */
public class GameStartEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;

    private final Stage stage;
    private final boolean random;
    private final CommandSender sender;
    private final Set<FGPlayer> redTeam;
    private final Set<FGPlayer> blueTeam;

    /**
     * コンストラクタ
     * 
     * @param stage
     * @param random
     * @param sender
     * @param redTeam
     * @param blueTeam
     */
    public GameStartEvent(Stage stage, boolean random, CommandSender sender, Set<FGPlayer> redTeam, Set<FGPlayer> blueTeam) {
        this.stage = stage;
        this.random = random;
        this.sender = sender;

        this.redTeam = redTeam;
        this.blueTeam = blueTeam;
    }

    public Stage getStage() {
        return this.stage;
    }

    public boolean isRandom() {
        return this.random;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public Set<FGPlayer> getRedTeam() {
        return this.redTeam;
    }

    public Set<FGPlayer> getBlueTeam() {
        return this.blueTeam;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
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
