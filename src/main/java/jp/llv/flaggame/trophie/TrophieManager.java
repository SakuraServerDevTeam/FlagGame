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
package jp.llv.flaggame.trophie;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.AccountNotReadyException;
import jp.llv.flaggame.api.player.Account;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.trophie.Trophie;
import jp.llv.flaggame.api.trophie.TrophieAPI;
import jp.llv.flaggame.api.util.function.ThrowingPredicate;

/**
 *
 * @author toyblocks
 */
public class TrophieManager implements TrophieAPI {

    private final FlagGameAPI api;
    private final Map<String, Trophie> trophies = Collections.synchronizedMap(new HashMap<>());

    public TrophieManager(FlagGameAPI api) {
        this.api = api;
    }

    @Override
    public void addTrophie(Trophie trophie) {
        trophies.put(trophie.getName(), trophie);
    }

    @Override
    public Optional<Trophie> getTrophie(String name) {
        return Optional.ofNullable(trophies.get(name));
    }

    @Override
    public void removeTrophie(Trophie trophie) {
        trophies.remove(trophie.getName());
    }

    @Override
    public Map<String, Trophie> getTrophies() {
        return Collections.unmodifiableMap(trophies);
    }

    @Override
    public <T extends Trophie, E extends Exception> void updateProgress(GamePlayer player, Class<T> clazz, ThrowingPredicate<? super T, E> predicate) throws AccountNotReadyException {
        Account account = player.getAccount();
        for (Trophie trophie : this) {
            if (!clazz.isInstance(trophie)
                    || account.getUnlockedTrophies().contains(trophie.getName())) {
                continue;
            }
            try {
                if (predicate.test((T) trophie)) {
                    trophie.reward(player);
                }
            } catch (Exception ex) {
                api.getLogger().warn("Failed to update progress of a trophie '" + trophie.getName() + "'", ex);
            }
        }
    }

}
