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
package jp.llv.flaggame.kit;

import java.util.Collections;
import jp.llv.flaggame.api.kit.KitAPI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import jp.llv.flaggame.api.kit.Kit;
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 */
public class KitManager implements KitAPI {
    
    private final Map<String, Kit> kits = Collections.synchronizedMap(new HashMap<>());
    
    @Override
    public void addKit(Kit kit) {
        kits.put(kit.getName(), kit);
    }
    
    @Override
    public void removeKit(Kit kit) {
        kits.remove(kit.getName(), kit);
    }
    
    @Override
    public void removeKits() {
        kits.clear();
    }

    @Override
    public Optional<Kit> getKit(String name) {
        return Optional.ofNullable(kits.get(name));
    }

    @Override
    public Map<String, Kit> getKits() {
        return Collections.unmodifiableMap(kits);
    }

    @Override
    public void openKitSelector(Player target) {
    }
    
}
