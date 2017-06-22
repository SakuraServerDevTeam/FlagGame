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
package jp.llv.flaggame.stage.rollback;

import jp.llv.flaggame.api.stage.rollback.SerializeTask;
import jp.llv.flaggame.api.exception.RollbackException;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 *
 * @author SakuraServerDev
 */
public class QueuedSerializeTask extends SerializeTask {

    private final LinkedList<SerializeTask> queue = new LinkedList<>();

    public QueuedSerializeTask(Consumer<RollbackException> callback) {
        super(callback);
    }

    @Override
    public void step() throws RollbackException {
        if (queue.isEmpty()) {
            return;
        }
        SerializeTask task = queue.getFirst();
        task.step();
        if (task.isFinished()) {
            queue.remove();
        }
    }

    @Override
    public boolean isFinished() {
        return queue.isEmpty();
    }

    @Override
    public long getEstimatedTickRemaining() {
        return queue.stream().mapToLong(SerializeTask::getEstimatedTickRemaining).sum();
    }

    public void offer(SerializeTask task) {
        queue.offer(task);
    }

}
