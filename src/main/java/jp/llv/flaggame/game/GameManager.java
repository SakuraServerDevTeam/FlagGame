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

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.llv.flaggame.reception.GameReception;
import jp.llv.flaggame.reception.ReceptionManager;
import syam.flaggame.FlagGame;

/**
 *
 * @author Toyblocks
 */
public class GameManager implements Iterable<Game> {

    private final FlagGame plugin;
    private final ReceptionManager receptions;

    public GameManager(FlagGame plugin) {
        this.plugin = plugin;
        this.receptions = plugin.getReceptions();
    }

    public Collection<Game> getGames() {
        return this.receptions.getReceptions().stream()
                .map(GameReception::getGame)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
    
    public Collection<Game> getGames(Game.State state) {
        return this.getGames().stream()
                .filter(g -> g.getState() == state)
                .collect(Collectors.toSet());
    }
    
    public Optional<Game> getGame(String id) {
        return this.receptions.getReception(id).flatMap(GameReception::getGame);
    }

    @Override
    public Iterator<Game> iterator() {
        return this.getGames().iterator();
    }

}
