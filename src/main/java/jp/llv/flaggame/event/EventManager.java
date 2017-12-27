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

import jp.llv.flaggame.api.event.EventAPI;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.event.SubscriptionTarget;

/**
 *
 * @author toyblocks
 */
public class EventManager implements EventAPI {

    private final long thread;
    private final FlagGameAPI api;
    private final EventManager parent;
    private final Set<EventManager> children = new HashSet<>();
    private final Multiset<RegisteredListener<?>> listeners = TreeMultiset.create(
            Comparator.comparing(listener -> {
                if (listener instanceof SyncRegisteredListener) {
                    return ((SyncRegisteredListener) listener).getPriority();
                } else {
                    return Double.MAX_VALUE;
                }
            }, Comparator.reverseOrder())
    );

    public EventManager(FlagGameAPI api, EventManager parent) {
        this.thread = Thread.currentThread().getId();
        this.api = api;
        this.parent = parent;
    }

    public EventManager(FlagGameAPI api) {
        this(api, null);
    }

    @Override
    public void raise(Object event) {
        if (Thread.currentThread().getId() == thread) {
            raiseNow(event);
        } else {
            api.getServer().getScheduler().runTask(api.getPlugin(), () -> raiseNow(event));
        }
    }
    
    public void raiseNow(Object event) {
        listeners.forEach(listener -> {
            try {
                listener.raise(event);
            } catch (RuntimeException ex) {
                api.getLogger().warn("An exception has been thrown in an event listener", ex);
            }
        });
        children.forEach(child -> child.raise(event));
    }
    
    @Override
    public <T> ExecutorSelector<T> subscribe(T object) {
        return new SimpleExecutorSelector<>(new ObjectSubscriptionTarget(object));
    }
    
    @Override
    public <T> ExecutorSelector<T> subscribe(Class<T> clazz) {
        return new SimpleExecutorSelector<>(new ClassSubscriptionTarget(clazz));
    }

    @Override
    public <T> ExecutorSelector<T> subscribe(SubscriptionTarget target) {
        return new SimpleExecutorSelector<>(target);
    }
    
    @Override
    public EventManager getSubsystem() {
        EventManager child = new EventManager(api, this);
        children.add(child);
        return child;
    }

    /*package*/ ExecutorService getExecutor() {
        return api.getExecutor();
    }

    @Override
    public void close() throws Exception {
        if (parent != null) {
            parent.children.remove(this);
        }
    }

    private class SimpleExecutorSelector<T> implements ExecutorSelector<T> {

        final SubscriptionTarget target;

        SimpleExecutorSelector(SubscriptionTarget target) {
            this.target = target;
        }

        @Override
        public CallbackSelector<T> synchronously() {
            return new SimpleCallbackSelector<>(callback
                    -> new SyncRegisteredListener<>(0, EventManager.this, target, callback)
            );
        }

        @Override
        public CallbackSelector<T> synchronously(double priority) {
            return new SimpleCallbackSelector<>(callback
                    -> new SyncRegisteredListener<>(priority, EventManager.this, target, callback)
            );
        }

        @Override
        public CallbackSelector<T> asynchronously() {
            return new SimpleCallbackSelector<>(callback
                    -> new AsyncRegisteredListener<>(EventManager.this, target, callback)
            );
        }

    }

    private class SimpleCallbackSelector<T> implements CallbackSelector<T> {

        final Function<Function<? super T, ?>, ? extends RegisteredListener<T>> constructor;

        SimpleCallbackSelector(Function<Function<? super T, ?>, ? extends RegisteredListener<T>> constructor) {
            this.constructor = constructor;
        }

        @Override
        public void with(Function<? super T, ?> listener) {
            listeners.add(constructor.apply(listener));
        }

        @Override
        public void with(Consumer<? super T> listener) {
            with(event -> {
                listener.accept(event);
                return null;
            });
        }

        @Override
        public void with(Supplier<?> listener) {
            with((Function<? super T, ?>) event -> listener.get());
        }

        @Override
        public void with(Runnable listener) {
            with(event -> {
                listener.run();
                return null;
            });
        }

    }

}
