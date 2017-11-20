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
package jp.llv.flaggame.api.trophie;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import jp.llv.flaggame.api.exception.AccountNotReadyException;
import jp.llv.flaggame.api.exception.InvalidNameException;
import jp.llv.flaggame.api.player.Account;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.session.Reservable;

/**
 *
 * @author toyblocks
 */
public interface Trophie extends Reservable<Trophie> {

    public static final Pattern NAME_REGEX = Pattern.compile("^[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

    String getType();
    
    void addRewardKit(String kit);

    void addRewardKits(Collection<String> kits);

    void addRewardNick(int index, String nick) throws InvalidNameException;

    void addRewardNicks(int index, Collection<String> nicks);

    String getName();

    double getRewardBits();

    Set<String> getRewardKits();

    double getRewardMoney();

    Set<String> getRewardNicks(int index);

    void removeRewardKit(String kit);

    void removeRewardNick(int index, String nick);

    /**
     * Rewards a player unconditionally.
     * This would give bits, money, nicks and kits. Not to, use 
     * {@link Account#unlockTrophie(jp.llv.flaggame.api.trophie.Trophie)}.
     * @param player a player to reward
     * @throws AccountNotReadyException if an account is not ready
     */
    void reward(GamePlayer player) throws AccountNotReadyException;

    void setRewardBits(double rewardBits);

    void setRewardMoney(double rewardMoney);
    
}
