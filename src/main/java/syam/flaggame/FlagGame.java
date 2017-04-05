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
package syam.flaggame;

import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;
import jp.llv.flaggame.database.Database;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.database.mongo.MongoDB;
import jp.llv.flaggame.game.GameManager;
import jp.llv.flaggame.profile.ProfileManager;
import jp.llv.flaggame.reception.EvenRequiredRealtimeTeamingReception;
import jp.llv.flaggame.reception.RealtimeTeamingReception;
import jp.llv.flaggame.reception.ReceptionManager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.World;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import syam.flaggame.queue.ConfirmQueue;
import syam.flaggame.listener.*;
import syam.flaggame.game.StageManager;
import syam.flaggame.player.PlayerManager;
import syam.flaggame.util.Debug;
import syam.flaggame.util.DynmapHandler;

public class FlagGame extends JavaPlugin {

    /*
     * TODO:
     * 
     * タイマー、状況表示用の看板
     * 
     * 定期的な状況告知
     * 
     * 受付中のゲームを定期アナウンス(など)
     * 
     * 参加チームの選択
     */

    // ** Private classes **
    private FlagConfig config;
    private Debug debug;
    private ConfirmQueue queue;

    private PlayerManager players;
    private ReceptionManager receptions;
    private GameManager games;
    private StageManager stages;
    private ProfileManager profiles;

    // ** Variable **
    // プレイヤーデータベース
    private Database database;

    // ** Instance **
    private static FlagGame instance;

    // Hookup plugins
    private Economy economy = null;
    private DynmapHandler dynmap = null;

    /**
     * プラグイン起動処理
     */
    @Override
    public void onEnable() {
        Debug.setStartupBeginTime();

        instance = this;

        PluginManager pluginManager = getServer().getPluginManager();

        try {
            config = new FlagConfig(this);
        } catch (IllegalStateException ex) {
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        // loadconfig
        try {
            config.loadConfig();
        } catch (Exception ex) {
            this.getLogger().log(Level.WARNING, "an error occured while trying to load the config file.", ex);
        }

        // setup Debugger
        Debug.getInstance().init(getLogger(), "plugins/FlagGame/debug.log", getConfigs().isDebug());
        debug = Debug.getInstance();

        // Vault
        debug.startTimer("vault");
        setupVault();
        debug.endTimer("vault");

        // プラグインを無効にした場合進まないようにする
        if (!pluginManager.isPluginEnabled(this)) {
            return;
        }

        // Regist Listeners
        debug.startTimer("listeners");
        registerListeners();
        debug.endTimer("listeners");

        // コマンド登録
        debug.startTimer("commands");
        FlagCommandRegistry.ROOT.initialize(this);
        this.getCommand("flag").setExecutor(FlagCommandRegistry.ROOT);
        queue = new ConfirmQueue(this);
        debug.endTimer("commands");

        // データベース連携
        debug.startTimer("database");
        database = new MongoDB(this, this.config);
        try {
            database.connect();
        } catch (DatabaseException ex) {
            getLogger().log(Level.WARNING, "Failed to connect database!", ex);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.getServer().getScheduler().runTaskTimer(this, database::tryConnect, 600000L, 300000L);
        debug.endTimer("database");

        debug.startTimer("managers");
        players = new PlayerManager(this);
        profiles = new ProfileManager(this);
        receptions = new ReceptionManager(this);
        receptions.addType("rt", RealtimeTeamingReception::new);
        receptions.addType("ert", EvenRequiredRealtimeTeamingReception::new);
        games = new GameManager(this);
        stages = new StageManager(this);
        debug.endTimer("managers");

        // ゲームデータ読み込み
        debug.startTimer("load games");
        database.loadStages(stage -> {
            stages.addStage(stage.get());
            getLogger().log(Level.INFO, "Loaded stage ''{0}''", stage.get().getName());
            profiles.loadStageProfile(stage.get().getName());
        }, result -> {
            try {
                result.test();
                getLogger().log(Level.INFO, "Finished loading stages!");
            } catch (DatabaseException ex) {
                getLogger().log(Level.WARNING, "Failed to load stage!", ex);
            }
        });
        debug.endTimer("load games");

        // dynmapフック
        debug.startTimer("dynmap");
        setupDynmap();
        debug.endTimer("dynmap");

        // メッセージ表示
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().log(Level.INFO, "[{0}] version {1} is enabled!", new Object[]{pdfFile.getName(), pdfFile.getVersion()});

        debug.finishStartup();
    }

    /**
     * プラグイン停止処理
     */
    @Override
    public void onDisable() {
        if (this.receptions != null) {
            this.receptions.closeAll("&cDisabled");
        }

        // タスクをすべて止める
        getServer().getScheduler().cancelTasks(this);

        // dynmapフック解除
        if (getDynmap() != null) {
            getDynmap().disableDynmap();
        }

        // メッセージ表示
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().log(Level.INFO, "[{0}] version {1} is disabled!", new Object[]{pdfFile.getName(), pdfFile.getVersion()});

        try {
            this.database.close();
        } catch (DatabaseException ex) {
        }
    }

    /**
     * Vaultプラグインにフック
     */
    private void setupVault() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Vault");
        if (plugin != null) {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            // 経済概念のプラグインがロードされているかチェック
            if (economyProvider == null) {
                return;
            }

            try {
                economy = economyProvider.getProvider();
            } // 例外チェック
            catch (Exception e) {
                getLogger().warning("Could NOT be hook to Vault.");
                return;
            }
            getLogger().info("Hooked to Vault!");
        } else {
            // Vaultが見つからなかった
            getLogger().warning("Vault was NOT found!");
        }
    }

    /**
     * Dynmapプラグインにフック
     */
    private void setupDynmap() {
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            dynmap = new DynmapHandler(FlagGame.getInstance());
            if (FlagGame.getInstance().getConfigs().getUseDynmap()) {
                dynmap.init();
            }
        }, 20L);
    }

    private void registerListeners() {
        Stream.<Function<FlagGame, ? extends Listener>>of(
                FGPlayerListener::new,
                FGBlockListener::new,
                FGEntityListener::new,
                FGSignListener::new
        ).map(l -> l.apply(this)).forEach(l -> this.getServer().getPluginManager().registerEvents(l, this));
    }

    /**
     * 設定マネージャを返す
     *
     * @return ConfigurationManager
     */
    public FlagConfig getConfigs() {
        return config;
    }

    public World getGameWorld() {
        return getServer().getWorld(config.getGameWorld());
    }

    /**
     * dynmapハンドラを返す
     *
     * @return DynmapHandler
     */
    public DynmapHandler getDynmap() {
        return dynmap;
    }

    /**
     * データベースを返す
     *
     * @return Database
     */
    public Optional<Database> getDatabases() {
        return this.database.isConnected() ? Optional.of(this.database) : Optional.empty();
    }

    /**
     * Confirmコマンドキューを返す
     *
     * @return ConfirmQueue
     */
    public ConfirmQueue getConfirmQueue() {
        return queue;
    }

    public Optional<Economy> getEconomy() {
        return Optional.ofNullable(economy);
    }

    public PlayerManager getPlayers() {
        return players;
    }

    public ProfileManager getProfiles() {
        return profiles;
    }

    public ReceptionManager getReceptions() {
        return receptions;
    }

    public GameManager getGames() {
        return games;
    }

    public StageManager getStages() {
        return stages;
    }

    public static FlagGame getInstance() {
        return instance;
    }
}
