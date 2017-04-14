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
package syam.flaggame.exception;

import jp.llv.flaggame.api.session.Reservable;

/**
 * An exception thrown when the accessed reservable is used by someone and not able
 * to use.
 *
 * @author Toyblocks
 */
public class ReservedException extends FlagGameException {
    
    private final Reservable reservable;

    public ReservedException(Reservable reservable) {
        this.reservable = reservable;
    }
    
    public ReservedException() {
        this(null);
    }
    
    public Reservable getReservable() {
        return reservable;
    }

}
