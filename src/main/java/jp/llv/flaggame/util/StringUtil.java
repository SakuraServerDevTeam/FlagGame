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

/**
 *
 * @author toyblocks
 */
public class StringUtil {

    private StringUtil() {
        throw new RuntimeException();
    }

    public static String capitalize(String source) {
        char[] result = source.toCharArray();
        boolean front = true;
        for (int i = 0; i < result.length; i++) {
            if (front) {
                result[i] = Character.toUpperCase(result[i]);
            } else {
                result[i] = Character.toLowerCase(result[i]);
            }
            front = !Character.isAlphabetic(result[i]);
        }
        return String.copyValueOf(result);
    }

    public static int countChar(String source, char target) {
        int count = 0;
        for (char c : source.toCharArray()) {
            if (c == target) {
                count++;
            }
        }
        return count;
    }

}
