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
package syam.flaggame.command.dashboard;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author SakuraServerDev
 * @param <R> callback
 */
public class CommandBuilder<R> {
    
    private final StringBuilder command = new StringBuilder("/flaggame:flag");
    private final Function<String, R> callback;

    private CommandBuilder(Function<String, R> callback) {
        this.callback = callback;
    }
    
    public CommandBuilder<R> append(Object argument) {
        if (command.length() != 1) {
            command.append(' ');
        }
        command.append(Objects.toString(argument));
        return this;
    }
    
    public R create() {
        return callback.apply(command.toString());
    }
    
    public static CommandBuilder<String> newBuilder() {
        return new CommandBuilder<>(Function.identity());
    }
    
    public static <R> CommandBuilder<R> newBuilder(Function<String, R> callback) {
        return new CommandBuilder<>(callback);
    }
    
}
