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
package jp.llv.flaggame.api.event;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import jp.llv.flaggame.event.EventManager;

/**
 *
 * @author toyblocks
 */
public interface EventAPI extends AutoCloseable {

    <T> ExecutorSelector<T> subscribe(T object);

    <T> ExecutorSelector<T> subscribe(Class<T> clazz);

    <T> ExecutorSelector<T> subscribe(SubscriptionTarget target);

    void raise(Object event);

    EventManager getSubsystem();

    interface ExecutorSelector<T> {

        CallbackSelector<T> synchronously();

        CallbackSelector<T> synchronously(double priority);

        CallbackSelector<T> asynchronously();

    }

    interface CallbackSelector<T> {

        void with(Function<? super T, ?> listener);

        void with(Consumer<? super T> listener);

        void with(Supplier<?> listener);

        void with(Runnable listener);

    }

}
