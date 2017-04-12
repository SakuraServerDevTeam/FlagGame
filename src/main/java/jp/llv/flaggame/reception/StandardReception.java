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
package jp.llv.flaggame.reception;

import java.util.List;
import java.util.UUID;
import jp.llv.flaggame.util.TriFunction;
import syam.flaggame.FlagGame;

/**
 *
 * @author SakuraServerDev
 */
public enum StandardReception implements ReceptionType<GameReception> {

    REALTIME(
            RealtimeTeamingReception::new,
            "realtime", "rt"
    ),
    MATCHING(
            MatchingReception::new,
            "matching", "ma"
    ),;

    private final String name;
    private final String[] aliases;
    private final TriFunction<FlagGame, UUID, List<String>, GameReception> constructor;

    private StandardReception(TriFunction<FlagGame, UUID, List<String>, GameReception> constructor, String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
        this.constructor = constructor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }
    
    @Override
    public GameReception newInstance(FlagGame plugin, UUID id, List<String> args) {
        return constructor.apply(plugin, id, args);
    }
    
    public static StandardReception of(String name) {
        for (StandardReception type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
            for (String alias : type.aliases) {
                if (alias.equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException();
    }

}
