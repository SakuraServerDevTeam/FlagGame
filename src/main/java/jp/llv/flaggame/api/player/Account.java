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

import com.google.common.util.concurrent.AtomicDouble;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import jp.llv.flaggame.api.exception.InvalidNameException;
import jp.llv.flaggame.api.trophy.Trophy;

/**
 *
 * @author toyblocks
 */
public interface Account {

    static final Pattern NICK_REGEX = Pattern.compile("^\\S+\\s?$");
    
    UUID getUUID();
    
    AtomicDouble getBalance();

    String getKit();

    String getNick(int index);

    Set<String> getUnlockedKits();

    Set<String> getUnlockedNicks(int index);

    Set<String> getUnlockedTrophies();

    void lockKit(String kit);

    void lockNick(int index, String nick);

    void lockTrophy(Trophy trophy);

    void setKit(String kit);

    void setNick(int index, String nick);

    void unlockKit(String kit);

    void unlockNick(int index, String nick) throws InvalidNameException;

    /**
     * Simply marks a trophy as unlocked.
     * This does ignore rewards. To consider rewards, use 
     * {@link Trophy#reward(jp.llv.flaggame.api.player.GamePlayer)}.
     * @param trophy a trophy to be unlocked
     */
    void unlockTrophy(Trophy trophy);
    
}
