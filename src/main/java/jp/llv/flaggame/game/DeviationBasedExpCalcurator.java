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

import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.util.MapUtils;

/**
 *
 * @author toyblocks
 */
public class DeviationBasedExpCalcurator implements ExpCalcurator {

    @Override
    public UnaryOperator<Map.Entry<GamePlayer, Double>> calcurate(Set<GamePlayer> winners, long gametime, int players) {
        return e -> MapUtils.tuple(
                e.getKey(),
                (e.getValue() + 6.0)
                * 150.0
                * calcGameTimeFactor(gametime)
                * calcPlayerCountFactor(players)
                * calcResultFactor(winners, e.getKey())
        );
    }

    private double calcGameTimeFactor(long gametime) {
        return Math.atan(gametime / 60_000.0 - 2.0) * 2.0 / Math.PI + 1.0;
    }

    private double calcPlayerCountFactor(int players) {
        return ((double) players) / (players + 1.0);
    }

    private double calcResultFactor(Set<GamePlayer> winners, GamePlayer player) {
        return winners.contains(player) ? 1.2 : 1.0;
    }

}
