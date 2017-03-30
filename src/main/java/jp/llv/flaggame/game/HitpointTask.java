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

import jp.llv.flaggame.game.permission.GamePermission;
import jp.llv.flaggame.reception.TeamColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import syam.flaggame.game.AreaSet;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author toyblocks
 */
public class HitpointTask extends BukkitRunnable {

    private final Game game;

    public HitpointTask(Game game) {
        this.game = game;
    }
    
    @Override
    public void run() {
        AreaSet areas = game.getStage().getAreas();
        for (GamePlayer gplayer : game) {
            if (!gplayer.isOnline()) {
                continue;
            }
            Player player = gplayer.getPlayer();
            Location loc = player.getLocation();
            TeamColor color = gplayer.getTeam().get().getColor();
            if (areas.getAreaInfo(loc, a -> a.getPermission(GamePermission.REGENERATION).getState(color))) {
                double hp = player.getHealth() + 1d;
                double maxHp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                player.setHealth(hp < maxHp ? hp : maxHp);
            }
            if (areas.getAreaInfo(loc, a -> a.getPermission(GamePermission.DAMAGE).getState(color))) {
                player.damage(2d);
            }
        }
    }
    
}
