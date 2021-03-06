/*
 * Copyright (C) 2017 toyblocks
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
package jp.llv.flaggame.api.player;

import jp.llv.flaggame.api.session.Reservable;

/**
 *
 * @author toyblocks
 * @param <R> A type of reservable to set up
 */
public abstract class SetupReservation<R extends Reservable<R>> implements SetupSession<R> {
    
    private final Reservable.Reservation<R> reservation;

    public SetupReservation(Reservable.Reservation<R> reservation) {
        this.reservation = reservation;
    }

    @Override
    public R getReserved() {
        return reservation.getReservable();
    }

    public Reservable.Reservation<R> getReservation() {
        return reservation;
    }
    
}
