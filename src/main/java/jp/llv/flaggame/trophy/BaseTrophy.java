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
package jp.llv.flaggame.trophy;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jp.llv.flaggame.api.exception.AccountNotReadyException;
import jp.llv.flaggame.api.exception.InvalidNameException;
import jp.llv.flaggame.api.kit.Kit;
import jp.llv.flaggame.api.player.Account;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.NickPosition;
import jp.llv.flaggame.api.session.SimpleReservable;
import jp.llv.flaggame.util.OnelineBuilder;
import syam.flaggame.util.Actions;
import jp.llv.flaggame.api.trophy.Trophy;

/**
 * Base variable structure for a trophie. This class is only for
 * {@link NashornTorophie}, do not extend this.
 *
 * @author toyblocks
 */
public abstract class BaseTrophy extends SimpleReservable<Trophy> implements Trophy {

    private final String name;

    private double rewardBits = 0d;
    private double rewardMoney = 0d;

    private final Map<NickPosition, Set<String>> rewardNicks = new EnumMap<>(NickPosition.class);

    {
        for (NickPosition pos : NickPosition.values()) {
            rewardNicks.put(pos, new HashSet<>());
        }
    }
    private final Set<String> rewardKits = new HashSet<>();

    public BaseTrophy(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getRewardBits() {
        return rewardBits;
    }

    @Override
    public void setRewardBits(double rewardBits) {
        this.rewardBits = Math.max(0d, rewardBits);
    }

    @Override
    public double getRewardMoney() {
        return rewardMoney;
    }

    @Override
    public void setRewardMoney(double rewardMoney) {
        this.rewardMoney = Math.max(0d, rewardMoney);
    }

    @Override
    public Set<String> getRewardNicks(NickPosition index) {
        return Collections.unmodifiableSet(rewardNicks.get(index));
    }

    @Override
    public void removeRewardNick(NickPosition index, String nick) {
        rewardNicks.get(index).remove(nick);
    }

    @Override
    public void addRewardNick(NickPosition index, String nick) throws InvalidNameException {
        if (!Account.NICK_REGEX.matcher(nick).matches()) {
            throw new InvalidNameException();
        }
        rewardNicks.get(index).add(nick);
    }

    @Override
    public void addRewardNicks(NickPosition index, Collection<String> nicks) {
        nicks.forEach(n -> {
            try {
                addRewardNick(index, n);
            } catch (InvalidNameException ex) {
                //IGNORE
            }
        });
    }

    @Override
    public Set<String> getRewardKits() {
        return Collections.unmodifiableSet(rewardKits);
    }

    @Override
    public void removeRewardKit(String kit) {
        rewardKits.remove(kit);
    }

    @Override
    public void addRewardKit(String kit) {
        if (!Kit.NAME_REGEX.matcher(kit).matches()) {
            throw new IllegalArgumentException();
        }
        rewardKits.add(kit);
    }

    @Override
    public void addRewardKits(Collection<String> kits) {
        kits.forEach(k -> addRewardKit(k));
    }

    @Override
    public void reward(GamePlayer player) throws AccountNotReadyException {
        Account account = player.getAccount();
        account.unlockTrophy(this);

        if (rewardBits > 0) {
            double total = account.getBalance().addAndGet(rewardBits);
            OnelineBuilder.newBuilder()
                    .value(rewardBits + "bits").info("を獲得しました！(Total:")
                    .value(total + "bits").info(")")
                    .sendTo(player.getPlayer());
        }

        if (rewardMoney > 0) {
            Actions.addMoney(player.getUUID(), rewardMoney);
            OnelineBuilder.newBuilder()
                    .value(Actions.formatMoney(rewardMoney)).info("を獲得しました！")
                    .sendTo(player.getPlayer());
        }

        for (NickPosition pos : NickPosition.values()) {
            rewardNicks.get(pos).forEach(nick -> {
                try {
                    account.unlockNick(pos, nick);
                    OnelineBuilder.newBuilder()
                            .info("ニックネーム").value(nick).info("を獲得しました！")
                            .sendTo(player.getPlayer());
                } catch (InvalidNameException ex) {
                    // NEVER HERE (ALREADY VERIFIED)
                }
            });
        }

        rewardKits.forEach(kit -> {
            account.unlockKit(kit);
            OnelineBuilder.newBuilder()
                    .info("キット").value(kit).info("を獲得しました！")
                    .sendTo(player.getPlayer());
        });
    }

}
