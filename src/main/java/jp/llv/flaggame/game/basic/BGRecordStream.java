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
package jp.llv.flaggame.game.basic;

import jp.llv.flaggame.profile.GameRecordStream;
import jp.llv.flaggame.profile.record.GameRecord;
import jp.llv.flaggame.profile.record.PlayerRecord;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author SakuraServerDev
 */
public class BGRecordStream extends GameRecordStream {
    
    private final FlagGame plugin;
    private final BasicGame game;
    
    public BGRecordStream(FlagGame plugin, BasicGame game) {
        super(game.getID());
        this.plugin = plugin;
        this.game = game;
    }

    @Override
    public void push(GameRecord record) {
        super.push(record);
        if (!(record instanceof PlayerRecord)) {
            return;
        }
        PlayerRecord pr = (PlayerRecord) record;
        GamePlayer gplayer = plugin.getPlayers().getPlayer(pr.getPlayer());
        if (!gplayer.isOnline()) {
            return;
        }
        Player player = gplayer.getPlayer();
        float sum = player.getLevel() + player.getExp();
        sum += pr.getExp(plugin.getConfigs());
        int level = (int) sum;
        float exp = sum - level;
        player.setLevel(level);
        player.setExp(exp);
    }
    
}
