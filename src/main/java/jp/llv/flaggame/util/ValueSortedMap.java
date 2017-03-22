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

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 *
 * @author toyblocks
 */
public class ValueSortedMap<K, V> implements SortedMap<K, V> {
    
    private TreeMap<K, V> map;
    
    public ValueSortedMap(Comparator<? super V> valueComparator) {
        Comparator<K> keyComparator = (k1, k2) -> {
            V v1 = map.get(k1), v2 = map.get(k2);
            return valueComparator.compare(v1, v2);
        };
        map = new TreeMap<>(keyComparator);
    }
    
    public ValueSortedMap(Map<? extends K, ? extends V> source, Comparator<? super V> valueComparator) {
        this(valueComparator);
        map.putAll(source);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public Comparator<? super K> comparator() {
        return map.comparator();
    }

    @Override
    public K firstKey() {
        return map.firstKey();
    }

    @Override
    public K lastKey() {
        return map.lastKey();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        this.map.putAll(map);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    public Entry<K, V> firstEntry() {
        return map.firstEntry();
    }

    public Entry<K, V> lastEntry() {
        return map.lastEntry();
    }

    public Entry<K, V> pollFirstEntry() {
        return map.pollFirstEntry();
    }

    public Entry<K, V> pollLastEntry() {
        return map.pollLastEntry();
    }

    public Entry<K, V> lowerEntry(K key) {
        return map.lowerEntry(key);
    }

    public K lowerKey(K key) {
        return map.lowerKey(key);
    }

    public Entry<K, V> floorEntry(K key) {
        return map.floorEntry(key);
    }

    public K floorKey(K key) {
        return map.floorKey(key);
    }

    public Entry<K, V> ceilingEntry(K key) {
        return map.ceilingEntry(key);
    }

    public K ceilingKey(K key) {
        return map.ceilingKey(key);
    }

    public Entry<K, V> higherEntry(K key) {
        return map.higherEntry(key);
    }

    public K higherKey(K key) {
        return map.higherKey(key);
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    public NavigableSet<K> navigableKeySet() {
        return map.navigableKeySet();
    }

    public NavigableSet<K> descendingKeySet() {
        return map.descendingKeySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public NavigableMap<K, V> descendingMap() {
        return map.descendingMap();
    }

    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return map.subMap(fromKey, fromInclusive, toKey, toInclusive);
    }

    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return map.headMap(toKey, inclusive);
    }

    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return map.tailMap(fromKey, inclusive);
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return map.subMap(fromKey, toKey);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return map.headMap(toKey);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return map.tailMap(fromKey);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return map.replace(key, value);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        map.replaceAll(function);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
}
