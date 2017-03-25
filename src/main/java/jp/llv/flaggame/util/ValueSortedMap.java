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

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * @author toyblocks
 */
public class ValueSortedMap<K, V> implements Map<K, V> {

    private final Comparator<Entry<K, V>> comparator;
    private final TreeSet<Entry<K, V>> map;

    public ValueSortedMap(Comparator<? super V> comparator, Comparator<? super K> subComparator) {
        Comparator<Entry<K, V>> c1 = Comparator.comparing(Entry::getValue, comparator);
        this.comparator = c1.thenComparing(Entry::getKey, subComparator);
        this.map = new TreeSet<>(this.comparator);
    }

    public ValueSortedMap(Comparator<? super V> comparator) {
        this.comparator = Comparator.comparing(Entry::getValue, comparator);
        this.map = new TreeSet<>(this.comparator);
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(map.stream().map(Entry::getKey).collect(Collectors.toSet()));
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(map.stream().map(Entry::getValue).collect(Collectors.toList()));
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.stream().map(Entry::getKey).anyMatch(k -> Objects.equals(key, k));
    }

    @Override
    public boolean containsValue(Object value) {
        return map.stream().map(Entry::getValue).anyMatch(v -> Objects.equals(value, v));
    }

    @Override
    public V get(Object key) {
        return map.stream().filter(e -> e.getKey().equals(key)).findFirst().map(Entry::getValue).orElse(null);
    }

    @Override
    public V put(K key, V value) {
        Objects.requireNonNull(key);
        for (Entry<K, V> entry : map) {
            if (key.equals(entry.getKey())) {
                V prev = entry.getValue();
                entry.setValue(value);
                return prev;
            }
        }
        map.add(new SimpleEntry<>(key, value));
        return null;
    }

    @Override
    public V remove(Object key) {
        Iterator<Entry<K, V>> it = map.iterator();
        while (it.hasNext()) {
            Entry<K, V> entry = it.next();
            if (entry.getKey().equals(key)) {
                it.remove();
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.entrySet().stream().forEach(e -> put(e.getKey(), e.getValue()));
    }

    @Override
    public void clear() {
        map.clear();
    }

    public static <K, V extends Comparable<? super V>> ValueSortedMap<K, V> newInstance() {
        return new ValueSortedMap<>(Comparator.naturalOrder());
    }

    public static <K extends Comparable<? super K>, V> ValueSortedMap<K, V> newInstance(Comparator<? super V> comparator) {
        return new ValueSortedMap<>(comparator, Comparator.naturalOrder());
    }

}
