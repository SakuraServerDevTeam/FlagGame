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

import jp.llv.flaggame.api.player.PlayerAPI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import jp.llv.flaggame.events.GameFinishedEvent;
import jp.llv.flaggame.events.GamePlayerUnloadEvent;
import jp.llv.flaggame.api.game.Game;
import org.bukkit.Bukkit;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.util.OptionSet;

/**
 * A {@link PlayerManager} provides ways of getting
 * {@link syam.flaggame.player.FlagGamePlayer} instance.
 *
 * @author Syamn, Toyblocks
 */
public class PlayerManager implements PlayerAPI<FlagGamePlayer> {

    private final FlagGameAPI api;
    private final Map<UUID, FlagGamePlayer> players = new HashMap<>();

    /**
     * Create new {@link PlayerManager} with new registry. In order to keep
     * consistency, do NOT create new one, use
     * {@link jp.llv.flaggame.api.FlagGameAPI#getPlayers}.
     *
     * @param api An API instance running on the server to sync this mamager
     */
    public PlayerManager(FlagGameAPI api) {
        this.api = api;
        api.getServer().getPluginManager().registerEvents(new OnlinePlayerListener(), api.getPlugin());
        new StageSaveRemindTask(api).runTaskTimer(api.getPlugin(), 1200L, 1200L);
        Bukkit.getOnlinePlayers()
                .forEach(p -> this.players.put(p.getUniqueId(), new FlagGamePlayer(this, p)));
    }

    /**
     * Returns all game players.
     *
     * @return all game players
     */
    @Override
    public Collection<FlagGamePlayer> getPlayers() {
        return Collections.unmodifiableCollection(this.players.values());
    }

    /**
     * Returns all game players in the given world.
     *
     * @param world the world where players are, {@code null} means nowhere
     * @return all game players in the world
     */
    @Override
    public Collection<FlagGamePlayer> getPlayersIn(World world) {
        return this.players.values().stream()
                .filter(FlagGamePlayer::isOnline)
                .filter(p -> p.getPlayer().getWorld().equals(world))
                .collect(Collectors.toSet());
    }

    /**
     * Returns the game player with the given UUID.
     *
     * @param uuid the uuid of the player to retrieve, nullable
     * @return the player if the player is online or in a game, otherwise
     * {@code null}
     */
    @Override
    public FlagGamePlayer getPlayer(UUID uuid) {
        return this.players.get(uuid);
    }

    /**
     * Return the game player with the given name.
     *
     * @param name the name of the player to retrieve, {@code null} means noone
     * @return the player if the player is online or in a game, otherwise
     * {@code null}
     */
    @Override
    public FlagGamePlayer getPlayer(String name) {
        return this.players.values().stream().filter(p -> p.getName().equals(name)).findAny().orElse(null);
    }

    /**
     * Return the game player of the bukkit player.
     *
     * @param player the bukkit player, who has the uuid of the game player
     * @return the player if the player is online or in a game, otherwise
     * {@code null}.
     */
    @Override
    public FlagGamePlayer getPlayer(Player player) {
        return player == null ? null : this.getPlayer(player.getUniqueId());
    }

    /**
     * Returns an iterator of all players.
     *
     * @return an iterator of all players
     */
    @Override
    public Iterator<FlagGamePlayer> iterator() {
        return this.getPlayers().iterator();
    }

    private void gc() {
        Iterator<FlagGamePlayer> it = players.values().iterator();
        while (it.hasNext()) {
            FlagGamePlayer player = it.next();
            if (player.isOnline()) {
                continue; // the player is still online
            } else if (player.getEntry().isPresent()) {
                Reception entry = player.getEntry().get();
                if (entry.getState().toGameState() != Game.State.FINISHED) {
                    continue; // the player is still in game
                }
                entry.leave(player); // remove from the reception
            }
            api.getServer().getPluginManager().callEvent(new GamePlayerUnloadEvent(player));
            it.remove(); // otherwise remove player
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
         * Create new {@link syam.flaggame.player.FlagGamePlayer} of the joined
         * player and register it.
         *
         * @param e An event to handle
         */
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        @SuppressWarnings("deprecation")
        public void on(PlayerJoinEvent e) {
            Player p = e.getPlayer();
            FlagGamePlayer gp = PlayerManager.this.getPlayer(p);
            if (gp != null) { // the player is in a game
                return;
            }
            gp = new FlagGamePlayer(PlayerManager.this, p);
            PlayerManager.this.players.put(p.getUniqueId(), gp);
            for (jp.llv.flaggame.api.reception.Reception r : api.getReceptions()) {
                if (r.getPlayers().contains(gp)) {
                    try {
                        gp.join(r, new OptionSet());
                    } catch (FlagGameException ex) {
                        api.getLogger().warn("A player failed re-joining to a reception", ex);
                    }
                }
            }
        }

        /**
         * Unregister {@link syam.flaggame.player.FlagGamePlayer} of left
         * player.
         *
         * @param e An event to handle
         */
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void on(PlayerQuitEvent e) {
            PlayerManager.this.getPlayer(e.getPlayer()).resetTabName();
            // wait until Player#getOnline returns false
            api.getServer().getScheduler().runTask(api.getPlugin(), PlayerManager.this::gc);
        }

        @EventHandler
        public void on(GameFinishedEvent e) {
            PlayerManager.this.gc();
        }

    }
}
