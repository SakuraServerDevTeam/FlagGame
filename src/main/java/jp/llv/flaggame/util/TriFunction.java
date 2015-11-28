/* 
 * Copyright (C) 2015 Toyblocks, SakuraServerDev
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
package jp.llv.flaggame.util;

/**
 * Represents a function that accepts three arguments and produces a result. This
 * is the three-arity specialization of {@link java.util.function.Function}.
 *
 * @author Toyblocks
 * @param <A1> the type of the first argument to the function
 * @param <A2> the type of the second argument to the function
 * @param <A3> the type of the result of the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface TriFunction<A1, A2, A3, R> {

    R apply(A1 a1, A2 a2, A3 a3);

}
