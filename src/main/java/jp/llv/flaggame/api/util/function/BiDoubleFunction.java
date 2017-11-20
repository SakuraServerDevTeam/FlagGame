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
package jp.llv.flaggame.api.util.function;

/**
 * Represents a function that accepts two arguments including double value and
 * produces a result. This is the three-arity specialization of
 * {@link java.util.function.Function}.
 *
 * @author Toyblocks
 * @param <T> the type of the first argument to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface BiDoubleFunction<T, R> {

    R apply(T t1, double t2);

}
