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

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 *
 * @author Toyblocks
 */
public final class MapUtils {

    private MapUtils() {
        throw new RuntimeException();
    }

    public static <K, V> Set<K> getKeyByValue(Map<? extends K, ? extends V> map, V value) {
        Set<K> result = new HashSet<>();
        for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
            if (Objects.equals(e.getValue(), value)) {
                result.add(e.getKey());
            }
        }
        return result;
    }
    
    public static <K, V> void removeValue(Map<? extends K, ? extends V> map, V value) {
        for (K key : getKeyByValue(map, value)) {
            map.remove(key, value);
        }
    }

    public static <K, V extends Comparable<V>> LinkedHashMap<V, Set<K>> rank(Map<? extends K, ? extends V> map) {
        TreeSet<V> sorted = new TreeSet<>(map.values());
        LinkedHashMap<V, Set<K>> result = new LinkedHashMap<>();
        for (V v : sorted) {
            result.put(v, getKeyByValue(map, v));
        }
        return result;
    }

    public static <K, V> LinkedHashMap<V, Set<K>> rank(Map<? extends K, ? extends V> map, Comparator<? super V> comparator) {
        TreeSet<V> sorted = new TreeSet<>(comparator);
        sorted.addAll(map.values());
        LinkedHashMap<V, Set<K>> result = new LinkedHashMap<>();
        for (V v : sorted) {
            result.put(v, getKeyByValue(map, v));
        }
        return result;
    }

    public static <K1, K2, A, V1, V2>
            Map<K2, V2> remap(Map<K1, V1> source,
                              Function<? super K1, ? extends K2> keyMapper,
                              Collector<V1, A, V2> collector) {
        return source.entrySet().stream().collect(Collectors.groupingBy(
                e -> keyMapper.apply(e.getKey()),
                Collectors.mapping(
                        Map.Entry::getValue,
                        collector
                )
        ));
    }

    public static <K, V> Map.Entry<K, V> tuple(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

}
