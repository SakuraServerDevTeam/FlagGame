/* 
 * Copyright (C) 2015 Toyblocks, SakuraServerDev
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
import jp.llv.flaggame.reception.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Openable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import syam.flaggame.FlagGame;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author Toyblocks
 */
public class BGPlayerListener extends BGListener {

    private final FlagGame plugin;
    private final Collection<GamePlayer> players;

    public BGPlayerListener(FlagGame plugin, BasicGame game) {
        super(game);
        this.plugin = plugin;
        this.players = game.getReception().getPlayers();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        Block block = event.getClickedBlock();
        if (!(block.getState() instanceof InventoryHolder)
                && !(block.getState().getData() instanceof Openable)) {
            return;
        }


        if (!canUseBlockAt(gplayer, event.getClickedBlock().getLocation())) {
            gplayer.sendMessage("&cここは敵拠点です!");
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @SuppressWarnings("deprecation")
    public void on(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        TeamColor color = gplayer.getTeam().get().getColor();
        gplayer.sendMessage("&c[*]&6このゲームはあと &a" +Actions.getTimeString(this.game.getRemainTime()) + "&6 残っています！");
        Location loc = this.game.getStage().getSpawn(color);
        event.setRespawnLocation(loc);
        player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short) 0, color.getBlockData()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.plugin.getConfigs().getGodModeTime(), 4));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        String cmd = event.getMessage().split(" ")[0];

        for (String s : plugin.getConfigs().getDisableCommands()) {
            if (s.equalsIgnoreCase(cmd)) {
                event.setCancelled(true);
                gplayer.sendMessage("&cこのコマンドは試合中に使えません！");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();
        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        event.setDeathMessage(null);
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null && helmet.getType() == Material.WOOL) {
            player.getInventory().setHelmet(null);
        }

        Player killer;
        String weapon;

        EntityDamageEvent cause = event.getEntity().getLastDamageCause();
        if (cause == null) {
            killer = null;
            weapon = "呪い";
        } else if (!(cause instanceof EntityDamageByEntityEvent)) {
            killer = null;
            weapon = cause.getCause().toString();
        } else {
            Entity killerEnt = ((EntityDamageByEntityEvent) cause).getDamager();
            if (killerEnt instanceof Player) {
                killer = (Player) killerEnt;
                ItemStack is = killer.getItemInHand();
                weapon = is == null ? null
                        : is.getItemMeta().hasDisplayName()
                                ? is.getItemMeta().getDisplayName() : is.getType().name();
            } else if (killerEnt instanceof Projectile) {
                Projectile p = (Projectile) killerEnt;
                weapon = p.getCustomName();
                weapon = weapon != null ? weapon : p.getName();
                if (p.getShooter() instanceof Player) {
                    killer = (Player) p.getShooter();
                } else {
                    killer = null;
                }
            } else {
                killer = null;
                weapon = killerEnt.getCustomName();
                weapon = weapon != null ? weapon : killerEnt.getName();
            }
        }

        GamePlayer gkiller = this.plugin.getPlayers().getPlayer(killer);
        if (!gkiller.getGame().isPresent() || gkiller.getGame().get() != this.game) {
            gkiller = null;
        }

        Team killedTeam = gplayer.getTeam().get();
        Team killerTeam = gkiller != null ? gkiller.getTeam().get() : null;

        String message;
        game.addDeathCount(gplayer);
        game.Result
        gplayer.getProfile().addDeath();
        if (gplayer == gkiller || gkiller == null || killerTeam == null) { //自殺
            message = gplayer.getColoredName() + "&6が&b"
                    + (weapon != null ? weapon + "&6で" : "&6") + "自殺しました!";
        } else {
            message = gplayer.getColoredName() + "&6が" + gkiller.getColoredName() + "&6に&b"
                    + (weapon != null ? weapon + "&6で" : "&6") + "殺されました!";
            gkiller.getProfile().addKill();
            game.addKillCount(gkiller);
        }
        GamePlayer.sendMessage(this.plugin.getPlayers().getPlayersIn(player.getWorld()), message);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        if (!plugin.getConfigs().getDeathWhenLogout()) {
            return;
        }

        String message = gplayer.getColoredName() + "&6がログアウトしたため死亡しました";
        GamePlayer.sendMessage(this.plugin.getPlayers().getPlayersIn(player.getWorld()), message);
    }

    private static boolean canUseBlockAt(GamePlayer player, Location loc) {
        if (!player.getStage().isPresent()) {
            return false;
        }
        Stage stage = player.getEntry().get().getStage().get();
        for (Team team : player.getGame().get().getTeams()) {
            if (player.getTeam().map(t -> (t == team)).orElse(Boolean.FALSE)) {
                continue;
            }
            if (stage.getBase(team.getColor()).isIn(loc)) {
                return false;
            }
        }
        return true;
    }

}
