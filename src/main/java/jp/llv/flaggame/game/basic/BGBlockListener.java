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
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import syam.flaggame.FlagGame;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.game.Flag;
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

        Flag f = this.game.getStage().getFlag(event.getBlock().getLocation());
        if (f == null) {
            event.setCancelled(true);
            return;
        }

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

        gplayer.getProfile().addBrokenFlag();
        this.game.getStage().getProfile().addBrokenFlag();

        if (plugin.getConfigs().getUseFlagEffects()) {
            Location loc = event.getBlock().getLocation();
            loc.getWorld().createExplosion(loc, 0F, false);
            loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0, 10);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        Flag f = this.game.getStage().getFlag(event.getBlock().getLocation());
        if (f == null) {//フラッグではない
            event.setCancelled(true);
            return;
        }

        Block b = event.getBlockPlaced();
        if (b.getType() != Material.WOOL) {//対象ブロックでない
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

        gplayer.getProfile().addPlacedFlag();
        this.game.getStage().getProfile().addPlacedFlag();

        if (plugin.getConfigs().getUseFlagEffects()) {
            Location loc = b.getLocation();
            loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0, 10);
            loc.getWorld().playEffect(loc, Effect.SMOKE, 4, 2);
        }
    }

}
