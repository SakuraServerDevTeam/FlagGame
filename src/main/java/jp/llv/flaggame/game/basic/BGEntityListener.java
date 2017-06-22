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
package jp.llv.flaggame.game.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.stage.objective.BannerSpawner;
import jp.llv.flaggame.api.stage.objective.Flag;
import jp.llv.flaggame.api.stage.permission.GamePermission;
import jp.llv.flaggame.profile.record.BannerStealRecord;
import jp.llv.flaggame.profile.record.PlayerDeathRecord;
import jp.llv.flaggame.profile.record.PlayerKillRecord;
import jp.llv.flaggame.reception.Team;
import jp.llv.flaggame.util.StringUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.stage.area.StageAreaSet;

/**
 * PvP handling listener.
 *
 * @author Toyblocks
 */
public class BGEntityListener extends BGListener {

    private static final double WOOL_REPAINT_PCT = 0.5;

    private final FlagGameAPI api;
    private final Collection<GamePlayer> players;

    public BGEntityListener(FlagGameAPI api, BasicGame game) {
        super(game);
        this.api = api;
        this.players = game.getReception().getPlayers();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        EntityDamageCause cause = getDamagerAndCause(event);
        Player damager = cause.attacker;
        boolean remoteDamage = cause.remote;

        Player player = (Player) event.getEntity();
        GamePlayer gp = this.api.getPlayers().getPlayer(player);
        GamePlayer gd = this.api.getPlayers().getPlayer(damager);

        // Banner check (drop)
        Optional<HeldBanner> bannerHeld = game.getBannerHeld(gp);
        if (bannerHeld.map(HeldBanner::isBroken).orElse(false)) {
            game.clearBannerHeld(gp);
        }
        Set<BannerSpawner> bannerDestroyed = bannerHeld.map(b -> b.damage()).orElse(Collections.emptySet());
        if (damager == null || remoteDamage) {
            bannerDestroyed.forEach(b -> {
                b.spawnBanner();
                GamePlayer.sendMessage(game, ChatMessageType.ACTION_BAR,
                        gp.getColoredName() + "&aが&6" + b.getPoint() + "pバナー&aを落としました！");
            });
            // supress damage by game-effect
            if (event.getDamager() instanceof Firework) {
                event.setCancelled(true);
            }
            return;
        }

        if (!this.players.contains(gp) || !this.players.contains(gd)) {
            return;
        }

        // Disallow damage in basements
        StageAreaSet as = game.getStage().getAreas();
        if (as.getAreaInfo(player.getLocation(), a -> a.getPermission(GamePermission.GODMODE).getState(gp.getTeam().get().getType()))) {
            event.setCancelled(true);
            if (!as.getAreaInfo(damager.getLocation(), a -> a.getPermission(GamePermission.GODMODE).getState(gd.getTeam().get().getType()))) {
                damager.damage(event.getDamage(), player);
            }
            return;
        }

        // Disallow friendly fire
        if (api.getConfig().getDisableTeamPVP()
            && gp.getTeam().get() == gd.getTeam().get()) {
            event.setDamage(0D);
            event.setCancelled(true);
            return;
        }

        // Banner check (steal)
        if (!bannerDestroyed.isEmpty() && !remoteDamage) {
            HeldBanner bannerItem = game.getBannerHeld(gd)
                    .map(b -> b.append(bannerDestroyed))
                    .orElseGet(() -> {
                        HeldBanner b = new HeldBanner(bannerDestroyed);
                        game.setBannerHeld(gd, b);
                        return b;
                    });
            gd.getPlayer().getInventory().setHelmet(bannerItem.getBanner(gd.getTeam().get().getColor()));
            game.getRecordStream().push(new BannerStealRecord(game.getID(), damager, bannerItem.getPoint()));
            GamePlayer.sendMessage(gp.getTeam().get(), ChatMessageType.ACTION_BAR,
                    gd.getColoredName() + "&aに" + gp.getColoredName() + "&aの&6"
                    + bannerItem.getPoint() + "pバナー&aを奪われました！");
            GamePlayer.sendMessage(game.getPlayersNotIn(gp.getTeam().get()), ChatMessageType.ACTION_BAR,
                    gd.getColoredName() + "&aが" + gp.getColoredName() + "&aの&6"
                    + bannerItem.getPoint() + "pバナー&aを奪いました！");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        GamePlayer gkilled = this.api.getPlayers().getPlayer(killed);
        if (!this.players.contains(gkilled)) {
            return;
        }
        event.setDroppedExp(0);

        event.setDeathMessage(null);

        EntityDamageCause cause = getDamagerAndCause(killed.getLastDamageCause());
        Player killer = cause.attacker;
        String weapon = cause.cause;

        GamePlayer gkiller = this.api.getPlayers().getPlayer(killer);
        if (gkiller != null && (!gkiller.getGame().isPresent() || gkiller.getGame().get() != this.game)) {
            return;
        }

        Team killedTeam = gkilled.getTeam().get();
        Team killerTeam = gkiller != null ? gkiller.getTeam().get() : null;

        //Keep exp level in order to keep score
        event.setKeepLevel(true);

        String message;
        if (gkilled == gkiller || gkiller == null || killerTeam == null) { //自殺
            message = gkilled.getColoredName() + "&6が&b"
                      + (weapon != null ? weapon + "&6で" : "&6") + "自殺しました!";
        } else {
            event.getDrops().stream().filter((is) -> (Flag.isFlag(is.getType()) && Math.random() < WOOL_REPAINT_PCT))
                    .forEach(is -> is.setData(new MaterialData(killerTeam.getColor().getBlockData())));
            message = gkilled.getColoredName() + "&6が" + gkiller.getColoredName() + "&6に&b"
                      + (weapon != null ? weapon + "&6で" : "&6") + "殺されました!";
            game.getRecordStream().push(new PlayerKillRecord(game.getID(), killer, game.getStage().getKillScore(), killed.getUniqueId(), weapon));
        }
        game.getRecordStream().push(new PlayerDeathRecord(game.getID(), killed, game.getStage().getDeathScore()));

        GamePlayer.sendMessage(this.api.getPlayers().getPlayersIn(killed.getWorld()), ChatMessageType.ACTION_BAR, message);
        GamePlayer.playSound(killedTeam, Sound.ENTITY_RABBIT_DEATH);
        if (killerTeam != null) {
            GamePlayer.playSound(killerTeam, Sound.ENTITY_SKELETON_DEATH);
        }

        // Banner check
        Optional<HeldBanner> bannerHeld = game.getBannerHeld(gkilled);
        if (bannerHeld.isPresent()) {
            game.clearBannerHeld(gkilled);
            Set<BannerSpawner> bannerDestroyed = bannerHeld.map(b -> b.destroy()).orElse(null);
            killed.getInventory().setHelmet(new ItemStack(Material.AIR));
            if (gkiller == null || cause.remote) {
                bannerDestroyed.forEach(b -> {
                    b.spawnBanner();
                    GamePlayer.sendMessage(game, ChatMessageType.ACTION_BAR,
                            gkilled.getColoredName() + "&aが&6" + b.getPoint() + "pバナー&aを落としました！");
                });
            } else {
                HeldBanner banner = game.getBannerHeld(gkiller)
                        .map(b -> b.append(bannerDestroyed))
                        .orElseGet(() -> {
                            HeldBanner b = new HeldBanner(bannerDestroyed);
                            game.setBannerHeld(gkiller, b);
                            return b;
                        });
                killer.getInventory().setHelmet(banner.getBanner(gkiller.getTeam().get().getColor()));
                game.getRecordStream().push(new BannerStealRecord(game.getID(), killer, banner.getPoint()));
                GamePlayer.sendMessage(gkilled.getTeam().get(), ChatMessageType.ACTION_BAR,
                        gkiller.getColoredName() + "&aに" + gkilled.getColoredName() + "&aの&6"
                        + banner.getPoint() + "pバナー&aを奪われました！");
                GamePlayer.sendMessage(game.getPlayersNotIn(gkilled.getTeam().get()), ChatMessageType.ACTION_BAR,
                        gkiller.getColoredName() + "&aが" + gkiller.getColoredName() + "&aの&6"
                        + banner.getPoint() + "pバナー&aを奪いました！");

            }
        }
    }

    private static EntityDamageCause getDamagerAndCause(EntityDamageEvent cause) {
        Player killer;
        String weapon;
        boolean remote = false;
        if (cause == null) { // unknown
            killer = null;
            weapon = "Unknown";
        } else if (!(cause instanceof EntityDamageByEntityEvent)) { // not killed
            killer = null;
            weapon = StringUtil.capitalize(cause.getCause().toString());
        } else {
            Entity killerEnt = ((EntityDamageByEntityEvent) cause).getDamager();
            if (killerEnt instanceof Player) { // killed by a player
                killer = (Player) killerEnt;
                ItemStack is = killer.getInventory().getItemInMainHand();
                if (is == null) {
                    weapon = null;
                } else if (!is.hasItemMeta()) {
                    weapon = StringUtil.capitalize(is.getType().name());
                } else if (!is.getItemMeta().hasDisplayName()) {
                    weapon = StringUtil.capitalize(is.getType().name());
                } else {
                    weapon = is.getItemMeta().getDisplayName();
                }
            } else if (killerEnt instanceof Projectile) { // killed by projectile
                Projectile p = (Projectile) killerEnt;
                weapon = p.getCustomName();
                weapon = weapon != null ? weapon : p.getName();
                if (p.getShooter() instanceof Player) {
                    killer = (Player) p.getShooter();
                } else {
                    killer = null;
                }
                remote = true;
            } else { // killed by entity
                killer = null;
                weapon = killerEnt.getCustomName();
                weapon = weapon != null ? weapon : killerEnt.getName();
            }
        }
        return new EntityDamageCause(killer, weapon, remote);
    }

    private static class EntityDamageCause {

        private final Player attacker;
        private final String cause;
        private final boolean remote;

        public EntityDamageCause(Player attacker, String cause, boolean remote) {
            this.attacker = attacker;
            this.cause = cause;
            this.remote = remote;
        }
    }

}
