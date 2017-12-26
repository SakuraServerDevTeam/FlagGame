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
import java.util.concurrent.ExecutorService;
import jp.llv.flaggame.api.event.EventAPI;
import jp.llv.flaggame.api.game.GameAPI;
import jp.llv.flaggame.api.kit.KitAPI;
import jp.llv.flaggame.api.menu.MenuAPI;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.PlayerAPI;
import jp.llv.flaggame.api.profile.ProfileAPI;
import jp.llv.flaggame.api.reception.ReceptionAPI;
import jp.llv.flaggame.api.stage.StageAPI;
import jp.llv.flaggame.database.Database;
import org.bukkit.Server;
import org.bukkit.World;
import org.slf4j.Logger;
import jp.llv.flaggame.api.queue.ConfirmQueueAPI;
import jp.llv.flaggame.api.trophy.TrophyAPI;

/**
 *
 * @author toyblocks
 */
public interface FlagGameAPI {

    default Server getServer() {
        return getPlugin().getServer();
    }

    FlagGameRegistry getRegistry();

    PlayerAPI<? extends GamePlayer> getPlayers();

    ProfileAPI getProfiles();

    ReceptionAPI getReceptions();

    GameAPI getGames();

    StageAPI getStages();

    KitAPI getKits();
    
    TrophyAPI getTrophies();
    
    MenuAPI getMenus();
    
    EventAPI getEvents();
    
    ExecutorService getExecutor();

    Optional<Database> getDatabase();

    FlagConfig getConfig();

    default World getGameWorld() {
        return getServer().getWorld(getConfig().getGameWorld());
    }

    ConfirmQueueAPI getConfirmQueue();

    Logger getLogger();

    FlagGamePlugin getPlugin();

}
