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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import jp.llv.flaggame.events.GameFinishedEvent;
import org.bukkit.Bukkit;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;

/**
 * A {@link PlayerManager} provides ways of getting
 * {@link syam.flaggame.player.GamePlayer} instance.
 *
 * @author Syamn, Toyblocks
 */
public class PlayerManager implements Iterable<GamePlayer> {

    private final FlagGame plugin;
    private final Map<UUID, GamePlayer> players = new HashMap<>();

    /**
     * Create new {@link PlayerManager} with new registry. In order to keep
     * consistency, do NOT create new one, use
     * {@link syam.flaggame.FlagGame#getPlayers()}.
     *
     * @param plugin A plugin instance running on the server to sync this
     * mamager
     */
    public PlayerManager(FlagGame plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(new OnlinePlayerListener(), plugin);
        Bukkit.getOnlinePlayers()
                .forEach(p -> this.players.put(p.getUniqueId(), new GamePlayer(this, p)));
    }

    /**
     * Returns all game players.
     *
     * @return all game players
     */
    public Collection<GamePlayer> getPlayers() {
        return Collections.unmodifiableCollection(this.players.values());
    }

    /**
     * Returns all game players in the given world.
     *
     * @param world the world where players are, {@code null} means nowhere
     * @return all game players in the world
     */
    public Collection<GamePlayer> getPlayersIn(World world) {
        return this.players.values().stream()
                .filter(GamePlayer::isOnline)
                .filter(p -> p.getPlayer().getWorld().equals(world))
                .collect(Collectors.toSet());
    }

    /**
     * Returns the game player with the given UUID.
     *
     * @param uuid the uuid of the player to retrieve, nullable
     * @return the player if the player is online or in a game, otherwise {@code null}
     */
    public GamePlayer getPlayer(UUID uuid) {
        return this.players.get(uuid);
    }

    /**
     * Return the game player with the given name.
     *
     * @param name the name of the player to retrieve, {@code null} means noone
     * @return the player if the player is online or in a game, otherwise {@code null}
     */
    public GamePlayer getPlayer(String name) {
        return this.players.values().stream().filter(p -> p.getName().equals(name)).findAny().orElse(null);
    }

    /**
     * Return the game player of the bukkit player.
     *
     * @param player the bukkit player, who has the uuid of the game player
     * @return the player if the player is online or in a game, otherwise {@code null}.
     */
    public GamePlayer getPlayer(Player player) {
        return player == null ? null : this.getPlayer(player.getUniqueId());
    }

    /**
     * Returns an iterator of all players.
     *
     * @return an iterator of all players
     */
    @Override
    public Iterator<GamePlayer> iterator() {
        return this.getPlayers().iterator();
    }

    private void gc() {
        Iterator<GamePlayer> it = players.values().iterator();
        while (it.hasNext()) {
            GamePlayer player = it.next();
            if (!player.isOnline() && !player.getEntry().isPresent()) {
                it.remove();
            }
        }
    }

    /**
     * An internal event handler to keep contents of
     * {@link syam.flaggame.player.PlayerManager#players} the same as
     * {@link org.bukkit.Bukkit#getOnlinePlayers()} except during games. This
     * listener shouldn't be unregistered while
     * {@link syam.flaggame.player.PlayerManager} keeps alive.
     */
    private class OnlinePlayerListener implements Listener {

        /**
         * Create new {@link syam.flaggame.player.GamePlayer} of the joined
         * player and register it.
         *
         * @param e An event to handle
         */
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        @SuppressWarnings("deprecation")
        public void on(PlayerJoinEvent e) throws CommandException {
            Player p = e.getPlayer();
            GamePlayer gp = PlayerManager.this.getPlayer(p);
            if (gp != null) { // the player is in a game
                return;
            }
            gp = new GamePlayer(PlayerManager.this, p);
            PlayerManager.this.players.put(p.getUniqueId(), gp);
            for (jp.llv.flaggame.reception.GameReception r : PlayerManager.this.plugin.getReceptions()) {
                if (r.getPlayers().contains(gp)) {
                    gp.join(r, Collections.emptyList());
                }
            }
        }

        /**
         * Unregister {@link syam.flaggame.player.GamePlayer} of left player.
         *
         * @param e An event to handle
         */
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void on(PlayerQuitEvent e) {
            PlayerManager.this.getPlayer(e.getPlayer()).resetTabName();
            PlayerManager.this.gc();
        }

        @EventHandler
        public void on(GameFinishedEvent e) {
            PlayerManager.this.gc();
        }

    }
}
