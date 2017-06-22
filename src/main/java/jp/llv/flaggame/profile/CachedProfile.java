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

import jp.llv.flaggame.api.profile.StatEntry;
import jp.llv.flaggame.api.profile.Profile;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.llv.flaggame.api.profile.RecordType;

/**
 *
 * @author SakuraServerDev
 */
public abstract class CachedProfile implements Profile {

    private final Map<RecordType, StatEntry> stats = Collections.synchronizedMap(new EnumMap<>(RecordType.class));

    @Override
    public Optional<StatEntry> getStat(RecordType type) {
        Objects.requireNonNull(type);
        return Optional.ofNullable(stats.get(type));
    }

    /*package*/ void setStat(RecordType type, StatEntry entry) {
        stats.put(type, entry);
    }

}
