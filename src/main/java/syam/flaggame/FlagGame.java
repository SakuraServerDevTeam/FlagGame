package syam.flaggame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import syam.flaggame.command.*;
import syam.flaggame.command.queue.ConfirmQueue;
import syam.flaggame.database.Database;
import syam.flaggame.enums.GameResult;
import syam.flaggame.game.Game;
import syam.flaggame.listener.*;
import syam.flaggame.manager.GameManager;
import syam.flaggame.manager.StageFileManager;
import syam.flaggame.manager.StageManager;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.PlayerManager;
import syam.flaggame.util.Actions;
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

    // ** Listener **
    private final FGPlayerListener playerListener = new FGPlayerListener(this);
    private final FGBlockListener blockListener = new FGBlockListener(this);
    private final FGEntityListener entityListener = new FGEntityListener(this);
    private final FGInventoryListener inventoryListener = new FGInventoryListener(this);

    // ** Commands **
    private static final List<BaseCommand> commands = new ArrayList<>();

    // ** Private classes **
    private ConfigurationManager config;
    private GameManager gm;
    private StageFileManager gfm;
    private Debug debug;
    private ConfirmQueue queue;

    // ** Variable **
    // プレイヤーデータベース
    private static Database database;

    // ** Instance **
    private static FlagGame instance;

    // Hookup plugins
    // public boolean usingDeathNotifier = false;
    private static Vault vault = null;
    private static Economy economy = null;
    private DynmapHandler dynmap = null;

    /**
     * プラグイン起動処理
     */
    @Override
    public void onEnable() {
        Debug.setStartupBeginTime();

        instance = this;

        PluginManager pm = getServer().getPluginManager();
        if (!initInternalAccessor()) {
            logger.severe("Your version of craftbukkit is not supported");
            pm.disablePlugin(this);
            return;
        }

        config = new ConfigurationManager(this);

        // loadconfig
        try {
            config.loadConfig(true);
        } catch (Exception ex) {
            logger.warning(logPrefix + "an error occured while trying to load the config file.");
            ex.printStackTrace();
        }

        // setup Debugger
        Debug.getInstance().init(logger, logPrefix, "plugins/FlagGame/debug.log", getConfigs().isDebug());
        debug = Debug.getInstance();

        // Vault
        debug.startTimer("vault");
        setupVault();
        debug.endTimer("vault");

        // プラグインを無効にした場合進まないようにする
        if (!pm.isPluginEnabled(this)) {
            return;
        }

        // 権限ハンドラセットアップ
        debug.startTimer("permission");
        Perms.setupPermissionHandler();
        debug.endTimer("permission");

        // Regist Listeners
        debug.startTimer("listeners");
        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(inventoryListener, this);
        debug.endTimer("listeners");

        // コマンド登録
        debug.startTimer("commands");
        registerCommands();
        queue = new ConfirmQueue(this);
        debug.endTimer("commands");

        // データベース連携
        debug.startTimer("database");
        database = new Database(this);
        database.createStructure();
        debug.endTimer("database");

        // マネージャ
        debug.startTimer("managers");
        gm = new GameManager(this);
        gfm = new StageFileManager(this); // 内部でDB使用
        debug.endTimer("managers");

        // ゲームデータ読み込み
        debug.startTimer("load games");
        gfm.loadStages();
        debug.endTimer("load games");

        // プレイヤー追加
        getServer().getOnlinePlayers().stream().forEach(PlayerManager::addPlayer);

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

        // 開始中のゲームをすべて終わらせる
        boolean readying = false;
        for (Game game : GameManager.getGames().values()) {
            if (game.isStarting()) {
                game.cancelTimerTask();
                game.finish(GameResult.STOP, null, "Unloading FlagGame Plugin");
                game.log("Game finished because disabling plugin..");
            } else if (game.isReady()) {
                game.message(msgPrefix + "&cあなたのエントリーはプラグインが無効になったため取り消されました");
                readying = true;
            }
        }
        if (readying) {
            Actions.broadcastMessage(msgPrefix + "&cプラグインが無効にされたため、参加受付中のゲームは削除されました");
        }

        // プレイヤープロファイルを保存
        PlayerManager.saveAll();

        // ゲームデータを保存
        if (gfm != null) {
            gfm.saveStages();
        }

        // ゲームステージプロファイルを保存
        StageManager.saveAll();

        // タスクをすべて止める
        getServer().getScheduler().cancelTasks(this);

        // dynmapフック解除
        if (getDynmap() != null) {
            getDynmap().disableDynmap();
        }

        // メッセージ表示
        PluginDescriptionFile pdfFile = this.getDescription();
        logger.log(Level.INFO, "[{0}] version {1} is disabled!", new Object[]{pdfFile.getName(), pdfFile.getVersion()});
    }

    private boolean initInternalAccessor() {
        String version = this.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf('.') + 1);

        return "v1_5_R2".equals(version.trim());
    }

    /**
     * Vaultプラグインにフック
     */
    private void setupVault() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Vault");
        if (plugin != null & plugin instanceof Vault) {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            // 経済概念のプラグインがロードされているかチェック
            if (economyProvider == null) {
                logger.warning(logPrefix + "Economy plugin not Fount. Disabling plugin.");
                getPluginLoader().disablePlugin(this);
                return;
            }

            try {
                vault = (Vault) plugin;
                economy = economyProvider.getProvider();
            } // 例外チェック
            catch (Exception e) {
                logger.warning(logPrefix + "Could NOT be hook to Vault. Disabling plugin.");
                getPluginLoader().disablePlugin(this);
                return;
            }
            logger.info(logPrefix + "Hooked to Vault!");
        } else {
            // Vaultが見つからなかった
            logger.warning(logPrefix + "Vault was NOT found! Disabling plugin.");
            getPluginLoader().disablePlugin(this);
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
            logger.warning(logPrefix + "Could not send metrics data!");
            ex.printStackTrace();
        }
    }

    /**
     * コマンドを登録
     */
    private void registerCommands() {
        // Intro Commands
        commands.add(new HelpCommand());
        commands.add(new InfoCommand());
        commands.add(new JoinCommand());
        commands.add(new WatchCommand());
        commands.add(new LeaveCommand());
        commands.add(new StatsCommand());
        commands.add(new TopCommand());
        commands.add(new ConfirmCommand());

        // Start Commands
        commands.add(new ReadyCommand());
        commands.add(new StartCommand());

        // Admin Commands
        commands.add(new StageCommand());
        commands.add(new SelectCommand());
        commands.add(new SetCommand());
        commands.add(new CheckCommand());
        commands.add(new TpCommand());
        commands.add(new SaveCommand());
        commands.add(new ReloadCommand());
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
                    return command.run(this, sender, args, commandLabel);
                }
            }
            // 有効コマンドなし ヘルプ表示
            new HelpCommand().run(this, sender, args, commandLabel);
            return true;
        }
        return false;
    }

    /* getter */
    /**
     * ゲームマネージャを返す
     *
     * @return GameManager
     */
    public GameManager getManager() {
        return gm;
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
    public static Database getDatabases() {
        return database;
    }

    /**
     * コマンドリストを返す
     *
     * @return {@code List<BaseCommand>}
     */
    public static List<BaseCommand> getCommands() {
        return commands;
    }

    /**
     * Confirmコマンドキューを返す
     *
     * @return ConfirmQueue
     */
    public ConfirmQueue getQueue() {
        return queue;
    }

    /**
     * Vaultを返す
     *
     * @return Vault
     */
    public Vault getVault() {
        return vault;
    }

    /**
     * Economyを返す
     *
     * @return Economy
     */
    public Economy getEconomy() {
        return economy;
    }

    /**
     * インスタンスを返す
     *
     * @return FlagGameインスタンス
     */
    public static FlagGame getInstance() {
        return instance;
    }
}
