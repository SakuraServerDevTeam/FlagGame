/* 
 * Copyright (c) 2015 SakuraServerDev All rights reserved.
 */
package syam.flaggame.permission;

import org.bukkit.permissions.Permissible;

import syam.flaggame.FlagGame;

/**
 * Permission (Permission.java)
 * 
 * @author syam(syamn)
 */
public enum Perms {
    /* 権限ノード */

    /* コマンド系 */
    // User Commands
    INFO("user.info"), JOIN("user.join"), LEAVE_GAME("user.leave.game"), LEAVE_READY("user.leave.ready"), LEAVE_SPECTATE("user.leave.spectate"), STATS_SELF("user.stats.self"), STATS_OTHER("user.stats.other"), TOP("user.top"), WATCH("user.watch"),

    // Admin Commands
    READY("admin.ready"), SELECT("admin.select"), START("admin.start"), TP("admin.tp"), ROLLBACK("admin.rollback"),

    // Setup Commands
    CREATE("admin.setup.create"), DELETE("admin.setup.delete"), SET("admin.setup.set"), CHECK("admin.setup.check"),

    // System Commands
    SAVE("admin.save"), RELOAD("admin.reload"),

    /* 特殊系 */
    IGNORE_PROTECT("ignoreWorldProtect"), IGNORE_INTERACT("ignoreInteractEvent"), SIGN("admin.sign"), ;

    // ノードヘッダー
    final String HEADER = "flag.";
    private final String node;

    /**
     * コンストラクタ
     * 
     * @param node
     *            権限ノード
     */
    Perms(final String node) {
        this.node = HEADER + node;
    }

    /*
     * 指定したプレイヤーが権限を持っているか
     * 
     * @param player
     *            Permissible. Player, CommandSender etc
     * @return boolean
     */
    public boolean has(final Permissible perm) {
        if (perm == null) return false;
        return handler.has(perm, this.node);
    }

    /*
     * 指定したプレイヤーが権限を持っているか(String)
     * 
     * @param player
     *            PlayerName
     * @return boolean
     */
    public boolean has(final String playerName) {
        if (playerName == null) return false;
        return has(FlagGame.getInstance().getServer().getPlayer(playerName));
    }

    /* ***** Static ***** */
    // 権限ハンドラ
    private static PermissionHandler handler = null;

    /**
     * PermissionHandlerセットアップ
     */
    public static void setupPermissionHandler() {
        if (handler == null) {
            handler = PermissionHandler.getInstance();
        }
        handler.setupPermissions(true);
    }
}
