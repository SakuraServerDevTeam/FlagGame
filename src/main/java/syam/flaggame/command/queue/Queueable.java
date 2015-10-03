/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.command.queue;

import java.util.List;

/**
 * Queueable (Queueable.java)
 *
 * @author syam(syamn)
 */
public interface Queueable {

    void executeQueue(List<String> args);
}
