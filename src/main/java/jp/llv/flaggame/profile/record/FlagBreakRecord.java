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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import syam.flaggame.ConfigurationManager;

/**
 *
 * @author Toyblocks, SakuraServerDev
 */
public class FlagBreakRecord extends ScoreRecord {

    public FlagBreakRecord(UUID game, double x, double y, double z, UUID player, double score) {
        super(game, x, y, z, player, score);
    }

    public FlagBreakRecord(UUID game, UUID player, Location location, double score) {
        super(game, player, location, score);
    }

    @Deprecated
    public FlagBreakRecord(UUID game, Player player, double score) {
        super(game, player, score);
    }

    /*package*/ FlagBreakRecord(Document base) {
        super(base);
    }

    @Override
    public RecordType getType() {
        return RecordType.FLAG_BREAK;
    }

    @Override
    public double getExp(ConfigurationManager config) {
        return getScore() * config.getScoreFlagBreak();
    }

}
