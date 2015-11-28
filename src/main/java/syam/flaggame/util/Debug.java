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
package syam.flaggame.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Debug (Debug.java)
 * 
 * @author syam(syamn)
 */
public class Debug {
    private static Debug instance = null;

    private Logger log;
    private String logPrefix;
    private TextFileHandler logFile = null;
    private boolean debug = false;
    private Level oldLogLevel = null;

    // timer
    private static boolean isStartup = true;
    private static long startup = 0L;
    private long timeLog = 0L;

    /**
     * 初期化
     * 
     * @param log
     * @param logPrefix
     * @param isDebug
     */
    public void init(Logger log, String logPrefix, boolean isDebug) {
        this.log = log;

        this.logPrefix = logPrefix;
        if (logPrefix == null) {
            this.logPrefix = "[" + log.getName() + "] ";
        } else {
            if (logPrefix.endsWith(" ")) {
                this.logPrefix = logPrefix;
            } else {
                this.logPrefix = logPrefix + " ";
            }
        }

        setDebug(isDebug);
    }

    /**
     * 初期化
     * 
     * @param log
     * @param logPrefix
     * @param logFilePath
     * @param isDebug
     */
    public void init(Logger log, String logPrefix, String logFilePath, boolean isDebug) {
        this.init(log, logPrefix, isDebug);
        this.logFile = new TextFileHandler(logFilePath);
    }

    /**
     * デバッグログを出力する
     * 
     * @param args
     */
    public void debug(Object... args) {
        if (debug) {
            StringBuilder sb = new StringBuilder(logPrefix);

            for (int i = 0; i < args.length; i++) {
                sb.append(args[i]);
            }

            log.fine(sb.toString());

            // ファイル出力
            if (logFile != null) {
                final String logHeader = "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] ";
                try {
                    logFile.appendLine(logHeader + sb.toString());
                } catch (IOException ex) {
                    log.log(Level.WARNING, "{0}Could not write debug log file!", logPrefix);
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * コンソール出力するログレベルを変更
     * 
     * @param level
     */
    private void setConsoleLevel(Level level) {
        if (level == null) return;

        Handler handler = getConsoleHandler(log);
        if (handler != null) {
            handler.setLevel(level);
        }
    }

    /**
     * コンソールハンドラを返す
     * 
     * @param log
     * @return
     */
    private Handler getConsoleHandler(Logger log) {
        // コンソールハンドラを返す
        Handler[] handlers = log.getHandlers();
        for (Handler h : handlers) {
            if (h instanceof ConsoleHandler) { return h; }
        }

        // 親ロガーのコンソールハンドラを返す
        Logger parent = log.getParent();
        if (parent != null) {
            return getConsoleHandler(parent);
        } else {
            return null;
        }
    }

    /* getter / setter */

    /**
     * デバッグ状態を設定する
     * 
     * @param isDebug
     */
    public void setDebug(boolean isDebug) {
        // 未初期化状態
        if (log == null) return;

        if (this.debug == isDebug) return;

        this.debug = isDebug;

        if (isDebug) {
            oldLogLevel = log.getLevel();
            log.setLevel(Level.FINE);
            setConsoleLevel(Level.FINE);

            log.log(Level.INFO, "{0}DEBUG MODE ENABLED!", logPrefix);
        } else {
            if (oldLogLevel != null) {
                log.setLevel(oldLogLevel);
                setConsoleLevel(oldLogLevel);
            }
            log.log(Level.INFO, "{0}DEBUG MODE DISABLED!", logPrefix);
        }
    }

    /**
     * デバッグ状態を取得する
     * 
     * @return debug
     */
    public boolean isDebug() {
        return this.debug;
    }

    /**
     * デバッグ出力先ファイルを設定する null無効にする
     * 
     * @param filePath
     *            null or FilePath
     */
    public void setLogFile(String filePath) {
        this.logFile = new TextFileHandler(filePath);
    }

    /**
     * デバッグをファイルに出力するか取得する
     * 
     * @return boolean
     */
    public boolean isDebugToFile() {
        return logFile != null;
    }

    /**
     * インスタンスを返す
     * 
     * @return Debug
     */
    public static Debug getInstance() {
        if (instance == null) {
            // ダブルチェック
            synchronized (Debug.class) {
                if (instance == null) {
                    instance = new Debug();
                }
            }
        }

        return instance;
    }

    /* ********** static ********** */
    /**
     * プラグイン起動時に現在のミリ秒を記録する
     */
    public static void setStartupBeginTime() {
        if (startup <= 0L) {
            startup = System.currentTimeMillis();
        }
        isStartup = true;
    }

    /**
     * プラグイン起動完了時に経過時間を出力する
     */
    public void finishStartup() {
        isStartup = false;
        debug("[Timer] Total initialization time: " + (System.currentTimeMillis() - startup) + "ms");
    }

    /*
     * デバッグ時刻計測開始
     * 
     * @param actionName
     * @param useStartup
     */
    public void startTimer(String actionName) {
        timeLog = System.currentTimeMillis();

        if (isStartup) {
            debug("[Timer] starting " + actionName + " (t+" + (System.currentTimeMillis() - startup) + ")");
        } else {
            debug("[Timer] starting " + actionName);
        }
    }

    /**
     * デバッグ時刻計測終了
     * 
     * @param actionName
     */
    public void endTimer(String actionName) {
        debug("[Timer] " + actionName + " finished in " + (System.currentTimeMillis() - timeLog) + "ms");
    }
}
