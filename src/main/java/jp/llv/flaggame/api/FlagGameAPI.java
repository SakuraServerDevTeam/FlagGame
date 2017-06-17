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
package jp.llv.flaggame.api;

import java.util.Optional;
import jp.llv.flaggame.api.reception.ReceptionAPI;
import jp.llv.flaggame.database.Database;
import jp.llv.flaggame.game.GameManager;
import jp.llv.flaggame.profile.ProfileManager;
import jp.llv.flaggame.reception.ReceptionManager;
import jp.llv.flaggame.reception.fest.FestivalManager;
import org.bukkit.Server;
import org.bukkit.World;
import org.slf4j.Logger;
import syam.flaggame.FlagConfig;
import syam.flaggame.game.StageManager;
import syam.flaggame.player.PlayerManager;
import syam.flaggame.queue.ConfirmQueue;

/**
 *
 * @author toyblocks
 */
public interface FlagGameAPI {

    default Server getServer() {
        return getPlugin().getServer();
    }
    
    FlagGameRegistry getRegistry();

    PlayerManager getPlayers();

    ProfileManager getProfiles();

    ReceptionAPI getReceptions();

    GameManager getGames();

    StageManager getStages();

    FestivalManager getFestivals();

    Optional<Database> getDatabase();

    FlagConfig getConfig();

    default World getGameWorld() {
        return getServer().getWorld(getConfig().getGameWorld());
    }
    
    ConfirmQueue getConfirmQueue();
    
    Logger getLogger();
    
    FlagGamePlugin getPlugin();

}
