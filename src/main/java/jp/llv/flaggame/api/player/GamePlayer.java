/*
 * Copyright (C) 2017 toyblocks
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
package jp.llv.flaggame.api.player;

import java.util.Optional;
import java.util.UUID;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.api.session.Reservable;
import jp.llv.flaggame.api.session.Reserver;
import jp.llv.flaggame.api.game.Game;
import jp.llv.flaggame.reception.Team;
import jp.llv.flaggame.util.OptionSet;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.exception.ReservedException;
import jp.llv.flaggame.api.stage.Stage;
import syam.flaggame.player.SetupSession;

/**
 *
 * @author toyblocks
 */
public interface GamePlayer extends Reserver {

    SetupSession createSetupSession(Reservable<?> reservable) throws ReservedException;

    void destroySetupSession();

    String getColoredName();

    Optional<Reception> getEntry();

    Optional<? extends Game> getGame();

    Player getPlayer();

    Optional<StageSetupSession> getSetupSession();

    Optional<Stage> getStage();

    Optional<Team> getTeam();

    Optional<Location> getTpBackLocation();

    UUID getUUID();

    boolean isOnline();

    /**
     * Let this join the reception with given arguments.
     *
     * @param reception the reception to join
     * @param options argument to be given to the reception
     * @throws jp.llv.flaggame.api.exception.CommandException caused by illegal
     * state or arguments
     * @deprecated use
     * {@link jp.llv.flaggame.api.reception.Reception#join(jp.llv.flaggame.api.player.GamePlayer, jp.llv.flaggame.util.OptionSet) }
     */
    @Deprecated
    void join(Reception reception, OptionSet options) throws FlagGameException;

    /**
     * Let this leave the reception.
     *
     * @param reception the reception to join
     * @deprecated use
     * {@link jp.llv.flaggame.api.reception.Reception#leave(jp.llv.flaggame.api.player.GamePlayer) }
     */
    @Deprecated
    void leave(Reception reception);

    void playSound(Sound sound);

    void resetTabName();

    void sendMessage(ChatMessageType type, String... messages);

    void sendMessage(String... messages);

    void sendMessage(ChatMessageType type, BaseComponent... message);

    void sendMessage(BaseComponent... message);

    void sendTitle(String title, String sub, int in, int stay, int out);

    void setTabName(String tabName);

    void setTpBackLocation(Location tpBack);

    boolean tpBack();

    static void sendMessage(Iterable<? extends GamePlayer> players, ChatMessageType type, String... messages) {
        for (GamePlayer p : players) {
            if (p == null) {
                continue;
            }
            p.sendMessage(type, messages);
        }
    }

    static void sendMessage(Iterable<? extends GamePlayer> players, String... messages) {
        sendMessage(players, ChatMessageType.CHAT, messages);
    }

    static void sendMessage(Iterable<? extends GamePlayer> players, ChatMessageType type, BaseComponent... message) {
        for (GamePlayer p : players) {
            if (p == null) {
                continue;
            }
            p.sendMessage(type, message);
        }
    }

    static void sendMessage(Iterable<? extends GamePlayer> players, BaseComponent... message) {
        sendMessage(players, ChatMessageType.CHAT, message);
    }

    static void playSound(Iterable<? extends GamePlayer> players, Sound sound) {
        for (GamePlayer p : players) {
            if (p == null) {
                continue;
            }
            p.playSound(sound);
        }
    }

    static void sendTitle(Iterable<? extends GamePlayer> players, String title, String subTitle, int in, int stay, int out) {
        for (GamePlayer p : players) {
            if (p == null) {
                continue;
            }
            p.sendTitle(title, subTitle, in, stay, out);
        }
    }

}
