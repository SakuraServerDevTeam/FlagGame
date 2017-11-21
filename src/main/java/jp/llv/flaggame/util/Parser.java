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
package jp.llv.flaggame.util;

import jp.llv.flaggame.api.exception.CommandException;

/**
 *
 * @author toyblocks
 */
public final class Parser {

    private Parser() {
        throw new RuntimeException();
    }

    public static int asInt(int min, String value, int max) throws CommandException {
        try {
            int number = Integer.parseInt(value);
            if (number < min || max < number) {
                throw new CommandException("&c入力値'" + number + "'は許容値'" + min + "-" + max + "'外です");
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new CommandException("&c入力値'" + value + "'は適切な数値フォーマットではありません");
        }
    }

    public static double asDouble(double min, String value) throws CommandException {
        try {
            double number = Double.parseDouble(value);
            if (number < min) {
                throw new CommandException("&c入力値'" + number + "'は許容値'" + min + "-'外です");
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new CommandException("&c入力値'" + value + "'は適切な数値フォーマットではありません");
        }
    }

    public static double asDouble(double min, String value, double max) throws CommandException {
        try {
            double number = Double.parseDouble(value);
            if (number < min || max < number) {
                throw new CommandException("&c入力値'" + number + "'は許容値'" + min + "-" + max + "'外です");
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new CommandException("&c入力値'" + value + "'は適切な数値フォーマットではありません");
        }
    }

}
