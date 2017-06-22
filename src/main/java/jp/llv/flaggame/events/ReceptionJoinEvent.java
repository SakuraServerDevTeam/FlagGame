/* 
 * Copyright (C) 2017 SakuraServerDev
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

import jp.llv.flaggame.api.player.GamePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import jp.llv.flaggame.api.reception.Reception;

/**
 *
 * @author Toyblocks
 */
public class ReceptionJoinEvent extends GamePlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Reception reception;
    private boolean cancel = false;

    public ReceptionJoinEvent(GamePlayer player, Reception reception) {
        super(player);
        this.reception = reception;
    }

    public Reception getReception() {
        return reception;
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
