/*
 * Copyright (C) 2015 Toyblocks
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
package jp.llv.flaggame.game.basic.objective;

import org.bukkit.DyeColor;
import static org.bukkit.DyeColor.*;

/**
 *
 * @author Toyblocks
 */
public class HoldBanner {

    public static DyeColor getDamageColor(int current, int max) {
        if (current <= 1) {
            return RED;
        } else {
            double p = current * 10 / (double) max;
            return p > 9 ? GRAY : p > 8 ? BROWN : p > 7 ? MAGENTA : p > 6 ? PURPLE : p > 5 ? BLUE
                    : p > 4 ? LIGHT_BLUE : p > 3 ? GREEN : p > 2 ? LIME : p > 1 ? YELLOW : ORANGE;
        }
    }

}
