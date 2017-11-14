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
import jp.llv.flaggame.api.exception.PermissionException;

/**
 * Permission (Permission.java)
 *
 * @author syam(syamn)
 */
public enum Perms {

    RELOAD("reload"),
    TP("tp"),
    IGNORE_PROTECT("ignoreprotect"),
    SIGN("sign"),
    WALL_KICK("wallkick"),
    SUPER_JUMP("superjump"),
    AREA_DASHBOARD("area.dashboard"),
    AREA_DELETE("area.delete"),
    AREA_INIT("area.init"),
    AREA_LIST("area.list"),
    AREA_SELECT("area.select"),
    AREA_SET("area.set"),
    AREA_DATA_DELETE("area.data.delete"),
    AREA_DATA_LIST("area.data.list"),
    AREA_DATA_LOAD("area.data.load"),
    AREA_DATA_SAVE("area.data.save"),
    AREA_DATA_TIMING("area.data.timing"),
    AREA_MESSAGE_ADD("area.message.add"),
    AREA_MESSAGE_DELETE("area.message.delete"),
    AREA_MESSAGE_LIST("area.message.list"),
    AREA_MESSAGE_TIMING("area.message.timing"),
    AREA_PERMISSION_DASHBOARD("area.permission.dashboard"),
    AREA_PERMISSION_LIST("area.permission.list"),
    AREA_PERMISSION_SET("area.permission.set"),
    AREA_PERMISSION_TEST("area.permission.test"),
    GAME_CLOSE("game.close"),
    GAME_JOIN("game.join"),
    GAME_LEAVE_SPECTATE("game.leave.spectate"), GAME_LEAVE_READY("game.leave.ready"),
    GAME_LEAVE_FINISHED("game.leave.finished"),
    GAME_LIST("game.list"),
    GAME_READY("game.ready"),
    GAME_START("game.start"),
    GAME_WATCH("game.watch"),
    STAGE_CREATE("stage.create"),
    STAGE_DASHBOARD("stage.dashboard"),
    STAGE_DELETE("stage.delete"),
    STAGE_INFO("stage.info"),
    STAGE_LIST("stage.list"),
    STAGE_RATE("stage.rate"),
    STAGE_SAVE("stage.save"),
    STAGE_SELECT("stage.select"),
    STAGE_SET("stage.set"),
    STAGE_STATS("stage.stats"),
    STAGE_TAG("stage.tag"),
    KIT_CREATE("kit.create"),
    KIT_GET("kit.get"),
    KIT_DELETE("kit.delete"),
    FESTIVAL_MATCH_CREATE("festival.match.create"),
    FESTIVAL_MATCH_DELETE("festival.match.delete"),
    FESTIVAL_MATCH_TEAM("festival.match.team"),
    FESTIVAL_MATCH_LIST("festival.match.list"),
    FESTIVAL_CREATE("festival.create"),
    FESTIVAL_DASHBOARD("festival.dashboard"),
    FESTIVAL_DELETE("festival.delete"),
    FESTIVAL_LIST("festival.list"),
    FESTIVAL_SAVE("festival.save"),
    FESTIVAL_SELECT("festival.select"),
    FESTIVAL_SET_ENTRYFEE("festival.entryfee"),
    FESTIVAL_SET_PRIZE("festival.prize"),
    FESTIVAL_SET_TEAM("festival.set.team"),
    OBJECTIVE_LIST("objective.list"),
    OBJECTIVE_DELETE("objective.delete"),
    OBJECTIVE_SET("objective.set"),
    PLAYER_INFO("player.info"),
    PLAYER_STATS_SELF("player.stats.self"), PLAYER_STATS_OTHER("player.stats.other"),
    PLAYER_EXP_SELF("player.exp.self"), PLAYER_EXP_OTHER("player.exp.other"),
    PLAYER_VIBE_SELF("player.vibe.self"), PLAYER_VIBE_OTHER("player.vibe.other"),
    NICK_SET_SELF("player.nick.set.self"), NICK_SET_OTHER("player.nick.set.other"),
    NICK_LOCK_SELF("player.nick.lock.self"), NICK_LOCK_OTHER("player.nick.lock.other"),
    NICK_UNLOCK_SELF("player.nick.unlock.self"), NICK_UNLOCK_OTHER("player.nick.unlock.other"),
    NICK_SELECT("player.nick.select"),
    BIT_BALANCE_SELF("player.bit.balance.self"), BIT_BALANCE_OTHER("player.bit.balance.other"),
    BIT_PAY("player.bit.pay"),
    BIT_ADD("player.bit.add"),
    DEBUG("debug")
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
