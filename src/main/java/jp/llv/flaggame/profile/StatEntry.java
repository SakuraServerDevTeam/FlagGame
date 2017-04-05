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
public class StatEntry {
    
    private int count;
    private double sum;

    public StatEntry(int count, double sum) {
        this.sum = sum;
        this.count = count;
    }

    public StatEntry() {
        this(0, 0D);
    }

    public int getCount() {
        return count;
    }

    public double getSum() {
        return sum;
    }
    
    public double getAverage() {
        return sum / count;
    }
    
}
