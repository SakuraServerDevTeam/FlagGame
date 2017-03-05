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

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import jp.llv.flaggame.reception.TeamColor;

/**
 * GameJoinEvent (GameJoinEvent.java)
 * 
 * @author syam(syamn)
 * @deprecated this won't be called, use {@link jp.llv.flaggame.event.TeamJoinedEvent} instead of.
 */
@Deprecated
public class GameJoinEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;

    private final Player player;
    private double entryFee;

    private TeamColor team = null;

    public GameJoinEvent(Player player, double entryFee) {
        this.player = player;
        this.entryFee = entryFee;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getEntryFee() {
        return this.entryFee;
    }

    public void setEntryFee(double cost) {
        this.entryFee = cost;
    }

    public void setGameTeam(TeamColor team) {
        this.team = team;
    }

    public TeamColor getGameTeam() {
        return this.team;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

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
