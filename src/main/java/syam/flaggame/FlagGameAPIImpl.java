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
package syam.flaggame;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import jp.llv.flaggame.api.FlagGamePlugin;
import jp.llv.flaggame.database.Database;
import jp.llv.flaggame.game.GameManager;
import jp.llv.flaggame.profile.ProfileManager;
import jp.llv.flaggame.reception.ReceptionManager;
import org.slf4j.Logger;
import org.slf4j.impl.JDK14LoggerAdapter;
import jp.llv.flaggame.stage.StageManager;
import syam.flaggame.player.PlayerManager;
import syam.flaggame.queue.ConfirmQueue;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.event.EventAPI;
import jp.llv.flaggame.event.EventManager;
import jp.llv.flaggame.kit.KitManager;
import jp.llv.flaggame.menu.MenuManager;
import jp.llv.flaggame.trophy.TrophyManager;

/**
 *
 * @author toyblocks
 */
public class FlagGameAPIImpl implements FlagGameAPI {

    private final FlagGame plugin;
    private final ScheduledExecutorService executor;
    private final FlagDefaultRegistry registry;
    private final PlayerManager players;
    private final ProfileManager profiles;
    private final ReceptionManager receptions;
    private final GameManager games;
    private final StageManager stages;
    private final KitManager kits;
    private final TrophyManager trophies;
    private final MenuManager menus;
    private final EventManager events;
    private final ConfirmQueue confirmQueue;
    private final Logger logger;

    /*package*/ FlagGameAPIImpl(FlagGame plugin) {        
        this.plugin = plugin;
        int threads = plugin.getConfigs().getThreads();
        executor = Executors.newScheduledThreadPool(threads);
        
        this.registry = new FlagDefaultRegistry();
        this.players = new PlayerManager(this);
        this.profiles = new ProfileManager(this);
        this.receptions = new ReceptionManager(this);
        this.games = new GameManager(this);
        this.stages = new StageManager(this);
        this.kits = new KitManager();
        this.trophies = new TrophyManager(this);
        this.menus = new MenuManager(this);
        this.events = new EventManager(this);
        this.confirmQueue = new ConfirmQueue();
        
        try {
            Constructor<? extends Logger> loggerConstructor = JDK14LoggerAdapter.class.getDeclaredConstructor(java.util.logging.Logger.class);
            loggerConstructor.setAccessible(true);
            this.logger = loggerConstructor.newInstance(plugin.getLogger());
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public FlagDefaultRegistry getRegistry() {
        return registry;
    }

    @Override
    public PlayerManager getPlayers() {
        return players;
    }

    @Override
    public ProfileManager getProfiles() {
        return profiles;
    }

    @Override
    public ReceptionManager getReceptions() {
        return receptions;
    }

    @Override
    public GameManager getGames() {
        return games;
    }

    @Override
    public StageManager getStages() {
        return stages;
    }

    @Override
    public KitManager getKits() {
        return kits;
    }

    @Override
    public TrophyManager getTrophies() {
        return trophies;
    }
    
    @Override
    public MenuManager getMenus() {
        return menus;
    }

    @Override
    public EventAPI getEvents() {
        return events;
    }

    @Override
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    @Override
    public Optional<Database> getDatabase() {
        return plugin.getDatabases();
    }

    @Override
    public CachedFlagConfig getConfig() {
        return plugin.getConfigs();
    }

    @Override
    public ConfirmQueue getConfirmQueue() {
        return confirmQueue;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public FlagGamePlugin getPlugin() {
        return plugin;
    }

}
