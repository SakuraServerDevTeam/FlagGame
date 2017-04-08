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
package jp.llv.flaggame.game.permission;

import jp.llv.flaggame.events.PlayerWallKickEvent;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.reception.Team;
import jp.llv.flaggame.reception.TeamColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
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
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
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
public class StagePermissionListener implements Listener {

    private final FlagGame plugin;
    private final Game game;

    public StagePermissionListener(FlagGame plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamColor color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), color, GamePermission.BREAK));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockPlaceEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamColor color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), color, GamePermission.PLACE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerBucketFillEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamColor color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getBlockClicked().getLocation(), color, GamePermission.BUCKET_FILL));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerBucketEmptyEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamColor color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getBlockClicked().getLocation(), color, GamePermission.BUCKET_EMPTY));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerShearEntityEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamColor color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getEntity().getLocation(), color, GamePermission.SHEAR));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockBurnEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), TeamColor.WHITE, GamePermission.BURN));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockFormEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), TeamColor.WHITE, GamePermission.FORM));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockFadeEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), TeamColor.WHITE, GamePermission.FADE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockGrowEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), TeamColor.WHITE, GamePermission.GROW));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockIgniteEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamColor color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), color, GamePermission.IGNITE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(LeavesDecayEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), TeamColor.WHITE, GamePermission.DECAY));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(HangingBreakEvent event) {
        Location loc = event.getEntity().getLocation();
        if (event.getCause() == HangingBreakEvent.RemoveCause.ENTITY) {
            Entity remover = ((HangingBreakByEntityEvent) event).getRemover();
            if (remover instanceof Player) { // in case of player
                Player breaker = (Player) remover;
                GamePlayer gplayer = plugin.getPlayers().getPlayer(breaker);
                if (!game.getReception().hasReceived(gplayer)) {
                    return;
                }
                TeamColor color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
                event.setCancelled(!hasPermission(loc, color, GamePermission.HANGING_BREAK));
                return;
            }
        }
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(loc)) {
            return;
        }
        event.setCancelled(!hasPermission(loc, TeamColor.WHITE, GamePermission.HANGING_BREAK));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(HangingPlaceEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamColor color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getEntity().getLocation(), color, GamePermission.HANGING_PLACE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamColor color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getWhoClicked().getLocation(), color, GamePermission.HANGING_PLACE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerArmorStandManipulateEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamColor color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getRightClicked().getLocation(), color, GamePermission.ARMOR_STAND));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(VehicleEnterEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getEntered().getLocation())) {
            return;
        }
        TeamColor color;
        if (event.getEntered() instanceof Player) {
            Player player = (Player) event.getEntered();
            GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
            color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        } else {
            color = TeamColor.WHITE;
        }
        event.setCancelled(!hasPermission(event.getEntered().getLocation(), color, GamePermission.VEHICLE_ENTER));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(VehicleExitEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getExited().getLocation())) {
            return;
        }
        TeamColor color;
        if (event.getExited() instanceof Player) {
            Player player = (Player) event.getExited();
            GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
            color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        } else {
            color = TeamColor.WHITE;
        }
        event.setCancelled(!hasPermission(event.getVehicle().getLocation(), color, GamePermission.VEHICLE_ENTER));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(VehicleDamageEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getAttacker().getLocation())) {
            return;
        }
        TeamColor color;
        if (event.getAttacker() instanceof Player) {
            Player player = (Player) event.getAttacker();
            GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
            color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        } else {
            color = TeamColor.WHITE;
        }
        event.setCancelled(!hasPermission(event.getVehicle().getLocation(), color, GamePermission.VEHICLE_ENTER));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
        AreaSet areas = game.getStage().getAreas();
        if (event.getEntity() instanceof Player
            || !areas.hasStageArea()
            || !areas.getStageArea().contains(event.getEntity().getLocation())) {
            return;
        }
        TeamColor color;
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
            color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        } else {
            color = TeamColor.WHITE;
        }
        event.setCancelled(!hasPermission(event.getEntity().getLocation(), color, GamePermission.ENTITY_DAMAGE));
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerWallKickEvent event) {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamColor color = gplayer.getTeam().map(Team::getColor).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getPlayer().getLocation(), color, GamePermission.WALL_KICK));
    }

    private boolean hasPermission(Location loc, TeamColor color, GamePermission type) {
        return game.getStage().getAreas().getAreaInfo(loc, a -> a.getPermission(type).getState(color));
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
