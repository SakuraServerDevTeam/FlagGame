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
package jp.llv.flaggame.api.session;

import jp.llv.flaggame.api.exception.ReservedException;

/**
 *
 * @author SakuraServerDev
 * @param <T> reservable type
 */
public interface Reservable<T extends Reservable<T>> {

    boolean isReserved();

    default void testReserved() throws ReservedException {
        if (isReserved()) {
            throw new ReservedException();
        }
    }

    Reservation<T> reserve(Reserver reserver) throws ReservedException;

    Reserver getReserver();

    interface Reservation<T extends Reservable<T>> {

        Reservable<T> getReservable();

        Reserver getReserver();

        void release();

        boolean isReleased();

    }

}
