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

import jp.llv.flaggame.api.util.function.BiDoubleFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jp.llv.flaggame.profile.record.LocationRecord;
import org.bukkit.Location;

/**
 *
 * @author toyblocks
 */
public class StreamUtil {

    private StreamUtil() {
        throw new RuntimeException();
    }

    public static Predicate<LocationRecord> isLocatedIn(Location loc) {
        return l -> loc.getBlockX() == Location.locToBlock(l.getX())
                    && loc.getBlockY() == Location.locToBlock(l.getY())
                    && loc.getBlockZ() == Location.locToBlock(l.getZ());
    }

    public static Predicate<LocationRecord> isLocated(Location loc) {
        return l -> loc.getX() == l.getX()
                    && loc.getY() == l.getY()
                    && loc.getZ() == l.getZ();
    }

    public static <T> BinaryOperator<T> toLastElement() {
        return (t, u) -> u;
    }

    public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>>
            toMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <T, U> Collector<T, ?, Stream<U>>
            deviation(ToDoubleFunction<T> keyMapper, BiDoubleFunction<T, U> valueMapper) {
        class DeviationCalcurator {

            List<T> list = new ArrayList<>();
            double sum = 0;
            double squaredSum = 0;
            int count = 0;

            void add(T node) {
                double val = keyMapper.applyAsDouble(node);
                list.add(node);
                sum += val;
                squaredSum += (val * val);
                count++;
            }

            DeviationCalcurator addAll(DeviationCalcurator another) {
                list.addAll(another.list);
                sum += another.sum;
                squaredSum += another.squaredSum;
                count += another.count;
                return this;
            }

            Stream<U> finish() {
                double average = sum / count;
                double variance = (squaredSum / count) - (average * average);
                double deviation = Math.sqrt(variance);
                if (deviation == 0.0) { // all values are the same
                    return list.stream().map(v -> valueMapper.apply(v, 0.0));
                } else {
                    return list.stream().map(v
                            -> valueMapper.apply(v,
                                    (keyMapper.applyAsDouble(v) - average) / deviation
                            ));
                }
            }
        }
        return Collector.of(
                DeviationCalcurator::new,
                DeviationCalcurator::add,
                DeviationCalcurator::addAll,
                DeviationCalcurator::finish);
    }

}
