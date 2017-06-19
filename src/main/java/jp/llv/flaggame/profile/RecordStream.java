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
package jp.llv.flaggame.profile;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jp.llv.flaggame.profile.record.GameRecord;
import jp.llv.flaggame.profile.record.PlayerRecord;
import org.bson.Document;

/**
 *
 * @author toyblocks
 */
public interface RecordStream extends Iterable<GameRecord> {

    List<GameRecord> getRecords();

    default List<Document> getDocuments() {
        return stream().map(GameRecord::toDocument).collect(Collectors.toList());
    }

    @Override
    default Iterator<GameRecord> iterator() {
        return getRecords().iterator();
    }

    default Stream<GameRecord> stream() {
        return getRecords().stream();
    }

    default <T extends GameRecord> Stream<T> stream(Class<T> clazz) {
        return stream().filter(clazz::isInstance).map(clazz::cast);
    }

    default <T extends PlayerRecord> Stream<T> stream(UUID player, Class<T> clazz) {
        return stream(clazz).filter(r -> r.getPlayer().equals(player));
    }

    default Stream<PlayerRecord> stream(UUID player) {
        return stream(player, PlayerRecord.class);
    }

    default <T extends GameRecord, K>
            Map<K, List<T>> groupingBy(Class<? extends T> clazz,
                                       Function<? super T, ? extends K> classifier) {
        return stream(clazz).collect(Collectors.groupingBy(classifier));
    }

    default <T extends GameRecord, K, A, D>
            Map<K, D> groupingBy(Class<? extends T> clazz,
                                 Function<? super T, ? extends K> classifier,
                                 Collector<? super T, A, D> downstream) {
        return stream(clazz).collect(Collectors.groupingBy(classifier, downstream));
    }

    default <T extends GameRecord, K, D, A, M extends Map<K, D>>
            M groupingBy(Class<? extends T> clazz,
                         Function<? super T, ? extends K> classifier,
                         Supplier<M> mapFactory,
                         Collector<? super T, A, D> downstream) {
        return stream(clazz).collect(Collectors.groupingBy(classifier, mapFactory, downstream));
    }

    default void push(GameRecord record) {
        getRecords().add(record);
    }

}
