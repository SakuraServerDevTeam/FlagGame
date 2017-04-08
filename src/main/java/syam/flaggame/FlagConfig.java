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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class FlagConfig {

    // Logger
    private static final String DEFAULT_DETAIL_DIRECTORY = "plugins/FlagGame/detail/";
    // Defaults
    private static final String DEFAULT_WORLD_NAME = "flag";
    private static final List<String> DEFAULT_DISABLED_COMMANDS = Arrays.asList("/spawn", "/home", "/setspawn");
    private static final List<String> DEFAULT_PERMISSIONS = Arrays.asList("vault", "superperms", "ops");

    private static final double VERSION = 0.7;

    private final FlagGame plugin;
    private final File pluginDir;
    // 設定項目
    /* Basic Configs */
    private int toolID = 269;
    private String gameWorld = DEFAULT_WORLD_NAME;
    private boolean isProtected = true;
    private boolean isDebug = false;
    private boolean useDynmap = false;
    
    private double wallKickPowerXZ = 0.75;
    private double wallKickPowerY = 0.6;
    
    /* Games Configs */
    private int startCountdownInSec = 10;
    private boolean useFlagEffects = true;
    private boolean deathWhenLogout = true;
    private boolean disableRegainHP = true;
    private boolean disableTeamPVP = true;
    private int godModeTime = 4;
    private List<String> disableCommands = new ArrayList<>(DEFAULT_DISABLED_COMMANDS);
    /* MySQL Configs */
    private String dbAddress = "localhost";
    private int dbPort = 3306;
    private String dbDatabaseName = "DatabaseName";
    private String dbUserName = "UserName";
    private String dbUserPassword = "UserPassword";
    /* Logging Configs */
    private String detailDirectory = DEFAULT_DETAIL_DIRECTORY;
    /* Permissions Configs */
    private List<String> permissions = new ArrayList<>(DEFAULT_PERMISSIONS);
    /* Score weight */
    private double scoreGameJoin, scoreGameExit,
            scoreCombatKill, scoreCombatDeath,
            scoreFlagPlace, scoreFlagBreak, scoreFlagLastPlace,
            scoreBannerDeploy, scoreBannerBreak, scoreBannerSteal, scoreBannerKeep,
            scoreNexusBreak,
            scoreRate;

    /**
     * コンストラクタ
     *
     * @param plugin
     */
    public FlagConfig(FlagGame plugin) {
        this.plugin = plugin;
        this.pluginDir = plugin.getDataFolder();
    }

    /**
     * 設定をファイルから読み込む
     *
     */
    public void loadConfig() {
        // ディレクトリ作成
        createDirs();

        // 設定ファイルパス取得
        File file = new File(pluginDir, "config.yml");
        // 無ければデフォルトコピー
        if (!file.exists()) {
            extractResource("/config.yml", pluginDir, false, true);
            plugin.getLogger().info("config.yml is not found! Created default config.yml!");
        }

        plugin.reloadConfig();

        // Check config.yml version
        checkver(plugin.getConfig().getDouble("Version", VERSION));

        FileConfiguration config = plugin.getConfig();
        
        /* Basic Configs */
        toolID = plugin.getConfig().getInt("ToolID", 269);
        gameWorld = plugin.getConfig().getString("WorldName", DEFAULT_WORLD_NAME);
        isProtected = plugin.getConfig().getBoolean("WorldProtect", true);
        isDebug = plugin.getConfig().getBoolean("Debug", false);
        useDynmap = plugin.getConfig().getBoolean("UseDynmap", false);
        wallKickPowerXZ = config.getDouble("WallKick.PowerXZ", wallKickPowerXZ);
        wallKickPowerY = config.getDouble("WallKick.PowerY", wallKickPowerY);
        /* Games Configs */
        startCountdownInSec = plugin.getConfig().getInt("StartCountdownInSec", 10);
        useFlagEffects = plugin.getConfig().getBoolean("UseFlagEffects", true);
        deathWhenLogout = plugin.getConfig().getBoolean("DeathWhenLogout", true);
        disableRegainHP = plugin.getConfig().getBoolean("DisableRegainHealth", true);
        disableTeamPVP = plugin.getConfig().getBoolean("DisableTeamPVP", true);
        godModeTime = plugin.getConfig().getInt("RespawnGodModeTime", 4);
        disableCommands = plugin.getConfig().getStringList("DisableCommands");
        /* MySQL Configs */
        dbAddress = plugin.getConfig().getString("Database.Address", "localhost");
        dbPort = plugin.getConfig().getInt("Database.Port", 27017);
        dbDatabaseName = plugin.getConfig().getString("Database.Name", "flaggame");
        dbUserName = plugin.getConfig().getString("Database.User_Name", null);
        dbUserPassword = plugin.getConfig().getString("Database.User_Password", null);
        /* Logging Configs */
        detailDirectory = plugin.getConfig().getString("DetailDirectory", DEFAULT_DETAIL_DIRECTORY);
        /* Permissions Configs */
        if (plugin.getConfig().get("Permissions") != null) {
            permissions = plugin.getConfig().getStringList("Permissions");
        } else {
            permissions = DEFAULT_PERMISSIONS;
        }
        /* Score weight configs */
        scoreGameJoin = plugin.getConfig().getDouble("score.game.join", 10.0);
        scoreGameExit = plugin.getConfig().getDouble("score.game.exit", -10.0);
        scoreCombatKill = plugin.getConfig().getDouble("score.combat.kill", 0.5);
        scoreCombatDeath = plugin.getConfig().getDouble("score.combat.death", 0.0);
        scoreFlagPlace = plugin.getConfig().getDouble("score.flag.place", 1.0);
        scoreFlagBreak = plugin.getConfig().getDouble("score.flag.break", 1.0);
        scoreFlagLastPlace = plugin.getConfig().getDouble("score.flag.last_place", 5.0);
        scoreBannerDeploy = plugin.getConfig().getDouble("score.banner.deploy", 3.0);
        scoreBannerKeep = plugin.getConfig().getDouble("score.banner.get", 0.1);
        scoreBannerSteal = plugin.getConfig().getDouble("score.banner.steal", 0.25);
        scoreBannerBreak = plugin.getConfig().getDouble("score.banner.keep", 3.0);
        scoreNexusBreak = plugin.getConfig().getDouble("score.nexus.break", 1.0);
        scoreRate = plugin.getConfig().getDouble("score.rate", 5.0);

        // ワールドチェック 見つからなければプラグイン無効化
        if (Bukkit.getWorld(gameWorld) == null) {
            plugin.getLogger().log(Level.WARNING, "World {0} is Not Found! Disabling plugin..", gameWorld);
            throw new IllegalStateException("Game world not found; configuration required");
        }

        // 詳細ログ用ディレクトリ作成
        createDir(new File(detailDirectory));
    }

    // 設定 getter ここから

    /* Basic Configs */
    public int getToolID() {
        return this.toolID;
    }

    public String getGameWorld() {
        return this.gameWorld;
    }

    public boolean isProtected() {
        return this.isProtected;
    }

    public boolean isDebug() {
        return this.isDebug;
    }

    public boolean getUseDynmap() {
        return this.useDynmap;
    }

    public double getWallKickPowerXZ() {
        return wallKickPowerXZ;
    }

    public double getWallKickPowerY() {
        return wallKickPowerY;
    }

    /* Games Configs */
    public int getStartCountdownInSec() {
        return this.startCountdownInSec;
    }

    public boolean getUseFlagEffects() {
        return this.useFlagEffects;
    }

    public boolean getDeathWhenLogout() {
        return this.deathWhenLogout;
    }

    public boolean getDisableRegainHP() {
        return this.disableRegainHP;
    }

    public boolean getDisableTeamPVP() {
        return this.disableTeamPVP;
    }

    public int getGodModeTime() {
        return this.godModeTime;
    }

    public List<String> getDisableCommands() {
        return this.disableCommands;
    }

    /* MySQL Configs */
    public String getDatabaseAddress() {
        return this.dbAddress;
    }

    public int getDatabasePort() {
        return this.dbPort;
    }

    public String getDatabaseDbname() {
        return this.dbDatabaseName;
    }

    public String getDatabaseUsername() {
        return this.dbUserName;
    }

    public String getDatabaseUserpass() {
        return this.dbUserPassword;
    }

    /* Logging Configs */
    public String getDetailDirectory() {
        return this.detailDirectory;
    }

    /* Permissions Configs */
    public List<String> getPermissions() {
        return this.permissions;
    }

    public double getScoreGameJoin() {
        return scoreGameJoin;
    }

    public double getScoreGameExit() {
        return scoreGameExit;
    }

    public double getScoreCombatKill() {
        return scoreCombatKill;
    }

    public double getScoreCombatDeath() {
        return scoreCombatDeath;
    }

    public double getScoreFlagPlace() {
        return scoreFlagPlace;
    }

    public double getScoreFlagBreak() {
        return scoreFlagBreak;
    }

    public double getScoreFlagLastPlace() {
        return scoreFlagLastPlace;
    }

    public double getScoreBannerDeploy() {
        return scoreBannerDeploy;
    }

    public double getScoreBannerBreak() {
        return scoreBannerBreak;
    }

    public double getScoreBannerKeep() {
        return scoreBannerKeep;
    }

    public double getScoreBannerSteal() {
        return scoreBannerSteal;
    }

    public double getScoreNexusBreak() {
        return scoreNexusBreak;
    }

    public long getScoreRate() {
        return (long) scoreRate;
    }

    // 設定 getter ここまで
    /**
     * 設定ファイルに設定を書き込む (コメントが消えるため使わない)
     *
     * @throws Exception
     */
    @Deprecated
    public void save() throws Exception {
        plugin.saveConfig();
    }

    /**
     * 必要なディレクトリ群を作成する
     */
    private void createDirs() {
        createDir(plugin.getDataFolder());
    }

    /**
     * 存在しないディレクトリを作成する
     *
     * @param dir File 作成するディレクトリ
     */
    private void createDir(File dir) {
        // 既に存在すれば作らない
        if (dir.isDirectory()) {
            return;
        }
        if (!dir.mkdir()) {
            plugin.getLogger().log(Level.WARNING, "Can''t create directory: {0}", dir.getName());
        }
    }

    /**
     * 設定ファイルのバージョンをチェックする
     *
     * @param ver
     */
    private void checkver(final double ver) {
        double configVersion = ver; // 設定ファイルのバージョン
        double nowVersion = VERSION; // プラグインのバージョン

        // 比較 設定ファイルのバージョンが古ければ config.yml を上書きする
        if (configVersion < nowVersion) {
            // 先に古い設定ファイルをリネームする
            String destName = "oldconfig-v" + configVersion + ".yml";
            String srcPath = new File(plugin.getDataFolder(), "config.yml").getPath();
            String destPath = new File(plugin.getDataFolder(), destName).getPath();
            try {
                copyTransfer(srcPath, destPath);
                plugin.getLogger().log(Level.INFO, "Copied old config.yml to {0}!", destName);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Cannot copy old config.yml!", ex);
            }

            // config.ymlと言語ファイルを強制コピー
            extractResource("/config.yml", plugin.getDataFolder(), true, false);

            plugin.getLogger().info("Deleted existing configuration file and generate a new one!");
        }
    }

    /**
     * リソースファイルをファイルに出力する
     *
     * @param from 出力元のファイルパス
     * @param to 出力先のファイルパス
     * @param force jarファイルの更新日時より新しいファイルが既にあっても強制的に上書きするか
     * @param checkenc 出力元のファイルを環境によって適したエンコードにするかどうか
     * @author syam
     */
    void extractResource(String from, File to, boolean force, boolean checkenc) {
        File of = to;

        // ファイル展開先がディレクトリならファイルに変換、ファイルでなければ返す
        if (to.isDirectory()) {
            String filename = new File(from).getName();
            of = new File(to, filename);
        } else if (!of.isFile()) {
            plugin.getLogger().log(Level.WARNING, "not a file:{0}", of);
            return;
        }

        // ファイルが既に存在する場合は、forceフラグがtrueでない限り展開しない
        if (of.exists() && !force) {
            return;
        }

        OutputStream out = null;
        InputStream in = null;
        InputStreamReader reader = null;
        OutputStreamWriter writer = null;
        try {
            // jar内部のリソースファイルを取得
            URL res = FlagGame.class.getResource(from);
            if (res == null) {
                plugin.getLogger().log(Level.WARNING, "Can''t find " + "{0} in plugin Jar file", from);
                return;
            }
            URLConnection resConn = res.openConnection();
            resConn.setUseCaches(false);
            in = resConn.getInputStream();

            if (in == null) {
                plugin.getLogger().log(Level.WARNING, "Can''t get input stream from {0}", res);
            } else // 出力処理 ファイルによって出力方法を変える
            {
                if (checkenc) {
                    // 環境依存文字を含むファイルはこちら環境

                    reader = new InputStreamReader(in, "UTF-8");
                    writer = new OutputStreamWriter(new FileOutputStream(of)); // 出力ファイルのエンコードは未指定
                    // =
                    // 自動で変わるようにする

                    int text;
                    while ((text = reader.read()) != -1) {
                        writer.write(text);
                    }
                } else {
                    // そのほか

                    out = new FileOutputStream(of);
                    byte[] buf = new byte[1024]; // バッファサイズ
                    int len;
                    while ((len = in.read(buf)) >= 0) {
                        out.write(buf, 0, len);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // 後処理
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    /**
     * コピー元のパス[srcPath]から、コピー先のパス[destPath]へファイルのコピーを行います。
     * コピー処理にはFileChannel#transferToメソッドを利用します。 コピー処理終了後、入力・出力のチャネルをクローズします。
     *
     * @param srcPath コピー元のパス
     * @param destPath コピー先のパス
     * @throws IOException 何らかの入出力処理例外が発生した場合
     */
    public static void copyTransfer(String srcPath, String destPath) throws IOException {
        FileChannel srcChannel = new FileInputStream(srcPath).getChannel();
        FileChannel destChannel = new FileOutputStream(destPath).getChannel();
        try {
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        } finally {
            srcChannel.close();
            destChannel.close();
        }
    }

    public static File getJarFile() {
        return new File("plugins", "FlagGame.jar");
    }
}
