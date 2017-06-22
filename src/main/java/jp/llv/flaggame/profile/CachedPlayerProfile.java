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

import jp.llv.flaggame.api.profile.PlayerProfile;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 *
 * @author SakuraServerDev
 */
public class CachedPlayerProfile extends CachedProfile implements PlayerProfile {

    private static final double LEVEL_UP_FACTOR = 1.1;
    private static final double LEVEL_UP_FACTOR_LOG = Math.log10(LEVEL_UP_FACTOR);

    private Long exp;
    private Double vibe;

    @Override
    public OptionalLong getExp() {
        return exp == null ? OptionalLong.empty() : OptionalLong.of(exp);
    }

    @Override
    public OptionalInt getLevel() {
        return exp == null ? OptionalInt.empty() : OptionalInt.of((int) getLevel(exp));
    }

    @Override
    public OptionalLong getExpRequiredToLevelUp() {
        return exp == null ? OptionalLong.empty() : OptionalLong.of(getExpRequired(getLevel(exp) + 1) - exp);
    }

    @Override
    public OptionalDouble getVibe() {
        return vibe == null ? OptionalDouble.empty() : OptionalDouble.of(vibe);
    }

    /*package*/ void setExp(long exp) {
        this.exp = exp;
    }

    /*package*/ void setVibe(double vibe) {
        this.vibe = vibe;
    }

    public static long getLevel(long exp) {
        return (long) Math.floor((((Math.log10((exp / 1000.0) + 1.0) / LEVEL_UP_FACTOR_LOG)) * 2.0) + 1.0);
    }

    public static long getExpRequired(long level) {
        return (long) Math.ceil(1000.0 * (Math.pow(LEVEL_UP_FACTOR, (level - 1.0) * 0.5) - 1));
    }

}
