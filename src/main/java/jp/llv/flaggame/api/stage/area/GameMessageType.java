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
package jp.llv.flaggame.api.stage.area;

import java.util.function.BiConsumer;
import jp.llv.flaggame.api.player.GamePlayer;
import net.md_5.bungee.api.ChatMessageType;

/**
 *
 * @author SakuraServerDev
 */
public enum GameMessageType {

    CHAT(GamePlayer::sendMessage),
    ACTIONBAR((g, m) -> g.sendMessage(ChatMessageType.ACTION_BAR, m)),
    TITLE((g, m) -> g.sendTitle(m, "", 0, 60, 20)),
    SUBTITLE((g, m) -> g.sendTitle("", m, 0, 60, 20)),;

    private final BiConsumer<GamePlayer, String> handler;

    private GameMessageType(BiConsumer<GamePlayer, String> handler) {
        this.handler = handler;
    }

    public void send(GamePlayer player, String message) {
        handler.accept(player, message);
    }

    public static GameMessageType of(String name) {
        return valueOf(name.toUpperCase());
    }

}
