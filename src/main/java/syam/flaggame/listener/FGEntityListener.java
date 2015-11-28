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
package syam.flaggame.listener;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import syam.flaggame.FlagGame;
import syam.flaggame.player.GamePlayer;

public class FGEntityListener implements Listener {
    public static final Logger log = FlagGame.logger;

    private final FlagGame plugin;

    public FGEntityListener(final FlagGame plugin) {
        this.plugin = plugin;
    }

    /* 登録するイベントはここから下に */

    // プレイヤーがダメージを受けた
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        // ゲーム用ワールドでなければ返す
        if (entity.getWorld() != Bukkit.getWorld(plugin.getConfigs().getGameWorld())) return;

        Player damaged = null;
        Player attacker = null;

        // プレイヤー対プレイヤーの直接攻撃
        if ((event.getCause() == DamageCause.ENTITY_ATTACK) && (entity instanceof Player) && (event.getDamager() instanceof Player)) {
            damaged = (Player) entity; // 攻撃された人
            attacker = (Player) event.getDamager(); // 攻撃した人
        }
        // 矢・雪球・卵など
        else if ((event.getCause() == DamageCause.PROJECTILE) && (entity instanceof Player) && (event.getDamager() instanceof Projectile)) {
            // プレイヤーが打ったもの
            if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                damaged = (Player) entity; // 攻撃された人
                attacker = (Player) ((Projectile) event.getDamager()).getShooter(); // 攻撃した人
            }
        }

        if (damaged == null || attacker == null) return;

        // 設定確認 チーム内PVPを無効にする設定が無効であれば何もしない
        if (!plugin.getConfigs().getDisableTeamPVP()) return;

        GamePlayer gAttacker = plugin.getPlayers().getPlayer(attacker),
                gDamaged = plugin.getPlayers().getPlayer(damaged);
        if (!gAttacker.getTeam().equals(gDamaged.getTeam())) {
            return;
        }
        event.setCancelled(true);
    }

    // 体力が回復した
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
        // ゲーム用ワールドでなければ返す
        if (event.getEntity().getWorld() != Bukkit.getWorld(plugin.getConfigs().getGameWorld())) { return; }

        // 設定確認、プレイヤーならイベントキャンセル
        if (plugin.getConfigs().getDisableRegainHP()) {
            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }
}
