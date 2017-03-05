/* 
 * Copyright (C) 2017 Syamn, SakuraServerDev
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

import java.util.Collection;
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
import jp.llv.flaggame.reception.TeamColor;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.util.Actions;

public class GamePlayer {

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

    public String getName() {
        return this.name;
    }

    public String getColoredName() {
        return this.getTeam().map(Team::getColor).map(TeamColor::getColor).orElse("&f") + this.getName();
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

    public Optional<Game> getGame() {
        return this.getEntry().flatMap(GameReception::getGame);
    }

    public Optional<Stage> getStage() {
        return this.getEntry().flatMap(GameReception::getStage);
    }

    public Optional<Team> getTeam() {
        return this.getEntry().flatMap(GameReception::getTeams)
                .map(Collection::stream)
                .map(s -> s.filter(t -> t.hasJoined(this)))
                .flatMap(s -> s.findAny());
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
        return Optional.ofNullable(this.session);
    }

    public SetupSession createSetupSession(Stage stage) {
        if (stage == null) {
            throw new NullPointerException("Stage can't be null.");
        }
        return this.session = new SetupSession(stage);
    }

    public void destroySetupSession() {
        this.session = null;
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

    public void sendMessage(String... messages) {
        if (!this.isOnline()) {
            return;
        }
        Player p = this.getPlayer();
        for (String message : messages) {
            Actions.sendPrefixedMessage(p, message);
        }
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
        this.getPlayer().playSound(this.getPlayer().getLocation(), sound, 1f, 1f);
    }

    public static void sendMessage(Iterable<? extends GamePlayer> players, String... messages) {
        for (GamePlayer p : players) {
            if (p == null) {
                continue;
            }
            p.sendMessage(messages);
        }
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

}
