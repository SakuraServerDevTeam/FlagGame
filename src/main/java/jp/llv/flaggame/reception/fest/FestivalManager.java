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
package jp.llv.flaggame.reception.fest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalManager implements Iterable<FestivalSchedule> {

    private final Map<String, FestivalSchedule> schedules = new HashMap<>();

    public Map<String, FestivalSchedule> getFestivals() {
        return Collections.unmodifiableMap(schedules);
    }

    public void addFestival(FestivalSchedule festival) {
        schedules.put(festival.getName(), festival);
    }

    public void removeFestival(FestivalSchedule festival) {
        schedules.remove(festival.getName());
    }

    public Optional<FestivalSchedule> getFestival(String name) {
        return Optional.ofNullable(schedules.get(name));
    }

    @Override
    public Iterator<FestivalSchedule> iterator() {
        return this.getFestivals().values().iterator();
    }

}
