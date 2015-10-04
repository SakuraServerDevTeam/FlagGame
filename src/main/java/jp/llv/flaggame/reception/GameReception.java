/*
 * Copyright (C) 2015 Toyblocks.
 * All rights reserved.
 */
package jp.llv.flaggame.reception;

import java.util.Collection;
import java.util.List;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Game;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public interface GameReception {
    
    void join(GamePlayer player, List<String> args) throws CommandException;
    
    void leave(GamePlayer player);
    
    Collection<? extends GamePlayer> getJoined();
    
    void start(List<String> args) throws CommandException;
    
    Game getGame();
    
    void stop() throws IllegalStateException;
    
    String getID();
    
}
