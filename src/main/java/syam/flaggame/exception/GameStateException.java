/* 
 * Copyright (c) 2015 SakuraServerDev All rights reserved.
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
