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
package jp.llv.flaggame.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import jp.llv.flaggame.api.exception.InvalidOptionException;

/**
 * Command line unordered optional arguments handler.
 *
 * @author toyblocks
 */
public class OptionSet {

    private final Map<String, String> options;

    public OptionSet(List<String> args) throws InvalidOptionException {
        options = new HashMap<>(args.size());
        String key = null;
        String value = null;
        for (String arg : args) {
            if (!arg.isEmpty() && arg.charAt(0) == '-') {
                options.put(key, value);
                key = arg.substring(1, arg.length());
            } else if (value == null) {
                value = arg;
            } else {
                value += arg;
            }
        }
        if (key != null || value != null) {
            options.put(key, value);
        }
    }

    public OptionSet(String... args) throws InvalidOptionException {
        this(Arrays.asList(args));
    }

    public String getString(String key, String value) {
        return options.containsKey(key) ? options.get(key) : value;
    }

    public String getString(String key) throws InvalidOptionException {
        if (options.get(key) != null) {
            return options.get(key);
        } else {
            throw new InvalidOptionException("missing value");
        }
    }

    public byte getByte(String key, byte value) throws InvalidOptionException {
        return getMappedValue(key, value, Byte::parseByte);
    }

    public byte getByte(String key) throws InvalidOptionException {
        return getMappedValue(key, Byte::parseByte);
    }

    public short getShort(String key, short value) throws InvalidOptionException {
        return getMappedValue(key, value, Short::parseShort);
    }

    public short getShort(String key) throws InvalidOptionException {
        return getMappedValue(key, Short::parseShort);
    }

    public int getInt(String key, int value) throws InvalidOptionException {
        return getMappedValue(key, value, Integer::parseInt);
    }

    public int getInt(String key) throws InvalidOptionException {
        return getMappedValue(key, Integer::parseInt);
    }

    public long getLong(String key, long value) throws InvalidOptionException {
        return getMappedValue(key, value, Long::parseLong);
    }

    public long getLong(String key) throws InvalidOptionException {
        return getMappedValue(key, Long::parseLong);
    }

    public float getFloat(String key, float value) throws InvalidOptionException {
        return getMappedValue(key, value, Float::parseFloat);
    }

    public float getFloat(String key) throws InvalidOptionException {
        return getMappedValue(key, Float::parseFloat);
    }

    public double getDouble(String key, double value) throws InvalidOptionException {
        return getMappedValue(key, value, Double::parseDouble);
    }

    public double getDouble(String key) throws InvalidOptionException {
        return getMappedValue(key, Double::parseDouble);
    }

    public <E extends Enum<E>> E getEnum(String key, E value) throws InvalidOptionException {
        return getMappedValue(key, value, v -> Enum.valueOf(value.getDeclaringClass(), v));
    }

    public <E extends Enum<E>> E getEnum(String key, Class<E> clazz) throws InvalidOptionException {
        return getMappedValue(key, v -> Enum.valueOf(clazz, v));
    }

    public boolean isPresent(String key) {
        return options.containsKey(key);
    }

    private <T> T getMappedValue(String key, T value, Function<? super String, ? extends T> mapper) throws InvalidOptionException {
        try {
            return options.containsKey(key) ? mapper.apply(options.get(key)) : value;
        } catch (RuntimeException ex) {
            throw new InvalidOptionException(ex);
        }
    }

    private <T> T getMappedValue(String key, Function<? super String, ? extends T> mapper) throws InvalidOptionException {
        try {
            if (options.containsKey(key)) {
                return mapper.apply(options.get(key));
            } else {
                throw new InvalidOptionException("missing value");
            }
        } catch (RuntimeException ex) {
            throw new InvalidOptionException(ex);
        }
    }

}
