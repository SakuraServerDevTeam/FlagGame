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

/**
 *
 * @author SakuraServerDev
 */
public class PlayerProfile extends ProfileBase {

    private static final double LEVEL_UP_FACTOR = 1.1;
    private static final double LEVEL_UP_FACTOR_LOG = Math.log10(LEVEL_UP_FACTOR);
    
    private double exp;
    private double vibe;
    
    public double getExp() {
        return exp;
    }
    
    public static int getLevel(double exp) {
        // 2log[1.1]((exp/1000)+1) + 1
        return (int) (Math.floor((Math.log10((exp / 1000.0) + 1.0) / LEVEL_UP_FACTOR_LOG)) * 2.0) + 1;
    }
    
    public double getVibe() {
        return vibe;
    }

}
