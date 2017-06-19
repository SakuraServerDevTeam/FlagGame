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

import jp.llv.flaggame.api.game.Game;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import jp.llv.flaggame.api.stage.objective.BannerSlot;
import jp.llv.flaggame.api.stage.objective.Flag;
import jp.llv.flaggame.api.stage.objective.Nexus;
import jp.llv.flaggame.api.stage.objective.StageObjective;

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
        game.getStage().getObjectives().values().stream()
                .filter(o -> o instanceof Flag || o instanceof BannerSlot || o instanceof Nexus)
                .map(StageObjective::getLocation)
                .forEach(this::spawnParticle);
    }

    private void spawnParticle(Location loc) {
        World world = loc.getWorld();
        world.spawnParticle(
                Particle.ENCHANTMENT_TABLE,
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                10,
                0.5, 0.5, 0.5,
                1
        );
    }

}
