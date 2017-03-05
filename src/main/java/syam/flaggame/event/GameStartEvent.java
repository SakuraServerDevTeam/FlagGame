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

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;

/**
 * GameStartEvent (GameStartEvent.java)
 * 
 * @author syam(syamn)
 * @deprecated use {@link jp.llv.flaggame.event.}
 */
@Deprecated
public class GameStartEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;

    private final Stage stage;
    private final CommandSender sender;
    private final Set<GamePlayer> redTeam;
    private final Set<GamePlayer> blueTeam;

    /**
     * コンストラクタ
     * 
     * @param stage
     * @param sender
     * @param redTeam
     * @param blueTeam
     */
    public GameStartEvent(Stage stage, CommandSender sender, Set<GamePlayer> redTeam, Set<GamePlayer> blueTeam) {
        this.stage = stage;
        this.sender = sender;

        this.redTeam = redTeam;
        this.blueTeam = blueTeam;
    }

    public Stage getStage() {
        return this.stage;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public Set<GamePlayer> getRedTeam() {
        return this.redTeam;
    }

    public Set<GamePlayer> getBlueTeam() {
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
