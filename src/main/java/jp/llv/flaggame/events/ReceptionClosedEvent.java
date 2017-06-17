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

import org.bukkit.event.HandlerList;
import jp.llv.flaggame.api.reception.Reception;

/**
 *
 * @author Toyblocks
 */
public class ReceptionClosedEvent extends ReceptionEvent {
    
    private static final HandlerList handlers = new HandlerList();

    private final String cause;

    public ReceptionClosedEvent(String cause, Reception reception) {
        super(reception);
        this.cause = cause;
    }

    public String getCause() {
        return cause;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
