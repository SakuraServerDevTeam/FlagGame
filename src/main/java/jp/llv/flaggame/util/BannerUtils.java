/* 
 * Copyright (C) 2015 Toyblocks, SakuraServerDev
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

import java.util.Arrays;
import java.util.Optional;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import static org.bukkit.block.banner.PatternType.*;

public final class BannerUtils {

    private BannerUtils() {
        throw new RuntimeException();
    }

    public static void paint(Banner banner, BannerChar character, DyeColor color, DyeColor backgroundColor) {
        for (BannerChar.DyeStep s : character.steps) {
            banner.addPattern(new Pattern(s.bgcolor ? backgroundColor : color, s.pattern));
        }
    }

    public static void paintChar(Banner banner, char character, DyeColor color, DyeColor backgroundColor) throws UnsupportedOperationException {
        paint(banner, BannerChar.getFromChar(character).orElseThrow(() -> new UnsupportedOperationException("That char is not supported")), color, backgroundColor);
    }

    public static void paintNum(Banner banner, int number, DyeColor color, DyeColor backgroundColor) {
        if (number < 0) {
            throw new IllegalArgumentException("The number must be positive");
        }
        char c;
        if (number <= 9) {
            c = Character.toChars(number)[0];
        } else {
            c = number < 50 ? 'X' : number < 100 ? 'L' : number < 500 ? 'C' : number < 1000 ? 'D' : 'M';
        }
        paintChar(banner, c, color, backgroundColor);
    }

    public enum BannerChar {

        NUM0('0', p(STRIPE_TOP), p(STRIPE_BOTTOM), p(STRIPE_LEFT), p(STRIPE_RIGHT), p(STRIPE_DOWNLEFT)),
        NUM1('1', p(SQUARE_TOP_LEFT), e(BORDER), p(STRIPE_CENTER)),
        NUM2('2', p(TRIANGLE_TOP), p(TRIANGLE_BOTTOM), p(SQUARE_TOP_LEFT), p(SQUARE_BOTTOM_RIGHT), e(RHOMBUS_MIDDLE), p(STRIPE_DOWNLEFT)),
        NUM3('3', p(STRIPE_MIDDLE), e(STRIPE_LEFT), p(STRIPE_BOTTOM), p(STRIPE_RIGHT), p(STRIPE_TOP)),
        NUM4('4', p(HALF_HORIZONTAL_MIRROR), p(STRIPE_LEFT), e(STRIPE_BOTTOM), p(STRIPE_RIGHT), p(STRIPE_MIDDLE)),
        NUM5('5', p(HALF_VERTICAL), e(HALF_HORIZONTAL_MIRROR), p(STRIPE_BOTTOM), e(DIAGONAL_RIGHT_MIRROR), p(STRIPE_DOWNRIGHT), p(STRIPE_TOP)),
        NUM6('6', p(STRIPE_RIGHT), e(HALF_HORIZONTAL), p(STRIPE_BOTTOM), p(STRIPE_MIDDLE), p(STRIPE_LEFT), p(STRIPE_TOP)),
        NUM7('7', p(STRIPE_TOP), e(DIAGONAL_RIGHT), p(SQUARE_BOTTOM_LEFT)),
        NUM8('8', p(STRIPE_TOP), p(STRIPE_MIDDLE), p(STRIPE_BOTTOM), p(STRIPE_LEFT), p(STRIPE_RIGHT)),
        NUM9('9', p(STRIPE_LEFT), e(HALF_HORIZONTAL_MIRROR), p(STRIPE_MIDDLE), p(STRIPE_TOP), p(STRIPE_RIGHT)),
        X_U('X', p(STRIPE_DOWNLEFT), p(STRIPE_DOWNRIGHT)),
        L_U('L', p(STRIPE_LEFT), p(STRIPE_BOTTOM)),
        C_U('C', p(STRIPE_BOTTOM), p(STRIPE_TOP), p(STRIPE_RIGHT), e(STRIPE_MIDDLE), p(STRIPE_LEFT)),
        D_U('D', p(STRIPE_RIGHT), p(STRIPE_TOP), p(STRIPE_BOTTOM), e(CURLY_BORDER), p(STRIPE_LEFT)),
        M_U('M', p(TRIANGLE_TOP), e(TRIANGLES_TOP), p(STRIPE_RIGHT), p(STRIPE_LEFT)),;

        private final char c;
        private final DyeStep[] steps;

        private BannerChar(char c, DyeStep... steps) {
            this.c = c;
            this.steps = steps;
        }

        public static Optional<BannerChar> getFromChar(char c) {
            return Arrays.stream(values()).filter(bc -> bc.c == c).findAny();
        }

        private static DyeStep e(PatternType p) {
            return new DyeStep(true, p);
        }

        private static DyeStep p(PatternType p) {
            return new DyeStep(false, p);
        }

        private static final class DyeStep {

            private final boolean bgcolor;
            private final PatternType pattern;

            private DyeStep(boolean bgcolor, PatternType pattern) {
                this.bgcolor = bgcolor;
                this.pattern = pattern;
            }

        }

    }

}
