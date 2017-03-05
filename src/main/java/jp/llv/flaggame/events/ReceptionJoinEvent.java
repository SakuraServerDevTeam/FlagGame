/* 
 * Copyright (C) 2017 Toyblocks, SakuraServerDev
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
package jp.llv.flaggame.events;

import jp.llv.flaggame.reception.GameReception;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class ReceptionJoinEvent extends GamePlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final GameReception reception;
    private boolean cancel = false;
    
    private double entryFee;

    public ReceptionJoinEvent(GamePlayer player, GameReception reception, double entryFee) {
        super(player);
        this.reception = reception;
        this.entryFee = entryFee;
    }

    public GameReception getReception() {
        return reception;
    }

    public double getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(double entryFee) {
        this.entryFee = entryFee >= 0 ? entryFee : 0;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean bln) {
        this.cancel = bln;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
