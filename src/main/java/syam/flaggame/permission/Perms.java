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
package syam.flaggame.permission;

import org.bukkit.permissions.Permissible;
import syam.flaggame.exception.PermissionException;

/**
 * Permission (Permission.java)
 *
 * @author syam(syamn)
 */
public enum Perms {
    /* 権限ノード */

 /* コマンド系 */
    // User Commands
    SINFO("user.stageinfo"), LIST("user.list"), JOIN("user.join"), LEAVE_GAME("user.leave.game"), LEAVE_READY("user.leave.ready"), LEAVE_SPECTATE("user.leave.spectate"), WATCH("user.watch"),
    // Admin Commands
    PINFO("user.playerinfo"), READY("admin.ready"), CLOSE("admin.close"), SELECT("admin.select"), START("admin.start"), TP("admin.tp"), ROLLBACK("admin.rollback"),
    // Setup Commands
    CREATE("admin.setup.create"), DELETE("admin.setup.delete"), SET("admin.setup.set"), CHECK("admin.setup.check"),
    // System Commands
    SAVE("admin.save"), RELOAD("admin.reload"),
    /* 特殊系 */
    IGNORE_PROTECT("ignoreWorldProtect"), SIGN("admin.sign"),
    //new Configuration
    STAGE_CONFIG_CHECK("config.stage.check"), STAGE_CONFIG_SET("config.stage.set"),
    // stats 
    PLAYER_STAT_SELF("user.stat.self"), PLAYER_STAT_OTHER("user.stat.other"),
    PLAYER_EXP_SELF("user.exp.self"), PLAYER_EXP_OTHER("user.exp.other"), 
    PLAYER_VIBE_SELF("user.vibe.self"), PLAYER_VIBE_OTHER("user.vibe.other"),
    STAGE_STAT("stage.stat"),
    ;

    // ノードヘッダー
    final String HEADER = "flag.";
    private final String node;

    /**
     * コンストラクタ
     *
     * @param node 権限ノード
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
        if (perm == null) {
            return false;
        }
        return perm.hasPermission(this.node);
    }
    
    public void requireTo(Permissible perm) throws PermissionException {
        if (perm == null || !perm.hasPermission(node)) {
            throw new PermissionException("Not enough permission");
        }
    }

}
