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
import jp.llv.flaggame.api.FlagGamePlugin;
import jp.llv.flaggame.database.Database;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.database.mongo.MongoDB;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.World;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import syam.flaggame.listener.*;

public class FlagGame extends JavaPlugin implements FlagGamePlugin {

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
    
    private static final long SLEEP_FOR_DATA_SAVING = 1000L;
    
    // ** Private classes **
    private CachedFlagConfig config;
    private Database database;
    private FlagGameAPIImpl api;
    // Hookup plugins
    private Economy economy = null;
    // ** Instance **
    private static FlagGame instance;

    /**
     * プラグイン起動処理
     */
    @Override
    public void onEnable() {
        instance = this;

        try {
            config = new CachedFlagConfig(this);
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

        setupVault();

        // プラグインを無効にした場合進まないようにする
        if (!getServer().getPluginManager().isPluginEnabled(this)) {
            return;
        }

        // データベース連携
        database = new MongoDB(this, this.config);
        try {
            database.connect();
        } catch (DatabaseException ex) {
            getLogger().log(Level.WARNING, "Failed to connect database!", ex);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.getServer().getScheduler().runTaskTimer(this, database::tryConnect, 600000L, 300000L);

        this.api = new FlagGameAPIImpl(this);

        registerListeners();

        // コマンド登録
        FlagCommandRegistry.initializeAll(api);
        this.getCommand("flag").setExecutor(FlagCommandRegistry.ROOT);

        // ゲームデータ読み込み
        database.loadStages(stage -> {
            api.getStages().addStage(stage.get());
            getLogger().log(Level.INFO, "Loaded stage ''{0}''", stage.get().getName());
            api.getProfiles().loadStageProfile(stage.get().getName());
        }, result -> {
            try {
                result.test();
                getLogger().log(Level.INFO, "Finished loading stages!");
            } catch (DatabaseException ex) {
                getLogger().log(Level.WARNING, "Failed to load stage!", ex);
            }
        });
        database.loadKits(kit -> {
            api.getKits().addKit(kit.get());
            getLogger().log(Level.INFO, "Loaded kit ''{0}''", kit.get().getName());
            api.getProfiles().loadStageProfile(kit.get().getName());
        }, result -> {
            try {
                result.test();
                getLogger().log(Level.INFO, "Finished loading kits!");
            } catch (DatabaseException ex) {
                getLogger().log(Level.WARNING, "Failed to load kits!", ex);
            }
        });
        
        api.getProfiles().loadPlayerProfiles(api.getPlayers(), false);

        // メッセージ表示
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().log(Level.INFO, "[{0}] version {1} is enabled!", new Object[]{pdfFile.getName(), pdfFile.getVersion()});
    }

    /**
     * プラグイン停止処理
     */
    @Override
    public void onDisable() {
        if (api != null) {
            api.getReceptions().closeAll("&cDisabled");
            api.getPlayers().saveAccounts();
        }

        // タスクをすべて止める
        getServer().getScheduler().cancelTasks(this);

        // メッセージ表示
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().log(Level.INFO, "[{0}] version {1} is disabled!", new Object[]{pdfFile.getName(), pdfFile.getVersion()});

        try {
            Thread.sleep(SLEEP_FOR_DATA_SAVING);
            this.database.close();
        } catch (DatabaseException | InterruptedException ex) {
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

    private void registerListeners() {
        Stream.<Function<? super FlagGameAPIImpl, ? extends Listener>>of(
                FGActionListener::new,
                FGPlayerListener::new,
                FGBlockListener::new,
                FGEntityListener::new,
                FGSignListener::new
        ).map(l -> l.apply(api)).forEach(l -> this.getServer().getPluginManager().registerEvents(l, this));
    }

    /**
     * 設定マネージャを返す
     *
     * @return ConfigurationManager
     */
    public CachedFlagConfig getConfigs() {
        return config;
    }

    public World getGameWorld() {
        return getServer().getWorld(config.getGameWorld());
    }

    /**
     * データベースを返す
     *
     * @return Database
     */
    public Optional<Database> getDatabases() {
        return this.database.isConnected() ? Optional.of(this.database) : Optional.empty();
    }

    public Optional<Economy> getEconomy() {
        return Optional.ofNullable(economy);
    }

    @Override
    public FlagGameAPIImpl getAPI() {
        return api;
    }

    @Deprecated
    public static FlagGame getInstance() {
        return instance;
    }
}
