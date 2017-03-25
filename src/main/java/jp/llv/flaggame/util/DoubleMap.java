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
package jp.llv.flaggame.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

/**
 *
 * @author Toyblocks
 * @param <K> the type of keys maintained by this map
 */
public class DoubleMap<K> extends HashMap<K, Double> {
    private static final long serialVersionUID = 1L;

    public DoubleMap(int i, float f) {
        super(i, f);
    }

    public DoubleMap(int i) {
        super(i);
    }

    public DoubleMap() {
    }

    public DoubleMap(Map<? extends K, ? extends Double> map) {
        super(map);
    }
    
    public void increase(K key) {
        this.add(key, 1);
    }
    
    public void add(K key, double num) {
        Double v = super.get(key);
        v = v != null ? v+=num : num;
        super.put(key, v);
    }
    
    public void decrease(K key) {
        this.subtract(key, 1);
    }
    
    public void subtract(K key, double num) {
        this.add(key, -num);
    }
    
    public double getOr(K key, double def) {
        Double i = super.get(key);
        return i != null ? i : def;
    }
    
    public double getOrZero(K key) {
        return this.getOr(key, 0);
    }
    
    public OptionalDouble getOptional(K key) {
        Double i = super.get(key);
        return i != null ? OptionalDouble.of(i) : OptionalDouble.empty();
    }
    
    public void putAll(Collection<? extends K> keys, double value) {
        for (K k : keys) {
            super.put(k, value);
        }
    }
    
}
