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
package jp.llv.flaggame.reception;

import java.util.List;
import java.util.UUID;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;

/**
 *
 * @author toyblocks
 */
public class EvenRequiredRealtimeTeamingReception extends RealtimeTeamingReception {

    public EvenRequiredRealtimeTeamingReception(FlagGame plugin, UUID id, List<String> args) {
        super(plugin, id, args);
    }

    @Override
    public void start(List<String> args) throws CommandException {
        if (super.getStage().isPresent()
                && super.getPlayers().size() % super.getStage().get().getTeams().size() != 0) {
            throw new CommandException("参加人数が均等ではありません！");
        }
    }

}
