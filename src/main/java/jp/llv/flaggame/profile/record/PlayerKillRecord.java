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
package jp.llv.flaggame.profile.record;

import jp.llv.flaggame.api.profile.RecordType;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagConfig;

/**
 *
 * @author toyblocks
 */
public class PlayerKillRecord extends ScoreRecord {

    private static final String FIELD_KILLED = "killed";
    private static final String FIELD_WEAPON = "weapon";

    public PlayerKillRecord(UUID game, double x, double y, double z, UUID player, double score, UUID killed, String weapon) {
        super(game, x, y, z, player, score);
        super.put(FIELD_WEAPON, weapon);
        super.put(FIELD_KILLED, killed);
    }

    public PlayerKillRecord(UUID game, Player player, double score, UUID killed, String weapon) {
        super(game, player, score);
        super.put(FIELD_WEAPON, weapon);
        super.put(FIELD_KILLED, killed);
    }

    public PlayerKillRecord(Document base) {
        super(base);
    }

    public String getWeapon() {
        return super.getString(FIELD_WEAPON);
    }

    public UUID getKilled() {
        return super.get(FIELD_KILLED, UUID.class);
    }

    @Override
    public RecordType getType() {
        return RecordType.KILL;
    }

    @Override
    public double getGamePoint() {
        return getScore();
    }

    @Override
    public double getExpWeight(FlagConfig config) {
        return getScore() * config.getScoreCombatKill();
    }
}
