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

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

/**
 *
 * @author Toyblocks
 */
public class IntMap<K> extends HashMap<K, Integer> {
    private static final long serialVersionUID = 1L;

    public IntMap(int i, float f) {
        super(i, f);
    }

    public IntMap(int i) {
        super(i);
    }

    public IntMap() {
    }

    public IntMap(Map<? extends K, ? extends Integer> map) {
        super(map);
    }
    
    public void increase(K key) {
        this.add(key, 1);
    }
    
    public void add(K key, int num) {
        Integer v = super.get(key);
        v = v != null ? v+=num : num;
        super.put(key, v);
    }
    
    public void decrease(K key) {
        this.subtract(key, 1);
    }
    
    public void subtract(K key, int num) {
        this.add(key, -num);
    }
    
    public int getOr(K key, int def) {
        Integer i = super.get(key);
        return i != null ? i : def;
    }
    
    public int getOrZero(K key) {
        return this.getOr(key, 0);
    }
    
    public OptionalInt getOptional(K key) {
        Integer i = super.get(key);
        return i != null ? OptionalInt.of(i) : OptionalInt.empty();
    }
    
}
