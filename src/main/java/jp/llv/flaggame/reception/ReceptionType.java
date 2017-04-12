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
import syam.flaggame.FlagGame;

/**
 *
 * @author SakuraServerDev
 * @param <R> reception class this type creates.
 */
public interface ReceptionType<R extends GameReception> {

    String getName();
    
    String[] getAliases();
    
    R newInstance(FlagGame plugin, UUID id, List<String> args);

}
