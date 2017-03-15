/* 
 * Copyright (C) 2017 Syamn, SakuraServerDev
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import jp.llv.flaggame.database.Database;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.database.MongoDB;
import jp.llv.flaggame.game.GameManager;
import jp.llv.flaggame.reception.EvenRequiredRealtimeTeamingReception;
import jp.llv.flaggame.reception.RealtimeTeamingReception;
import jp.llv.flaggame.reception.ReceptionManager;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import syam.flaggame.command.*;
import syam.flaggame.command.queue.ConfirmQueue;
import syam.flaggame.listener.*;
import syam.flaggame.game.StageFileManager;
import syam.flaggame.game.StageManager;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.PlayerManager;
import syam.flaggame.util.Debug;
import syam.flaggame.util.DynmapHandler;
import syam.flaggame.util.Metrics;

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
    // ** Logger **
    public final static Logger logger = Logger.getLogger("FlagGame");
    public final static String logPrefix = "[FlagGame] ";
    public final static String msgPrefix = "&6[FlagGame] &f";

    // ** Commands **
    private final List<BaseCommand> commands = new ArrayList<>();

    // ** Private classes **
    private ConfigurationManager config;
    private StageFileManager gfm;
    private Debug debug;
    private ConfirmQueue queue;

    private PlayerManager pm;
    private ReceptionManager rm;
    private GameManager gm;
    private StageManager sm;

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
            config = new ConfigurationManager(this);
        } catch (IllegalStateException ex) {
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        // loadconfig
        try {
            config.loadConfig(true);
        } catch (Exception ex) {
            this.getLogger().log(Level.WARNING, "an error occured while trying to load the config file.", ex);
        }

        // setup Debugger
        Debug.getInstance().init(logger, logPrefix, "plugins/FlagGame/debug.log", getConfigs().isDebug());
        debug = Debug.getInstance();

        // Vault
        debug.startTimer("vault");
        setupVault();
        debug.endTimer("vault");

        // プラグインを無効にした場合進まないようにする
        if (!pluginManager.isPluginEnabled(this)) {
            return;
        }

        // 権限ハンドラセットアップ
        debug.startTimer("permission");
        Perms.setupPermissionHandler();
        debug.endTimer("permission");

        // Regist Listeners
        debug.startTimer("listeners");
        registerListeners();
        debug.endTimer("listeners");

        // コマンド登録
        debug.startTimer("commands");
        registerCommands();
        queue = new ConfirmQueue(this);
        debug.endTimer("commands");

        // データベース連携
        /*debug.startTimer("database");
        database = new MongoDB(this.config);
        try {
            database.connect();
        } catch (DatabaseException ex) {
        }
        this.getServer().getScheduler().runTaskTimer(this, database::tryConnect, 600000L, 300000L);
        debug.endTimer("database");*/

        debug.startTimer("managers");
        gfm = new StageFileManager(this);

        pm = new PlayerManager(this);
        rm = new ReceptionManager(this);
        rm.addType("rt", RealtimeTeamingReception::new);
        rm.addType("ert", EvenRequiredRealtimeTeamingReception::new);
        gm = new GameManager(this);
        sm = new StageManager();
        debug.endTimer("managers");

        // ゲームデータ読み込み
        debug.startTimer("load games");
        gfm.loadStages();
        debug.endTimer("load games");

        // dynmapフック
        debug.startTimer("dynmap");
        setupDynmap();
        debug.endTimer("dynmap");

        // Metrics
        debug.startTimer("metrics");
        setupMetrics();
        debug.endTimer("metrics");

        // メッセージ表示
        PluginDescriptionFile pdfFile = this.getDescription();
        logger.log(Level.INFO, "[{0}] version {1} is enabled!", new Object[]{pdfFile.getName(), pdfFile.getVersion()});

        debug.finishStartup();
    }

    /**
     * プラグイン停止処理
     */
    @Override
    public void onDisable() {
        commands.clear();

        if (this.rm != null) {
            this.rm.closeAll("&cDisabled");
        }

        // ゲームデータを保存
        if (gfm != null) {
            gfm.saveStages();
        }

        // タスクをすべて止める
        getServer().getScheduler().cancelTasks(this);

        // dynmapフック解除
        if (getDynmap() != null) {
            getDynmap().disableDynmap();
        }

        // メッセージ表示
        PluginDescriptionFile pdfFile = this.getDescription();
        logger.log(Level.INFO, "[{0}] version {1} is disabled!", new Object[]{pdfFile.getName(), pdfFile.getVersion()});
        
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
                logger.warning(logPrefix + "Could NOT be hook to Vault.");
                return;
            }
            logger.info(logPrefix + "Hooked to Vault!");
        } else {
            // Vaultが見つからなかった
            logger.warning(logPrefix + "Vault was NOT found!");
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

    /**
     * Metricsセットアップ
     */
    private void setupMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException ex) {
            this.getLogger().log(Level.WARNING, "Could not send metrics data!", ex);
        }
    }

    private void registerCommands() {
        Stream.<Function<FlagGame, ? extends BaseCommand>>of(
                HelpCommand::new,
                SInfoCommand::new,
                PInfoCommand::new,
                ListCommand::new,
                JoinCommand::new,
                WatchCommand::new,
                LeaveCommand::new,
                ConfirmCommand::new,
                ReadyCommand::new,
                StartCommand::new,
                CloseCommand::new,
                StageCommand::new,
                SelectCommand::new,
                SetCommand::new,
                CheckCommand::new,
                TpCommand::new,
                SaveCommand::new,
                ReloadCommand::new
        ).map(f -> f.apply(this)).forEach(this.commands::add);
    }

    private void registerListeners() {
        Stream.<Function<FlagGame, ? extends Listener>>of(
                FGPlayerListener::new,
                FGBlockListener::new,
                FGEntityListener::new
        ).map(l -> l.apply(this)).forEach(l -> this.getServer().getPluginManager().registerEvents(l, this));
    }

    /*
     * コマンドが呼ばれた
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
        if (cmd.getName().equalsIgnoreCase("flag")) {
            if (args.length == 0) {
                // 引数ゼロはヘルプ表示
                args = new String[]{"help"};
            }

            outer:
            for (BaseCommand command : commands.toArray(new BaseCommand[0])) {
                String[] cmds = command.getName().split(" ");
                for (int i = 0; i < cmds.length; i++) {
                    if (i >= args.length || !cmds[i].equalsIgnoreCase(args[i])) {
                        continue outer;
                    }
                    // 実行
                    return command.run(sender, args, commandLabel);
                }
            }
            // 有効コマンドなし ヘルプ表示
            new HelpCommand(this).run(sender, args, commandLabel);
            return true;
        }
        return false;
    }

    /**
     * ゲームファイルマネージャを返す
     *
     * @return GameManager
     */
    public StageFileManager getFileManager() {
        return gfm;
    }

    /**
     * 設定マネージャを返す
     *
     * @return ConfigurationManager
     */
    public ConfigurationManager getConfigs() {
        return config;
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
     * コマンドリストを返す
     *
     * @return {@code List<BaseCommand>}
     */
    public Collection<BaseCommand> getCommands() {
        return Collections.unmodifiableCollection(this.commands);
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
        return pm;
    }

    public ReceptionManager getReceptions() {
        return rm;
    }

    public GameManager getGames() {
        return gm;
    }

    public StageManager getStages() {
        return sm;
    }

    public static FlagGame getInstance() {
        return instance;
    }
}
