/* 
 * Copyright (C) 2017 Toyblocks, SakuraServerDev
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
package jp.llv.flaggame.game.basic;

import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import syam.flaggame.FlagGame;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class BGEntityListener extends BGListener {

    private final FlagGame plugin;
    private final Collection<GamePlayer> players;

    public BGEntityListener(FlagGame plugin, BasicGame game) {
        super(game);
        this.plugin = plugin;
        this.players = game.getReception().getPlayers();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = null;
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Object shooter = ((Projectile) event.getDamager()).getShooter();
            if (shooter instanceof Player) {
                damager = (Player) shooter;
            }
        }
        if (damager == null) {
            return;
        }

        Player player = (Player) event.getEntity();
        GamePlayer gp = this.plugin.getPlayers().getPlayer(player);
        GamePlayer gd = this.plugin.getPlayers().getPlayer(damager);
        if (!this.players.contains(gp) || !this.players.contains(gd)) {
            return;
        }

        if (this.game.getStage().getBase(gp.getTeam().get().getColor()).isIn(player.getLocation())) {
            event.setCancelled(true);
            if (!this.game.getStage().getBase(gd.getTeam().get().getColor()).isIn(damager.getLocation())) {
                damager.damage(event.getDamage(), player);
            }
            return;
        }

        if (!plugin.getConfigs().getDisableTeamPVP()) {
            return;
        }

        if (!gp.getTeam().isPresent() || !gd.getTeam().isPresent()) {
            return;
        }

        if (gp.getTeam().get() == gd.getTeam().get()) {
            event.setDamage(0D);
            event.setCancelled(true);
        }
    }

}
