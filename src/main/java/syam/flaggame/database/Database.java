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
package syam.flaggame.database;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    //Connect to mysql via jdbc
    private static final String JDBC_PREFIX = "jdbc:mysql://";
    private static final String RECORD_TABLE_NAME = "records";
    
    private final Logger logger;
    private final String jdbcUrl;
    private final String tablePrefix;
    
    public static final int BLOB_MAX_LENGTH = 65535;
    
    private Connection connection = null;
    
    public Database(Logger logger, String address, int port, String dbname, String tablePrefix, String username, String password) {
        Objects.requireNonNull(logger);
        Objects.requireNonNull(address);
        Objects.requireNonNull(dbname);
        Objects.requireNonNull(tablePrefix);
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        this.logger = logger;
        this.jdbcUrl = JDBC_PREFIX + address + ":" + port + "/" + dbname
                + "?user=" + username + "&password=" + password;
        this.tablePrefix = tablePrefix;
    }
    
    public void connect() throws SQLException {
        if (this.isConnected()) {
            return;
        }
        
        logger.info("Attempting connection to MySQL..");
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.getConnection(this.jdbcUrl);
        } catch (ClassNotFoundException ex) {
            throw new SQLException(ex);
        }
        
        Properties connectionProperties = new Properties();
        connectionProperties.put("autoReconnect", "false");
        connectionProperties.put("maxReconnects", "0");
        connection = DriverManager.getConnection(jdbcUrl, connectionProperties);
        
        logger.info("Connected MySQL database!");
    }
    
    public void createStructure() throws SQLException {
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + RECORD_TABLE_NAME + "` (" + //
                "`id` INT UNSIGNED AUTO_INCREMENT," +//レコードID
                "`timestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +//記録時刻
                "`type` TINYINT NOT NULL," +//レコード種別
                "`game` BINARY(16)," +//ゲームID
                "`player` BINARY(16)," +//プレイヤーID
                "`data` VARBINARY(255)," +//追加データ
                "PRIMARY KEY (`id`)" + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;");
    }
    
    private void write(String sql) throws SQLException {
        write(sql, new Object[0]);
    }
    
    private void write(String sql, Object... obj) throws SQLException {
        if (!this.isConnected()) {
            throw new SQLException("Not connected to the database!");
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // バインド
            if (obj != null && obj.length > 0) {
                for (int i = 0; i < obj.length; i++) {
                    statement.setObject(i + 1, obj[i]);
                }
            }
            statement.executeUpdate(); // 実行
        }
    }
    
    private ResultSet read(String sql) throws SQLException {
        return read(sql, new Object[0]);
    }
    
    private ResultSet read(String sql, Object... obj) throws SQLException {
        if (!this.isConnected()) {
            throw new SQLException("Not connected to the database!");
        }
        
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            if (obj != null && obj.length > 0) {
                for (int i = 0; i < obj.length; i++) {
                    statement.setObject(i + 1, obj[i]);
                }
            }
            return statement.executeQuery();
        }
    }
    
    public void write(RecordType type, UUID game, UUID player, byte[] optionalData) throws SQLException {
        this.write(
                "INSERT INTO " + tablePrefix + RECORD_TABLE_NAME + " (`type`, `game`, `player`, `data`)" +//
                "VALUES(?, ?, ?, ?)", type.getTypeCode(), game, player, optionalData);
    }
    
    public void write(RecordType type, UUID game, UUID player, UUID optionalUuid) throws SQLException {
        this.write(type, game, player, ByteBuffer.allocate(16)
                .putLong(optionalUuid.getMostSignificantBits())
                .putLong(optionalUuid.getLeastSignificantBits()).array());
    }
    
    public void write(RecordType type, UUID game, UUID player, double optionalDouble) throws SQLException {
        this.write(type, game, player, ByteBuffer.allocate(8).putDouble(optionalDouble).array());
    }
    
    public void write(RecordType type, UUID game, UUID player) throws SQLException {
        this.write(type, game, player, new byte[0]);
    }
    
    public int getRecordsMatchCount(RecordType type, UUID game, UUID player, byte[] optionalData) throws SQLException {
        ArrayList<Object> values = new ArrayList<>();
        ArrayList<String> where = new ArrayList<>();
        if (type != null) {
            values.add(type.getTypeCode());
            where.add("`type` = ?");
        }
        if (game != null) {
            values.add(game);
            where.add("`game` = ?");
        }
        if (player != null) {
            values.add(player);
            where.add("`player` = ?");
        }
        if (optionalData != null) {
            values.add(optionalData);
            where.add("`data` = ?");
        }
        try (ResultSet result = this.read(
                "SELECT COUNT(*) FROM " + tablePrefix + RECORD_TABLE_NAME +//
                " WHERE " + String.join(" AND ", where), values.toArray())) {
            result.next();
            return result.getInt(1);
        }
    }
    
    public boolean isConnected() {
        if (connection == null) {
            return false;
        }
        try {
            return connection.isValid(3);
        } catch (SQLException ex) {
            return false;
        }
    }
    
}
