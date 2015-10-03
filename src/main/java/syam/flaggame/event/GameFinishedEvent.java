/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.event;

import java.util.Map;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import syam.flaggame.enums.GameResult;
import syam.flaggame.enums.GameTeam;
import syam.flaggame.game.Stage;
import syam.flaggame.player.FGPlayer;

/**
 * GameFinishedEvent (GameFinishedEvent.java)
 * 
 * @author syam(syamn)
 */
public class GameFinishedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Stage stage;
    private GameResult result;
    private GameTeam winTeam;
    private String reason;
    private Map<GameTeam, Set<FGPlayer>> players;

    /**
     * コンストラクタ
     * 
     * @param stage
     * @param result
     * @param winTeam
     * @param reason
     * @param playersMap
     */
    public GameFinishedEvent(Stage stage, GameResult result, GameTeam winTeam, String reason, Map<GameTeam, Set<FGPlayer>> playersMap) {
        this.stage = stage;
        this.result = result;
        this.winTeam = winTeam;
        this.reason = reason;
        this.players = playersMap;
    }

    public GameFinishedEvent(Stage stage, GameResult result, GameTeam winTeam, Map<GameTeam, Set<FGPlayer>> playersMap) {
        this(stage, result, winTeam, null, playersMap);
    }

    public Stage getStage() {
        return this.stage;
    }

    public GameResult getResult() {
        return this.result;
    }

    public GameTeam getWinTeam() {
        return this.winTeam;
    }

    public String getReason() {
        return this.reason;
    }

    public Map<GameTeam, Set<FGPlayer>> getPlayers() {
        return this.players;
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
