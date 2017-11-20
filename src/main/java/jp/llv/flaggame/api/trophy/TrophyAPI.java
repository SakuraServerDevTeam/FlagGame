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
package jp.llv.flaggame.api.trophy;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import jp.llv.flaggame.api.exception.AccountNotReadyException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.util.function.ThrowingPredicate;

/**
 *
 * @author toyblocks
 */
public interface TrophyAPI extends Iterable<Trophy> {
    
    void addTrophy(Trophy trophy);
    
    Optional<Trophy> getTrophy(String name);
    
    void removeTrophy(Trophy trophy);
    
    Map<String, Trophy> getTrophy();

    <T extends Trophy, E extends Exception> void updateProgress(GamePlayer player, Class<T> clazz, ThrowingPredicate<? super T, E> predicate) throws AccountNotReadyException;
    
    @Override
    public default Iterator<Trophy> iterator() {
        return getTrophy().values().iterator();
    }
    
}
