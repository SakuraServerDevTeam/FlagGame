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
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.stage.permission.GamePermission;
import jp.llv.flaggame.profile.record.LoginRecord;
import jp.llv.flaggame.profile.record.PlayerLeaveRecord;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Openable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import jp.llv.flaggame.reception.TeamType;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.stage.area.StageAreaSet;
import jp.llv.flaggame.api.stage.objective.Spawn;
import syam.flaggame.util.Actions;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Toyblocks
 */
public class BGPlayerListener extends BGListener {

    private final FlagGameAPI api;
    private final Collection<GamePlayer> players;

    public BGPlayerListener(FlagGameAPI api, BasicGame game) {
        super(game);
        this.api = api;
        this.players = game.getReception().getPlayers();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.api.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        Block block = event.getClickedBlock();
        BlockState state = block.getState();
        StageAreaSet area = game.getStage().getAreas();
        TeamType color = gplayer.getTeam().get().getType();
        Location loc = block.getLocation();
        if ((state instanceof InventoryHolder && !area.getAreaInfo(loc, a -> a.getPermission(GamePermission.CONTAINER).getState(color)))
            || (state.getData() instanceof Openable && !area.getAreaInfo(loc, a -> a.getPermission(GamePermission.DOOR).getState(color)))) {
            gplayer.sendMessage("&cあなたのチームはこのブロックにアクセスできません!");
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @SuppressWarnings("deprecation")
    public void on(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.api.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        TeamType color = gplayer.getTeam().get().getType();
        gplayer.sendMessage("&c[*]&6このゲームはあと &a" + Actions.getTimeString(this.game.getRemainTime()) + "&6 残っています！");

        List<Spawn> spawns = game.getStage().getObjectives(Spawn.class).stream()
                .filter(spawn -> spawn.getColor() == gplayer.getTeam().get().getColor())
                .collect(Collectors.toList());
        Location loc = spawns.get(new Random().nextInt(spawns.size())).getLocation();
        
        event.setRespawnLocation(loc);
        player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short) 0, color.toColor().getBlockData()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.api.getConfig().getGodModeTime(), 4));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.api.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        String cmd = event.getMessage().split(" ")[0];

        for (String s : api.getConfig().getDisableCommands()) {
            if (s.equalsIgnoreCase(cmd)) {
                event.setCancelled(true);
                gplayer.sendMessage("&cこのコマンドは試合中に使えません！");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.api.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }
        game.getRecordStream().push(new LoginRecord(game.getID(), player));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.api.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }
        game.getRecordStream().push(new PlayerLeaveRecord(game.getID(), player));
        if (!api.getConfig().getDeathWhenLogout()) {
            return;
        }
        player.setHealth(0D);
        String message = gplayer.getColoredName() + "&6がログアウトしたため死亡しました";
        GamePlayer.sendMessage(this.api.getPlayers().getPlayersIn(player.getWorld()), message);
    }

}
