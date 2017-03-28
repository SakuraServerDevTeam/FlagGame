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

import java.text.DecimalFormat;

/**
 *
 * @author Toyblocks
 */
public final class ConvertUtils {
    
    private static final DecimalFormat TIME_FORMAT= new DecimalFormat("###0.0");
    
    private  ConvertUtils() {
        throw new RuntimeException();
    }
    
    public static String format(long timeinms) {
        double d = timeinms / 1000;
        return TIME_FORMAT.format(d);
    }
    
    public static long toTick(long timeinms) {
        return timeinms / 50;
    }
    
    public static long toMiliseconds(long tick) {
        return tick * 50;
    } 
    
    public static String smartRound(double value) {
        char[] chars = Double.toString(value).toCharArray();
        StringBuilder result = new StringBuilder();
        int count = -1;
        for (char c : chars) {
            if (c== '.' || (count >= 0 && c == '0')) {
                count++;
            } else if (count >= 0) {
                count = 0;
            }
            if (count == 2) {
                break;
            }
            result.append(c);
        }
        return result.toString();
    }
    
}
