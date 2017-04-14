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

import syam.flaggame.exception.ReservedException;

/**
 *
 * @author SakuraServerDev
 * @param <T> reservation target
 */
public class SimpleReservable<T extends Reservable<T>> implements Reservable<T> {

    private SimpleReservation reservation;

    @Override
    public final Reservation reserve(Reserver reserver) throws ReservedException {
        if (reservation != null) {
            throw new ReservedException(this);
        }
        reservation = new SimpleReservation(reserver);
        return reservation;
    }

    @Override
    public Reserver getReserver() {
        if (reservation == null) {
            throw new IllegalStateException();
        }
        return reservation.getReserver();
    }

    @Override
    public final boolean isReserved() {
        return reservation != null;
    }

    @Override
    public void testReserved() throws ReservedException {
        if (reservation != null) {
            throw new ReservedException(this);
        }
    }

    private void release() {
        if (reservation == null) {
            throw new IllegalStateException();
        }
        reservation = null;
    }

    private class SimpleReservation implements Reservation<T> {

        private final Reserver reserver;
        private boolean released = false;

        public SimpleReservation(Reserver reserver) {
            this.reserver = reserver;
        }

        @Override
        public Reservable<T> getReservable() {
            return SimpleReservable.this;
        }

        @Override
        public Reserver getReserver() {
            return reserver;
        }

        @Override
        public void release() {
            if (released) {
                throw new IllegalStateException();
            }
            released = true;
            SimpleReservable.this.release();
        }

        @Override
        public boolean isReleased() {
            return released;
        }

    }

}
