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
import java.util.Optional;
import jp.llv.flaggame.game.basic.objective.BannerSlot;
import jp.llv.flaggame.game.basic.objective.BannerSpawner;
import jp.llv.flaggame.reception.Team;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import syam.flaggame.FlagGame;
import syam.flaggame.enums.TeamColor;
import jp.llv.flaggame.game.basic.objective.Flag;
import jp.llv.flaggame.game.basic.objective.Nexus;
import org.bukkit.Material;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class BGBlockListener extends BGListener {

    private final FlagGame plugin;
    private final Collection<GamePlayer> players;

    public BGBlockListener(FlagGame plugin, BasicGame game) {
        super(game);
        this.plugin = plugin;
        this.players = game.getReception().getPlayers();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        Optional<Flag> of = this.game.getStage().getFlag(event.getBlock().getLocation());
        Optional<Nexus> on = this.game.getStage().getNexus(event.getBlock().getLocation());
        Optional<BannerSpawner> ob = this.game.getStage().getBannerSpawner(event.getBlock().getLocation());

        event.setCancelled(true);
        of.ifPresent(f -> this.breakFlag(gplayer, f, event));
        on.ifPresent(n -> this.breakNexus(gplayer, n, event));
        ob.ifPresent(b -> this.breakBannerSpawner(gplayer, b, event));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        Optional<Flag> of = this.game.getStage().getFlag(event.getBlock().getLocation());
        Optional<BannerSlot> os = this.game.getStage().getBannerSlot(event.getBlock().getLocation());

        event.setCancelled(true);
        of.ifPresent(f -> this.placeFlag(gplayer, f, event));
        os.ifPresent(s -> this.placeBanner(gplayer, s, event));
    }

    private void placeFlag(GamePlayer gplayer, Flag f, BlockPlaceEvent event) {
        Block b = event.getBlockPlaced();
        if (!Flag.isFlag(b.getType())) {//対象ブロックでない
            event.setCancelled(true);
            return;
        }

        Team placerTeam = gplayer.getTeam().get();
        @SuppressWarnings("deprecation")
        TeamColor placedTeamColor = TeamColor.getByColorData(b.getData());

        if (placedTeamColor != placerTeam.getColor()) {
            gplayer.sendMessage("&c味方チーム以外のフラッグは設置できません!");
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);

        GamePlayer.sendMessage(placerTeam, gplayer.getColoredName() + "&aが&6" + f.getTypeName() + "pフラッグ&aを獲得しました!");
        this.game.getTeams().stream().filter(team -> team != placerTeam)
                .forEach(team -> GamePlayer.sendMessage(team,
                                gplayer.getColoredName() + "&aに&6" + f.getTypeName() + "pフラッグ&aを獲得されました!"));
        this.game.getProfile().capture.add(gplayer, f.getFlagPoint());

        if (plugin.getConfigs().getUseFlagEffects()) {
            Location loc = b.getLocation();
            loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0, 10);
            loc.getWorld().playEffect(loc, Effect.SMOKE, 4, 2);
        }
    }
    
    private void placeBanner(GamePlayer gplayer, BannerSlot s, BlockPlaceEvent event) {
        
    }

    private void breakFlag(GamePlayer gplayer, Flag f, BlockBreakEvent event) {
        Team placerTeam = gplayer.getTeam().get();
        @SuppressWarnings("deprecation")
        TeamColor placedTeamColor = TeamColor.getByColorData(event.getBlock().getData());
        if (placerTeam.getColor() == placedTeamColor) {
            gplayer.sendMessage("&c味方チームのフラッグは破壊できません!");
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);

        GamePlayer.sendMessage(placerTeam,
                gplayer.getColoredName() + "&aが" + placedTeamColor.getTeamName() + "チームの&6" + f.getTypeName() + "pフラッグ&aを破壊しました!");
        this.game.getTeams().stream().filter(team -> team.getColor() == placedTeamColor)
                .forEach(team -> GamePlayer.sendMessage(team,
                                gplayer.getColoredName() + "&aに&6" + f.getTypeName() + "pフラッグ&aを破壊されました!"));

        if (plugin.getConfigs().getUseFlagEffects()) {
            Location loc = event.getBlock().getLocation();
            loc.getWorld().createExplosion(loc, 0F, false);
            loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0, 10);
        }
    }

    private void breakNexus(GamePlayer gplayer, Nexus f, BlockBreakEvent event) {
        Team breaker = gplayer.getTeam().get();
        Team broken = gplayer.getGame().get().getTeam(f.getColor());
        if (breaker == broken) {
            gplayer.sendMessage("&c味方チームの目標は破壊できません!");
            return;
        }
        
        GamePlayer.sendMessage(breaker,
                gplayer.getColoredName() + "&aが" + breaker.getColor().getRichName() + "の&6" + f.getPoint() + "p目標&aを破壊しました!");
        GamePlayer.sendMessage(broken, 
                gplayer.getColoredName() + "&aに" + f.getPoint() + "p目標&aを破壊されました!");
        
        
        if (plugin.getConfigs().getUseFlagEffects()) {
            Location loc = event.getBlock().getLocation();
            loc.getWorld().createExplosion(loc, 0F, false);
            loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0, 10);
        }
    }

    private void breakBannerSpawner(GamePlayer gplayer, BannerSpawner s, BlockBreakEvent event) {
        //move into
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
    }

}
