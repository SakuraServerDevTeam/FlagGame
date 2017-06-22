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
package jp.llv.flaggame.game;

import jp.llv.flaggame.api.game.GameAPI;
import jp.llv.flaggame.api.game.Game;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.FlagGameAPI;

/**
 *
 * @author Toyblocks
 */
public class GameManager implements GameAPI {

    private final FlagGameAPI api;

    public GameManager(FlagGameAPI api) {
        this.api = api;
    }

    @Override
    public Collection<Game> getGames() {
        return api.getReceptions().getReceptions().stream()
                .flatMap(r -> r.getGames().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Game> getGames(Game.State state) {
        return this.getGames().stream()
                .filter(g -> g.getState() == state)
                .collect(Collectors.toSet());
    }

    @Override
    public Iterator<Game> iterator() {
        return this.getGames().iterator();
    }

}
