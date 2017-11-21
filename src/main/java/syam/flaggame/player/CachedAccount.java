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
package syam.flaggame.player;

import jp.llv.flaggame.api.player.Account;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import jp.llv.flaggame.api.exception.InvalidNameException;
import jp.llv.flaggame.api.kit.Kit;
import jp.llv.flaggame.api.player.NickPosition;
import jp.llv.flaggame.api.trophy.Trophy;

/**
 *
 * @author toyblocks
 */
public class CachedAccount implements Account {

    private final UUID uuid;
    private String name;
    
    private final Map<NickPosition, String> nicks = new EnumMap<>(NickPosition.class);
    private String kit;
    private final AtomicDouble balance = new AtomicDouble(0D);

    private final Map<NickPosition, Set<String>> unlockedNicks = new EnumMap<>(NickPosition.class);
    {
        for (NickPosition pos : NickPosition.values()) {
            unlockedNicks.put(pos, new HashSet<>());
        }
    }
    private final Set<String> unlockedKits = new HashSet<>();
    private final Set<String> unlockedTrophies = new HashSet<>();

    public CachedAccount(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getNick(NickPosition index) {
        return nicks.get(index);
    }

    @Override
    public void setNick(NickPosition index, String nick) {
        nicks.put(index, nick);
    }

    @Override
    public String getKit() {
        return kit;
    }

    @Override
    public void setKit(String kit) {
        this.kit = kit;
    }

    @Override
    public AtomicDouble getBalance() {
        return balance;
    }

    @Override
    public Set<String> getUnlockedNicks(NickPosition index) {
        return Collections.unmodifiableSet(unlockedNicks.get(index));
    }

    @Override
    public void lockNick(NickPosition index, String nick) {
        unlockedNicks.get(index).remove(nick);
    }

    @Override
    public void unlockNick(NickPosition index, String nick) throws InvalidNameException {
        if (!Account.NICK_REGEX.matcher(nick).matches()) {
            throw new InvalidNameException();
        }
        unlockedNicks.get(index).add(nick);
    }

    public void unlockNicks(NickPosition index, Collection<String> nicks) {
        nicks.forEach(n -> {
            try {
                unlockNick(index, n);
            } catch (InvalidNameException ex) {
                //IGNORE
            }
        });
    }

    @Override
    public Set<String> getUnlockedKits() {
        return Collections.unmodifiableSet(unlockedKits);
    }

    @Override
    public void lockKit(String kit) {
        unlockedKits.remove(kit);
    }

    @Override
    public void unlockKit(String kit) {
        if (!Kit.NAME_REGEX.matcher(kit).matches()) {
            throw new IllegalArgumentException();
        }
        unlockedKits.add(kit);
    }

    public void unlockKits(Collection<String> kits) {
        kits.forEach(k -> unlockKit(k));
    }

    @Override
    public Set<String> getUnlockedTrophies() {
        return Collections.unmodifiableSet(unlockedTrophies);
    }

    @Override
    public void lockTrophy(Trophy trophy) {
        unlockedTrophies.remove(trophy.getName());
    }

    @Override
    public void unlockTrophy(Trophy trophy) {
        unlockedTrophies.add(trophy.getName());
    }

    public void unlockTrophies(Collection<String> trophies) {
        trophies.addAll(trophies);
    }

}
