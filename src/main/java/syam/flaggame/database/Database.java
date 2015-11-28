/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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
package syam.flaggame.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import syam.flaggame.FlagGame;

public class Database {
    // Logger
    public static final Logger log = FlagGame.logger;
    private static final String logPrefix = FlagGame.logPrefix;
    private static final String msgPrefix = FlagGame.msgPrefix;

    private static FlagGame plugin;

    private static String connectionString = null;
    private static String tablePrefix = null;
    private static Connection connection = null;
    private static long reconnectTimestamp = 0;

    /**
     * コンストラクタ
     * 
     * @param plugin
     *            FlagGameプラグインインスタンス
     */
    public Database(final FlagGame plugin) {
        this.plugin = plugin;

        // 接続情報読み込み
        connectionString = "jdbc:mysql://" + plugin.getConfigs().getMySQLaddress() + ":" + plugin.getConfigs().getMySQLport() + "/" + plugin.getConfigs().getMySQLdbname() + "?user=" + plugin.getConfigs().getMySQLusername() + "&password=" + plugin.getConfigs().getMySQLuserpass();
        tablePrefix = plugin.getConfigs().getMySQLtablePrefix();

        connect(); // 接続

        // ドライバを読み込む
        try {
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.getConnection(connectionString);
        } catch (ClassNotFoundException ex1) {
            log.severe(ex1.getLocalizedMessage());
        } catch (SQLException ex2) {
            log.severe(ex2.getLocalizedMessage());
            printErrors(ex2);
        }
    }

    /**
     * データベースに接続する
     */
    public static void connect() {
        try {
            log.info(logPrefix + "Attempting connection to MySQL..");

            Properties connectionProperties = new Properties();
            connectionProperties.put("autoReconnect", "false");
            connectionProperties.put("maxReconnects", "0");
            connection = DriverManager.getConnection(connectionString, connectionProperties);

            log.info(logPrefix + "Connected MySQL database!");
        } catch (SQLException ex) {
            log.severe(logPrefix + "Could not connect MySQL database!");
            ex.printStackTrace();
            printErrors(ex);
        }
    }

    /**
     * データベース構造を構築する
     */
    public void createStructure() {
        // ユーザーデータテーブル
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "users` (" + "`player_id` int(10) unsigned NOT NULL AUTO_INCREMENT," + // 割り当てるID
                "`uuid` binary(16) NOT NULL,"+
                "`player_name` varchar(32) NOT NULL," + // プレイヤー名
                "`lastjoingame` int(32) unsigned NOT NULL DEFAULT '0'," + // 最後にゲームに参加した時間
                "`status` int(2) NOT NULL DEFAULT '0'," + // ステータスデータ
                "PRIMARY KEY (`player_id`)," + "UNIQUE KEY `player_name` (`player_name`)" + ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");

        // 成績データテーブル
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "records` (" + "`player_id` int(10) unsigned NOT NULL," + // 割り当てられたプレイヤーID
                "`played` int(12) unsigned NOT NULL DEFAULT '0'," + // ゲーム参加回数
                "`exit` int(12) unsigned NOT NULL DEFAULT '0'," + // ゲーム途中離脱回数
                "`win` int(12) unsigned NOT NULL DEFAULT '0'," + // トータルwin数
                "`lose` int(12) unsigned NOT NULL DEFAULT '0'," + // トータルlose数
                "`draw` int(12) unsigned NOT NULL DEFAULT '0'," + // トータルdraw数
                "`place` int(12) unsigned NOT NULL DEFAULT '0'," + // トータルplace数
                "`break` int(12) unsigned NOT NULL DEFAULT '0'," + // トータルbreak数
                "`kill` int(12) unsigned NOT NULL DEFAULT '0'," + // トータルkill数
                "`death` int(12) unsigned NOT NULL DEFAULT '0'," + // トータルdeath数
                "PRIMARY KEY (`player_id`)" + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;");

        // ステージデータテーブル
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "stages` (" + "`stage_id` int(10) unsigned NOT NULL AUTO_INCREMENT," + // 割り当てるステージID
                "`stage_name` varchar(32) NOT NULL," + // プレイヤー名
                "`lastplayed` int(32) unsigned NOT NULL DEFAULT '0'," + // 最後にステージを開始した時間
                "`played` int(12) unsigned NOT NULL DEFAULT '0'," + // ステージ開始回数
                "`place` int(12) unsigned NOT NULL DEFAULT '0'," + // トータルplace数
                "`break` int(12) unsigned NOT NULL DEFAULT '0'," + // トータルbreak数
                "`kill` int(12) unsigned NOT NULL DEFAULT '0'," + // ステージkill数
                "`death` int(12) unsigned NOT NULL DEFAULT '0'," + // ステージdeath数
                "PRIMARY KEY (`stage_id`)," + "UNIQUE KEY `stage_name` (`stage_name`)" + ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");

        // tpで戻る地点のデータテーブル
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "tpbacks` (" + "`player_id` int(10) unsigned NOT NULL," + // 割り当てられたプレイヤーID
                "`world` varchar(255) NOT NULL," + // ワールド名
                "`x` double NOT NULL," + // x
                "`y` double NOT NULL," + // y
                "`z` double NOT NULL," + // z
                "`pitch` float NOT NULL," + // Pitch
                "`yaw` float NOT NULL," + // Yaw
                "PRIMARY KEY (`player_id`)" + ") ENGINE=MyISAM DEFAULT CHARSET=utf8");
    }

    /**
     * 書き込みのSQLクエリーを発行する
     * 
     * @param sql
     *            発行するSQL文
     * @return クエリ成功ならtrue、他はfalse
     */
    public boolean write(String sql) {
        return write(sql, new Object[0]);
    }

    public boolean write(String sql, Object... obj) {
        // 接続確認
        if (isConnected()) {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                // バインド
                if (obj != null && obj.length > 0) {
                    for (int i = 0; i < obj.length; i++) {
                        statement.setObject(i + 1, obj[i]);
                    }
                }
                statement.executeUpdate(); // 実行
            } catch (SQLException ex) {
                printErrors(ex);
                return false;
            } finally {
                // 後処理
                try {
                    if (statement != null) statement.close();
                } catch (SQLException ex) {
                    printErrors(ex);
                }
            }
            return true;
        }
        // 未接続
        else {
            attemptReconnect();
        }

        return false;
    }

    /**
     * 読み出しのSQLクエリーを発行する
     * 
     * @param sql
     *            発行するSQL文
     * @return SQLクエリで得られたデータ
     */
    public HashMap<Integer, ArrayList<String>> read(String sql) {
        return read(sql, new Object[0]);
    }

    public HashMap<Integer, ArrayList<String>> read(String sql, Object... obj) {
        ResultSet resultSet = null;
        HashMap<Integer, ArrayList<String>> rows = new HashMap<>();

        // 接続確認
        if (isConnected()) {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                // バインド
                if (obj != null && obj.length > 0) {
                    for (int i = 0; i < obj.length; i++) {
                        statement.setObject(i + 1, obj[i]);
                    }
                }
                resultSet = statement.executeQuery(); // 実行

                // 結果のレコード数だけ繰り返す
                while (resultSet.next()) {
                    ArrayList<String> column = new ArrayList<>();

                    // カラム内のデータを順にリストに追加
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        column.add(resultSet.getString(i));
                    }

                    // 返すマップにレコード番号とリストを追加
                    rows.put(resultSet.getRow(), column);
                }
            } catch (SQLException ex) {
                printErrors(ex);
            } finally {
                // 後処理
                try {
                    if (statement != null) statement.close();
                } catch (SQLException ex) {
                    printErrors(ex);
                }

                try {
                    if (resultSet != null) resultSet.close();
                } catch (SQLException ex) {
                    printErrors(ex);
                }
            }
        }
        // 未接続
        else {
            attemptReconnect();
        }

        return rows;
    }

    /**
     * int型の値を取得します
     * 
     * @param sql
     *            発行するSQL文
     * @return 最初のローにある数値
     */
    public int getInt(String sql) {
        return getInt(sql, new Object[0]);
    }

    public int getInt(String sql, Object... obj) {
        ResultSet resultSet = null;
        int result = 0;

        // 接続確認
        if (isConnected()) {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                // バインド
                if (obj != null && obj.length > 0) {
                    for (int i = 0; i < obj.length; i++) {
                        statement.setObject(i + 1, obj[i]);
                    }
                }
                resultSet = statement.executeQuery(); // 実行

                if (resultSet.next()) {
                    result = resultSet.getInt(1);
                } else {
                    // 結果がなければ0を返す
                    result = 0;
                }
            } catch (SQLException ex) {
                printErrors(ex);
            } finally {
                // 後処理
                try {
                    if (statement != null) statement.close();
                } catch (SQLException ex) {
                    printErrors(ex);
                }

                try {
                    if (resultSet != null) resultSet.close();
                } catch (SQLException ex) {
                    printErrors(ex);
                }
            }
        }
        // 未接続
        else {
            attemptReconnect();
        }

        return result;
    }

    /**
     * テーブル接頭語を返します
     * 
     * @return
     */
    public String getTablePrefix() {
        return plugin.getConfigs().getMySQLtablePrefix();
    }

    /**
     * 接続状況を返す
     * 
     * @return 接続中ならtrue、タイムアウトすればfalse
     */
    public static boolean isConnected() {
        if (connection == null) { return false; }

        try {
            return connection.isValid(3);
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * MySQLデータベースへ再接続を試みる
     */
    public static void attemptReconnect() {
        final int RECONNECT_WAIT_TICKS = 60000;
        final int RECONNECT_DELAY_TICKS = 1200;

        if (reconnectTimestamp + RECONNECT_WAIT_TICKS < System.currentTimeMillis()) {
            reconnectTimestamp = System.currentTimeMillis();
            log.severe(logPrefix + "Conection to MySQL was lost! Attempting to reconnect 60 seconds...");
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MySQLReconnect(plugin), RECONNECT_DELAY_TICKS);
        }
    }

    /**
     * エラーを出力する
     * 
     * @param ex
     */
    private static void printErrors(SQLException ex) {
        log.log(Level.WARNING, "SQLException:{0}", ex.getMessage());
        log.log(Level.WARNING, "SQLState:{0}", ex.getSQLState());
        log.log(Level.WARNING, "ErrorCode:{0}", ex.getErrorCode());
    }
}
