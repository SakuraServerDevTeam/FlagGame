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
import java.util.stream.Collectors;
import jp.llv.flaggame.reception.Team;
import org.bukkit.Effect;
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
    private final Collection<Player> players;

    public BGBlockListener(FlagGame plugin, BasicGame game) {
        super(game);
        this.plugin = plugin;
        this.players = game.getReception().getPlayers()
                .stream().map(GamePlayer::getPlayer)
                .collect(Collectors.toSet());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!this.players.contains(player)) {
            return;
        }

        Flag f = this.game.getStage().getFlag(event.getBlock().getLocation());
        if (f == null) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!this.players.contains(player)) {
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

        GamePlayer gplayer = this.plugin.getPlayers().getPlayer(player);
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
            b.getLocation().getWorld().createExplosion(b.getLocation(), 0F, false);
            b.getLocation().getWorld().playEffect(b.getLocation(), Effect.ENDER_SIGNAL, 0, 10);
        }
    }

}
