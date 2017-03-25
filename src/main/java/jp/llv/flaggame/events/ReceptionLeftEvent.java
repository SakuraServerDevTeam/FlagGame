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

import jp.llv.flaggame.reception.GameReception;
import org.bukkit.event.HandlerList;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class ReceptionLeftEvent extends GamePlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    
    private final GameReception reception;

    public ReceptionLeftEvent(GamePlayer player, GameReception reception) {
        super(player);
        this.reception = reception;
    }

    public GameReception getReception() {
        return reception;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
