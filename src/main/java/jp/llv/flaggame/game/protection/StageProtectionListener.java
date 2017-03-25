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
package jp.llv.flaggame.game.protection;

import jp.llv.flaggame.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import syam.flaggame.FlagGame;
import syam.flaggame.game.AreaSet;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author toyblocks
 */
public class StageProtectionListener implements Listener {

    private final FlagGame plugin;
    private final Game game;

    public StageProtectionListener(FlagGame plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), Protection.BREAK));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockPlaceEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), Protection.PLACE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerBucketFillEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlockClicked().getLocation(), Protection.BUCKET_FILL));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerBucketEmptyEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlockClicked().getLocation(), Protection.BUCKET_EMPTY));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerShearEntityEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        event.setCancelled(!hasPermission(event.getEntity().getLocation(), Protection.SHEAR));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockBurnEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), Protection.BURN));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockFormEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), Protection.FORM));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockFadeEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), Protection.FADE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockGrowEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), Protection.GROW));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockIgniteEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), Protection.IGNITE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(LeavesDecayEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), Protection.DECAY));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(HangingBreakEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getEntity().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getEntity().getLocation(), Protection.HANGING_BREAK));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(HangingPlaceEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getEntity().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getEntity().getLocation(), Protection.HANGING_PLACE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(CraftItemEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getWhoClicked().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getWhoClicked().getLocation(), Protection.HANGING_PLACE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerArmorStandManipulateEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        event.setCancelled(!hasPermission(event.getRightClicked().getLocation(), Protection.ARMOR_STAND));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(VehicleEnterEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getEntered().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getEntered().getLocation(), Protection.VEHICLE_ENTER));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(VehicleExitEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getVehicle().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getVehicle().getLocation(), Protection.VEHICLE_ENTER));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(VehicleDamageEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getVehicle().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getVehicle().getLocation(), Protection.VEHICLE_ENTER));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (event.getEntity() instanceof Player
            || !areas.hasStageArea()
            || !areas.getStageArea().contains(event.getEntity().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getEntity().getLocation(), Protection.ENTITY_DAMAGE));
    }

    private boolean hasPermission(Location loc, Protection type) {
        return game.getStage().getAreas().getAreaInfo(loc, a -> a.isProtected(type));
    }
    
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
