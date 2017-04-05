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
package syam.flaggame.player;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.scheduler.BukkitRunnable;
import syam.flaggame.FlagGame;

/**
 *
 * @author SakuraServerDev
 */
public class StageSaveRemindTask extends BukkitRunnable {

    private final FlagGame plugin;

    public StageSaveRemindTask(FlagGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getPlayers().getPlayers().stream()
                .filter(p -> p.getSetupSession().isPresent())
                .forEach(p -> {
                    p.sendTitle("", "現在ステージ編集中です", 10, 10, 10);
                    p.sendMessage(ChatMessageType.ACTION_BAR, "忘れずに保存してください");
                });
    }

}
