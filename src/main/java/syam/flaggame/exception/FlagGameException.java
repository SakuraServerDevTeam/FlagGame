/* 
 * Copyright (c) 2015 SakuraServerDev All rights reserved.
 */
package syam.flaggame.exception;

/**
 * FlagGameException (FlagGameException.java)
 * 
 * @author syam(syamn)
 */
public class FlagGameException extends RuntimeException {
    private static final long serialVersionUID = -5759263894003798032L;

    public FlagGameException() {
    }

    public FlagGameException(String message) {
        super(message);
    }

    public FlagGameException(Throwable cause) {
        super(cause);
    }

    public FlagGameException(String message, Throwable cause) {
        super(message, cause);
    }
}
