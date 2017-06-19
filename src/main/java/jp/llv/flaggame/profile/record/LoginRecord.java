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
import syam.flaggame.FlagConfig;

/**
 *
 * @author toyblocks
 */
public class LoginRecord extends PlayerRecord {

    public LoginRecord(UUID game, double x, double y, double z, UUID player) {
        super(game, x, y, z, player);
    }

    public LoginRecord(UUID game, Player player) {
        super(game, player);
    }

    public LoginRecord(Document base) {
        super(base);
    }

    @Override
    public RecordType getType() {
        return RecordType.LOGIN;
    }

    @Override
    public double getExpWeight(FlagConfig config) {
        return config.getScoreGameJoin();
    }

}
