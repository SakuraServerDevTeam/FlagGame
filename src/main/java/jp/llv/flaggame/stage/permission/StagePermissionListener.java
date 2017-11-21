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
package jp.llv.flaggame.stage.permission;

import jp.llv.flaggame.api.stage.permission.GamePermission;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.events.PlayerWallKickEvent;
import jp.llv.flaggame.api.game.Game;
import jp.llv.flaggame.api.reception.Team;
import jp.llv.flaggame.api.reception.TeamColor;
import jp.llv.flaggame.api.reception.TeamType;
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
import org.bukkit.event.entity.EntityDamageEvent;
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
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.stage.area.StageAreaSet;
import jp.llv.flaggame.events.PlayerSuperJumpEvent;

/**
 *
 * @author toyblocks
 */
public class StagePermissionListener implements Listener {

    private final FlagGameAPI api;
    private final Game game;

    public StagePermissionListener(FlagGameAPI api, Game game) {
        this.api = api;
        this.game = game;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        GamePlayer gplayer = api.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), color, GamePermission.BREAK));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockPlaceEvent event) {
        GamePlayer gplayer = api.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), color, GamePermission.PLACE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerBucketFillEvent event) {
        GamePlayer gplayer = api.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getBlockClicked().getLocation(), color, GamePermission.BUCKET_FILL));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerBucketEmptyEvent event) {
        GamePlayer gplayer = api.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getBlockClicked().getLocation(), color, GamePermission.BUCKET_EMPTY));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerShearEntityEvent event) {
        GamePlayer gplayer = api.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getEntity().getLocation(), color, GamePermission.SHEAR));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockBurnEvent event) {
        StageAreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), TeamColor.WHITE, GamePermission.BURN));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockFormEvent event) {
        StageAreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), TeamColor.WHITE, GamePermission.FORM));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockFadeEvent event) {
        StageAreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), TeamColor.WHITE, GamePermission.FADE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockGrowEvent event) {
        StageAreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), TeamColor.WHITE, GamePermission.GROW));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockIgniteEvent event) {
        GamePlayer gplayer = api.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getBlock().getLocation(), color, GamePermission.IGNITE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(LeavesDecayEvent event) {
        StageAreaSet areas = game.getStage().getAreas();
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
                GamePlayer gplayer = api.getPlayers().getPlayer(breaker);
                if (!game.getReception().hasReceived(gplayer)) {
                    return;
                }
                TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
                event.setCancelled(!hasPermission(loc, color, GamePermission.HANGING_BREAK));
                return;
            }
        }
        StageAreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(loc)) {
            return;
        }
        event.setCancelled(!hasPermission(loc, TeamColor.WHITE, GamePermission.HANGING_BREAK));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(HangingPlaceEvent event) {
        GamePlayer gplayer = api.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getEntity().getLocation(), color, GamePermission.HANGING_PLACE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        GamePlayer gplayer = api.getPlayers().getPlayer(player);
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getWhoClicked().getLocation(), color, GamePermission.HANGING_PLACE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerArmorStandManipulateEvent event) {
        GamePlayer gplayer = api.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getRightClicked().getLocation(), color, GamePermission.ARMOR_STAND));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(VehicleEnterEvent event) {
        StageAreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getEntered().getLocation())) {
            return;
        }
        TeamType color;
        if (event.getEntered() instanceof Player) {
            Player player = (Player) event.getEntered();
            GamePlayer gplayer = api.getPlayers().getPlayer(player);
            color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        } else {
            color = TeamColor.WHITE;
        }
        event.setCancelled(!hasPermission(event.getEntered().getLocation(), color, GamePermission.VEHICLE_ENTER));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(VehicleExitEvent event) {
        StageAreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getExited().getLocation())) {
            return;
        }
        TeamType color;
        if (event.getExited() instanceof Player) {
            Player player = (Player) event.getExited();
            GamePlayer gplayer = api.getPlayers().getPlayer(player);
            color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        } else {
            color = TeamColor.WHITE;
        }
        event.setCancelled(!hasPermission(event.getVehicle().getLocation(), color, GamePermission.VEHICLE_EXIT));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(VehicleDamageEvent event) {
        StageAreaSet areas = game.getStage().getAreas();
        if (!areas.hasStageArea() || !areas.getStageArea().contains(event.getAttacker().getLocation())) {
            return;
        }
        TeamType color;
        if (event.getAttacker() instanceof Player) {
            Player player = (Player) event.getAttacker();
            GamePlayer gplayer = api.getPlayers().getPlayer(player);
            color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        } else {
            color = TeamColor.WHITE;
        }
        event.setCancelled(!hasPermission(event.getVehicle().getLocation(), color, GamePermission.VEHICLE_DAMAGE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
        StageAreaSet areas = game.getStage().getAreas();
        if (event.getEntity() instanceof Player
            || !areas.hasStageArea()
            || !areas.getStageArea().contains(event.getEntity().getLocation())) {
            return;
        }
        TeamType color;
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            GamePlayer gplayer = api.getPlayers().getPlayer(player);
            color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        } else {
            color = TeamColor.WHITE;
        }
        event.setCancelled(!hasPermission(event.getEntity().getLocation(), color, GamePermission.ENTITY_DAMAGE));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerWallKickEvent event) {
        GamePlayer gplayer = api.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getPlayer().getLocation(), color, GamePermission.WALL_KICK));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerSuperJumpEvent event) {
        GamePlayer gplayer = api.getPlayers().getPlayer(event.getPlayer());
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
        event.setCancelled(!hasPermission(event.getPlayer().getLocation(), color, GamePermission.SUPER_JUMP));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        GamePlayer gplayer = api.getPlayers().getPlayer(player);
        if (!game.getReception().hasReceived(gplayer)) {
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            TeamType color = gplayer.getTeam().map(Team::getType).orElse(TeamColor.WHITE);
            event.setCancelled(hasPermission(player.getLocation(), color, GamePermission.FEATHER_FALL));
        }
    }

    private boolean hasPermission(Location loc, TeamType color, GamePermission type) {
        return game.getStage().getAreas().getAreaInfo(loc, a -> a.getPermission(type).getState(color));
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
