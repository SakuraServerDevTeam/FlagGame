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
package jp.llv.flaggame.game;

import java.util.Collection;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author SakuraServerDev
 */
public class ObjectiveEffectTask extends BukkitRunnable {

    private final Game game;

    public ObjectiveEffectTask(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        Collection<GamePlayer> players = game.getPlayers().stream().filter(GamePlayer::isOnline).collect(Collectors.toSet());
        game.getStage().getFlags().keySet().forEach(l -> spawnParticle(players, l));
        game.getStage().getNexuses().keySet().forEach(l -> spawnParticle(players, l));
        game.getStage().getBannerSlots().keySet().forEach(l -> spawnParticle(players, l));
    }

    private void spawnParticle(Collection<GamePlayer> players, Location loc) {
        int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
        y += 1;
        spawnParticle(players, x, y, z);
        x += 1;
        spawnParticle(players, x, y, z);
        z += 1;
        spawnParticle(players, x, y, z);
        x -= 1;
        spawnParticle(players, x, y, z);
        
    }

    private void spawnParticle(Collection<GamePlayer> players, double x, double y, double z) {
        players.stream().forEach(p -> p.getPlayer().spawnParticle(Particle.END_ROD, x, y, z, 1));
    }

}
