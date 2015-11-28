/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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

/**
 * GameStateException (GameStateException.java)
 * 
 * @author syam(syamn)
 */
public class GameStateException extends FlagGameException {
    private static final long serialVersionUID = -3385340476319991882L;

    public GameStateException(String message) {
        super(message);
    }

    public GameStateException(Throwable cause) {
        super(cause);
    }

    public GameStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
