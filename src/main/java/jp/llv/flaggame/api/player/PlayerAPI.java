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

import java.util.Collection;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 * @param <P> a Player type this manager accepts
 */
public interface PlayerAPI<P extends GamePlayer> extends Iterable<P> {

    /**
     * Returns the game player with the given UUID.
     *
     * @param uuid the uuid of the player to retrieve, nullable
     * @return the player if the player is online or in a game, otherwise
     * {@code null}
     */
    P getPlayer(UUID uuid);

    /**
     * Return the game player with the given name.
     *
     * @param name the name of the player to retrieve, {@code null} means noone
     * @return the player if the player is online or in a game, otherwise
     * {@code null}
     */
    P getPlayer(String name);

    /**
     * Return the game player of the bukkit player.
     *
     * @param player the bukkit player, who has the uuid of the game player
     * @return the player if the player is online or in a game, otherwise
     * {@code null}.
     */
    P getPlayer(Player player);

    /**
     * Returns all game players.
     *
     * @return all game players
     */
    Collection<P> getPlayers();

    /**
     * Returns all game players in the given world.
     *
     * @param world the world where players are, {@code null} means nowhere
     * @return all game players in the world
     */
    Collection<P> getPlayersIn(World world);

}
