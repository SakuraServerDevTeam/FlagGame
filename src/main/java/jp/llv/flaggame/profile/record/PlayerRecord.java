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
package jp.llv.flaggame.profile.record;

import java.util.UUID;
import org.bson.Document;
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 */
public abstract class PlayerRecord extends LocationRecord {
    
    private static final String FIELD_PLAYER = "player";
    
    public PlayerRecord(UUID game, double x, double y, double z, UUID player) {
        super(game, x, y, z);
        super.put(FIELD_PLAYER, player);
    }

    public PlayerRecord(UUID game, Player player) {
        super(game, player.getLocation());
        super.put(FIELD_PLAYER, player.getUniqueId());
    }

    public PlayerRecord(Document base) {
        super(base);
    }
    
    public UUID getPlayer() {
        return (UUID) super.get(FIELD_PLAYER);
    }
    
}
