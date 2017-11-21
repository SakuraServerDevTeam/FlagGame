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
package jp.llv.flaggame.trophy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.AccountNotReadyException;
import jp.llv.flaggame.api.player.Account;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.util.function.ThrowingPredicate;
import jp.llv.flaggame.api.trophy.Trophy;
import jp.llv.flaggame.api.trophy.TrophyAPI;

/**
 *
 * @author toyblocks
 */
public class TrophyManager implements TrophyAPI {

    private static final String INITIAL_TROPHY_NAME = "$initial";

    private final FlagGameAPI api;
    private final Map<String, Trophy> trophies = Collections.synchronizedMap(new HashMap<>());

    public TrophyManager(FlagGameAPI api) {
        this.api = api;
    }

    @Override
    public void addTrophy(Trophy trophy) {
        if (!Trophy.NAME_REGEX.matcher(trophy.getName()).matches()) {
            throw new IllegalArgumentException("Invalid trophy name");
        }
        trophies.put(trophy.getName(), trophy);
    }

    @Override
    public Trophy getInitialTrophy() {
        if (trophies.containsKey(INITIAL_TROPHY_NAME)) {
            return trophies.get(INITIAL_TROPHY_NAME);
        } else {
            Trophy initialTrophy = new ImpossibleTrophy(INITIAL_TROPHY_NAME);
            trophies.put(INITIAL_TROPHY_NAME, initialTrophy);
            return initialTrophy;
        }
    }

    @Override
    public Optional<Trophy> getTrophy(String name) {
        if (name.equals(INITIAL_TROPHY_NAME)) {
            return Optional.empty();
        }
        return Optional.ofNullable(trophies.get(name));
    }

    @Override
    public void removeTrophy(Trophy trophy) {
        if (trophy.getName().equals(INITIAL_TROPHY_NAME)) {
            return;
        }
        trophies.remove(trophy.getName());
    }

    @Override
    public Set<String> getNormalTrophies() {
        return trophies.keySet().stream()
                .filter(s -> !INITIAL_TROPHY_NAME.equals(s))
                .collect(Collectors.toSet());
    }

    @Override
    public Map<String, Trophy> getTrophies() {
        return Collections.unmodifiableMap(trophies);
    }

    @Override
    public <T extends Trophy, E extends Exception> void updateProgress(GamePlayer player, Class<T> clazz, ThrowingPredicate<? super T, E> predicate) throws AccountNotReadyException {
        Account account = player.getAccount();
        for (Trophy trophy : this) {
            if (!clazz.isInstance(trophy)
                    || account.getUnlockedTrophies().contains(trophy.getName())) {
                continue;
            }
            try {
                if (predicate.test((T) trophy)) {
                    trophy.reward(player);
                }
            } catch (Exception ex) {
                api.getLogger().warn("Failed to update progress of a trophy '" + trophy.getName() + "'", ex);
            }
        }
    }

}
