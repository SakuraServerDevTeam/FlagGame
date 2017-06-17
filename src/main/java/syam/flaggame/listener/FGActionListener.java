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
package syam.flaggame.listener;

import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.events.PlayerWallKickEvent;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import syam.flaggame.FlagGame;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class FGActionListener implements Listener {

    private final FlagGameAPI api;

    public FGActionListener(FlagGameAPI api) {
        this.api = api;
    }
    
    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        BlockFace face = event.getBlockFace();
        Block wallBlock = player.getLocation().getBlock().getRelative(face.getOppositeFace());
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
            || event.getHand() != EquipmentSlot.HAND
            || player.isOnGround()
            || face.getModY() != 0
            || !Perms.WALL_KICK.has(player)
            || (event.getItem() != null && event.getItem().getType().isOccluding())
            || !wallBlock.getType().isSolid()
            || wallBlock.getLocation().add(0.5, 0.5, 0.5).distanceSquared(player.getLocation()) > 1) {
            return;
        }
        Vector in = player.getEyeLocation().getDirection();
        Vector wall = new Vector(face.getModX(), face.getModY(), face.getModZ());
        Vector out = in.add(wall.multiply(-2 * in.dot(wall)))
                .multiply(api.getConfig().getWallKickPowerXZ())
                .setY(api.getConfig().getWallKickPowerY());
        PlayerWallKickEvent actionEvent = new PlayerWallKickEvent(player, wallBlock, out);
        api.getServer().getPluginManager().callEvent(actionEvent);
        if (actionEvent.isCancelled()) {
            return;
        }
        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, wallBlock.getType());
        player.setVelocity(actionEvent.getVelocity());
    }
    
}
