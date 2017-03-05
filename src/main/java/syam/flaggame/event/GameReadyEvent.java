/* 
 * Copyright (C) 2017 Syamn, SakuraServerDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package syam.flaggame.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import syam.flaggame.game.Stage;

/**
 * GameReadyEvent (GameReadyEvent.java)
 * 
 * @author syam(syamn)
 * @deprecated this won't be called, use {@link jp.llv.flaggame.event.ReceptionOpenEvent} instead of.
 */
@Deprecated
public class GameReadyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;

    private Stage stage;
    private final CommandSender sender;
    boolean random;

    /**
     * コンストラクタ
     * 
     * @param stage
     * @param sender
     * @param random
     */
    public GameReadyEvent(Stage stage, CommandSender sender, boolean random) {
        this.stage = stage;
        this.sender = sender;
        this.random = random;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public boolean isRandom() {
        return this.random;
    }

    public void setRandom(boolean random) {
        this.random = random;
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
