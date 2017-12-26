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
package jp.llv.flaggame.event;

import java.util.function.Function;
import jp.llv.flaggame.api.event.SubscriptionTarget;

/**
 *
 * @author toyblocks
 */
public final class SyncRegisteredListener<T> extends RegisteredListener<T>{

    private final double priority;

    public SyncRegisteredListener(double priority, EventManager registry, SubscriptionTarget target, Function<? super T, ?> listener) {
        super(registry, target, listener);
        this.priority = priority;
    }

    @Override
    void call(T event) {
        Object result = listener.apply(event);
        if (result != null) {
            registry.raise(result);
        }
    }

    public double getPriority() {
        return priority;
    }
    
}
