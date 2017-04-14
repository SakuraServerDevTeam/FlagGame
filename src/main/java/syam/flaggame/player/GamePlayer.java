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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import jp.llv.flaggame.reception.GameReception;

import org.bukkit.entity.Player;

import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.reception.Team;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import jp.llv.flaggame.api.session.Reserver;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.ReservedException;
import syam.flaggame.game.Stage;
import syam.flaggame.util.Actions;

public class GamePlayer implements Reserver {

    // プレイヤーデータ
    private final UUID player;
    private final String name;
    private final PlayerManager manager;

    private SetupSession session;
    private GameReception reception;

    private Location tpBack = null;
    private String defaultTabName = null;

    /**
     * コンストラクタ
     *
     * @param player
     */
    /*package*/ GamePlayer(PlayerManager manager, Player player) {
        if (player == null) {
            throw new NullPointerException();
        }
        this.manager = manager;
        this.player = player.getUniqueId();
        this.name = player.getName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getColoredName() {
        return this.getTeam().map(t -> t.getType().toColor().getChatColor()).orElse("&f") + this.getName() + "&r";
    }

    public UUID getUUID() {
        return this.player;
    }

    public Player getPlayer() {
        Player p = Bukkit.getPlayer(this.player);
        if (p == null) {
            throw new IllegalStateException("The player is offline");
        }
        return p;
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(this.player) != null;
    }

    /**
     * Let this join the reception with given arguments.
     *
     * @param reception the reception to join
     * @param args argument to be given to the reception
     * @throws syam.flaggame.exception.CommandException caused by illegal state
     * or arguments
     * @deprecated use
     * {@link jp.llv.flaggame.reception.GameReception#join(syam.flaggame.player.GamePlayer, java.util.List)}
     */
    @Deprecated
    public void join(GameReception reception, List<String> args) throws CommandException {
        /* 主たる処理は全てGameReception内で行う */
        if (!reception.hasReceived(this)) {//未参加なら参加させる
            reception.join(this, args);
        } else {//GameReceptionからの呼び出し->内部状態を更新
            this.reception = reception;
        }
    }

    /**
     * Let this leave the reception.
     *
     * @param reception the reception to join
     * @deprecated use
     * {@link jp.llv.flaggame.reception.GameReception#leave(syam.flaggame.player.GamePlayer)}
     */
    @Deprecated
    public void leave(GameReception reception) {
        /* 主たる処理は全てGameReception内で行う */
        if (reception.hasReceived(this)) {//参加中なら脱退させる
            reception.leave(this);
        } else {//GameReceptionからの呼び出し->内部状態を更新
            this.reception = null;
        }
    }

    public Optional<GameReception> getEntry() {
        return Optional.ofNullable(this.reception);
    }

    public Optional<? extends Game> getGame() {
        return getEntry().flatMap(r -> r.getGame(this));
    }

    public Optional<Stage> getStage() {
        return getEntry().flatMap(r -> r.getStage(this));
    }

    public Optional<Team> getTeam() {
        return getGame().map(Game::getTeams)
                .flatMap(teams
                        -> teams.stream().filter(t -> t.hasJoined(this)).findAny()
                );
    }

    public void setTpBackLocation(Location tpBack) {
        this.tpBack = tpBack;
    }

    public Optional<Location> getTpBackLocation() {
        return Optional.ofNullable(this.tpBack);
    }

    public boolean tpBack() {
        if (this.tpBack == null) {
            return false;
        }
        this.getPlayer().teleport(this.tpBack);
        this.tpBack = null;
        return true;
    }

    public void resetTabName() {
        this.getPlayer().setPlayerListName(defaultTabName);
        this.defaultTabName = null;
    }

    public void setTabName(String tabName) {
        if (this.defaultTabName == null) {
            this.defaultTabName = this.getPlayer().getPlayerListName();
        }
        String validated = tabName.length() <= 16 ? tabName : tabName.substring(0, 14) + "..";
        this.getPlayer().setPlayerListName(validated);
    }

    public Optional<SetupSession> getSetupSession() {
        if (this.session == null) {
            return Optional.empty();
        } else {
            return Optional.of(this.session);
        }
    }

    public SetupSession createSetupSession(Stage stage) throws ReservedException {
        Objects.requireNonNull(stage);
        return session = new SetupSession(stage.reserve(this));
    }

    public void destroySetupSession() {
        Objects.requireNonNull(session);
        session.getReservation().release();
        session = null;
    }

    @Override
    public int hashCode() {
        return this.player.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GamePlayer other = (GamePlayer) obj;
        return Objects.equals(this.player, other.player);
    }

    public void sendMessage(ChatMessageType type, String... messages) {
        if (!this.isOnline()) {
            return;
        }
        Player p = this.getPlayer();
        for (String message : messages) {
            Actions.sendPrefixedMessage(p, type, message);
        }
    }

    public void sendMessage(String... messages) {
        sendMessage(ChatMessageType.CHAT, messages);
    }

    public void sendMessage(ChatMessageType type, BaseComponent... message) {
        if (!this.isOnline()) {
            return;
        }
        Player p = this.getPlayer();
        Actions.sendPrefixedMessage(p, type, message);
    }

    public void sendMessage(BaseComponent... message) {
        sendMessage(ChatMessageType.CHAT, message);
    }

    public void playSound(Sound sound) {
        if (!this.isOnline()) {
            return;
        }
        this.getPlayer().playSound(this.getPlayer().getLocation(), sound, 1f, 1f);
    }

    public void sendTitle(String title, String sub, int in, int stay, int out) {
        if (!this.isOnline()) {
            return;
        }
        Actions.sendTitle(getPlayer(), title, sub, in, stay, out);
    }

    public static void sendMessage(Iterable<? extends GamePlayer> players, ChatMessageType type, String... messages) {
        for (GamePlayer p : players) {
            if (p == null) {
                continue;
            }
            p.sendMessage(type, messages);
        }
    }

    public static void sendMessage(Iterable<? extends GamePlayer> players, String... messages) {
        sendMessage(players, ChatMessageType.CHAT, messages);
    }

    public static void sendMessage(Iterable<? extends GamePlayer> players, ChatMessageType type, BaseComponent... message) {
        for (GamePlayer p : players) {
            if (p == null) {
                continue;
            }
            p.sendMessage(type, message);
        }
    }

    public static void sendMessage(Iterable<? extends GamePlayer> players, BaseComponent... message) {
        sendMessage(players, ChatMessageType.CHAT, message);
    }

    public static void playSound(Iterable<? extends GamePlayer> players, Sound sound) {
        for (GamePlayer p : players) {
            if (p == null) {
                continue;
            }
            p.playSound(sound);
        }
    }

    public static void sendTitle(Iterable<? extends GamePlayer> players, String title, String subTitle, int in, int stay, int out) {
        for (GamePlayer p : players) {
            if (p == null) {
                continue;
            }
            p.sendTitle(title, subTitle, in, stay, out);
        }
    }

}
