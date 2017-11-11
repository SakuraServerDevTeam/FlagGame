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

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.AccountNotReadyException;
import jp.llv.flaggame.api.session.Reservable;

import org.bukkit.entity.Player;

import jp.llv.flaggame.api.game.Game;
import jp.llv.flaggame.reception.Team;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.exception.ReservedException;
import jp.llv.flaggame.api.player.Account;
import jp.llv.flaggame.api.player.AccountState;
import syam.flaggame.util.Actions;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.util.OptionSet;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.StageSetupSession;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.database.DatabaseException;

public class FlagGamePlayer implements GamePlayer {

    // プレイヤーデータ
    private final UUID player;
    private final String name;

    private Account account;
    private final AtomicReference<AccountState> accountState = new AtomicReference<>(AccountState.INITIAL);

    private SetupSession session;
    private Reception reception;

    private Location tpBack = null;
    private String defaultTabName = null;

    /**
     * コンストラクタ
     *
     * @param player
     */
    /*package*/ FlagGamePlayer(Player player) {
        if (player == null) {
            throw new NullPointerException();
        }
        this.player = player.getUniqueId();
        this.name = player.getName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getColoredName() {
        return this.getTeam().map(t -> t.getType().toColor().getChatColor()).orElse("&f") + this.getName() + "&r";
    }

    @Override
    public UUID getUUID() {
        return this.player;
    }

    @Override
    public Player getPlayer() {
        Player p = Bukkit.getPlayer(this.player);
        if (p == null) {
            throw new IllegalStateException("The player is offline");
        }
        return p;
    }

    @Override
    public boolean isOnline() {
        return Bukkit.getPlayer(this.player) != null;
    }

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
    @Override
    public void join(Reception reception, OptionSet options) throws FlagGameException {
        /* 主たる処理は全てGameReception内で行う */
        if (!reception.hasReceived(this)) {//未参加なら参加させる
            reception.join(this, options);
        } else {//GameReceptionからの呼び出し->内部状態を更新
            this.reception = reception;
        }
    }

    /**
     * Let this leave the reception.
     *
     * @param reception the reception to join
     * @deprecated use
     * {@link jp.llv.flaggame.api.reception.Reception#leave(jp.llv.flaggame.api.player.GamePlayer) }
     */
    @Deprecated
    @Override
    public void leave(Reception reception) {
        /* 主たる処理は全てGameReception内で行う */
        if (reception.hasReceived(this)) {//参加中なら脱退させる
            reception.leave(this);
        } else {//GameReceptionからの呼び出し->内部状態を更新
            this.reception = null;
        }
    }

    @Override
    public Optional<Reception> getEntry() {
        return Optional.ofNullable(this.reception);
    }

    @Override
    public Optional<? extends Game> getGame() {
        return getEntry().flatMap(r -> r.getGame(this));
    }

    @Override
    public Optional<Stage> getStage() {
        return getEntry().flatMap(r -> r.getStage(this));
    }

    @Override
    public Optional<Team> getTeam() {
        return getGame().map(Game::getTeams)
                .flatMap(teams
                        -> teams.stream().filter(t -> t.hasJoined(this)).findAny()
                );
    }

    @Override
    public void setTpBackLocation(Location tpBack) {
        this.tpBack = tpBack;
    }

    @Override
    public Optional<Location> getTpBackLocation() {
        return Optional.ofNullable(this.tpBack);
    }

    @Override
    public boolean tpBack() {
        if (this.tpBack == null) {
            return false;
        }
        this.getPlayer().teleport(this.tpBack);
        this.tpBack = null;
        return true;
    }

    @Override
    public void resetTabName() {
        this.getPlayer().setPlayerListName(defaultTabName);
        this.defaultTabName = null;
    }

    @Override
    public void setTabName(String tabName) {
        if (this.defaultTabName == null) {
            this.defaultTabName = this.getPlayer().getPlayerListName();
        }
        String validated = tabName.length() <= 16 ? tabName : tabName.substring(0, 14) + "..";
        this.getPlayer().setPlayerListName(validated);
    }

    @Override
    public Optional<StageSetupSession> getSetupSession() {
        if (this.session == null) {
            return Optional.empty();
        } else {
            return Optional.of(this.session);
        }
    }

    @Override
    public SetupSession createSetupSession(Reservable<?> reservable) throws ReservedException {
        Objects.requireNonNull(reservable);
        return session = new SetupSession(reservable.reserve(this));
    }

    @Override
    public void destroySetupSession() {
        Objects.requireNonNull(session);
        session.getReservation().release();
        session = null;
    }

    /*package*/ void loadAccount(FlagGameAPI api) {
        if (accountState.compareAndSet(AccountState.SAVING, AccountState.AVAILABLE)
                || accountState.get() == AccountState.AVAILABLE) {
            return;
        } else if (accountState.compareAndSet(AccountState.INITIAL, AccountState.LOADING)) {
        } else {
            throw new IllegalStateException();
        }
        api.getDatabase().ifPresent(database -> {
            database.loadPlayerAccount(player, result -> {
                try {
                    account = result.get();
                    if (account == null) {
                        account = new CachedAccount(player);
                    }
                    accountState.compareAndSet(AccountState.LOADING, AccountState.AVAILABLE);
                } catch (DatabaseException ex) {
                    accountState.compareAndSet(AccountState.LOADING, AccountState.NOT_AVAILABLE);
                    api.getLogger().warn("Failed to load a player account", ex);
                }
            });
        });
    }

    /*package*/ void saveAccount(FlagGameAPI api) {
        if (getAccountState() != AccountState.AVAILABLE) {
            return;
        }
        api.getDatabase().ifPresent(database -> {
            database.savePlayerAccount(account, result -> {
                try {
                    result.test();
                } catch (DatabaseException ex) {
                    api.getLogger().warn("Failed to save a player account", ex);
                }
            });
        });
    }

    /*package*/ void unloadAccount(FlagGameAPI api, Runnable callback) {
        if (!this.accountState.compareAndSet(AccountState.AVAILABLE, AccountState.SAVING)) {
            throw new IllegalStateException();
        }
        api.getDatabase().ifPresent(database -> {
            database.savePlayerAccount(account, result -> {
                try {
                    result.test();
                } catch (DatabaseException ex) {
                    api.getLogger().warn("Failed to unload a player account", ex);
                } finally {
                    if (accountState.compareAndSet(AccountState.SAVING, AccountState.ABANDONED)) {
                        callback.run();
                    }
                }
            });
        });
    }

    @Override
    public Account getAccount() throws AccountNotReadyException {
        if (getAccountState() != AccountState.AVAILABLE) {
            throw new AccountNotReadyException();
        }
        return account;
    }

    @Override
    public AccountState getAccountState() {
        return accountState.get();
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
        final FlagGamePlayer other = (FlagGamePlayer) obj;
        return Objects.equals(this.player, other.player);
    }

    @Override
    public void sendMessage(ChatMessageType type, String... messages) {
        if (!this.isOnline()) {
            return;
        }
        Player p = this.getPlayer();
        for (String message : messages) {
            Actions.sendPrefixedMessage(p, type, message);
        }
    }

    @Override
    public void sendMessage(String... messages) {
        sendMessage(ChatMessageType.CHAT, messages);
    }

    @Override
    public void sendMessage(ChatMessageType type, BaseComponent... message) {
        if (!this.isOnline()) {
            return;
        }
        Player p = this.getPlayer();
        Actions.sendPrefixedMessage(p, type, message);
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        sendMessage(ChatMessageType.CHAT, message);
    }

    @Override
    public void playSound(Sound sound) {
        if (!this.isOnline()) {
            return;
        }
        this.getPlayer().playSound(this.getPlayer().getLocation(), sound, 1f, 1f);
    }

    @Override
    public void sendTitle(String title, String sub, int in, int stay, int out) {
        if (!this.isOnline()) {
            return;
        }
        Actions.sendTitle(getPlayer(), title, sub, in, stay, out);
    }

    @Override
    public String toString() {
        return getName();
    }

}
