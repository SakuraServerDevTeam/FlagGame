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
import jp.llv.flaggame.profile.record.LoginRecord;
import jp.llv.flaggame.profile.record.PlayerLeaveRecord;
import jp.llv.flaggame.reception.Team;
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
import syam.flaggame.FlagGame;
import jp.llv.flaggame.reception.TeamColor;
import org.bukkit.event.EventHandler;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;
import org.bukkit.event.player.PlayerJoinEvent;

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
        gplayer.sendMessage("&c[*]&6このゲームはあと &a" + Actions.getTimeString(this.game.getRemainTime()) + "&6 残っています！");
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }
        game.getRecordStream().push(new LoginRecord(game.getID(), player));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }
        game.getRecordStream().push(new PlayerLeaveRecord(game.getID(), player));
        if (!plugin.getConfigs().getDeathWhenLogout()) {
            return;
        }
        player.setHealth(0D);
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
