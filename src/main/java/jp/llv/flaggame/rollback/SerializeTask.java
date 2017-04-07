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
package jp.llv.flaggame.rollback;

import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author SakuraServerDev
 */
public abstract class SerializeTask extends BukkitRunnable {

    private final Consumer<RollbackException> callback;

    public SerializeTask(Consumer<RollbackException> callback) {
        this.callback = callback;
    }

    public final void start(Plugin plugin) {
        start(plugin, 0L);
    }

    public final void start(Plugin plugin, long delay) {
        if (isFinished()) {
            callback.accept(null);
        }
    }

    @Override
    public final void run() {
        try {
            step();
        } catch (RollbackException ex) {
            callback.accept(ex);
            super.cancel();
        }
        if (isFinished()) {
            callback.accept(null);
            super.cancel();
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        if (!isFinished()) {
            this.callback.accept(new RollbackException("Task has been cancelled"));
        }
        try {
            super.cancel();
        } catch (IllegalStateException ex) {
            // already cancelled
        }
    }

    public abstract void step() throws RollbackException;

    public abstract boolean isFinished();

    public abstract long getEstimatedTickRemaining();

    public static class CompletedSerializeTask extends SerializeTask {

        public CompletedSerializeTask(Consumer<RollbackException> callback) {
            super(callback);
        }

        @Override
        public void step() {
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public long getEstimatedTickRemaining() {
            return 0;
        }

    }

    public static class FailedSerializeTask extends SerializeTask {

        private final RollbackException ex;

        public FailedSerializeTask(Consumer<RollbackException> callback, RollbackException ex) {
            super(callback);
            this.ex = ex;
        }

        @Override
        public void step() throws RollbackException {
            throw ex;
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public long getEstimatedTickRemaining() {
            return 0;
        }

    }

}
