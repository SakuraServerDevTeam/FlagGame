/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
 */
package jp.llv.flaggame.reception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Game;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class RealtimeTeamingReception implements GameReception {

    private final Map<TeamColor, Set<GamePlayer>> players = new EnumMap<>(TeamColor.class);
    private final Stage stage;

    public RealtimeTeamingReception(Stage stage) {
        if (stage.isUsing()) {
            throw new IllegalStateException();
        }
        this.stage = stage;
    }

    @Override
    public void join(GamePlayer player, List<String> args) throws CommandException {
        synchronized (player) {
            if (player.getEntry() != null) {
                throw new CommandException("");
            }
        }
    }

    @Override
    public void leave(GamePlayer player) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<? extends GamePlayer> getJoined() {
        Set<GamePlayer> result = new HashSet<>();
        this.players.values().stream().forEach(result::addAll);
        return Collections.unmodifiableSet(result);
    }

    @Override
    public void start(List<String> args) throws CommandException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getID() {

    }

    @Override
    public Game getGame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
