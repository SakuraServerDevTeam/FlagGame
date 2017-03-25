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
package jp.llv.flaggame.game.basic.objective;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.util.BannerUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

/**
 *
 * @author Toyblocks
 */
public class HeldBanner {

    private final Map<BannerSpawner, Byte> sources;

    public HeldBanner(Collection<BannerSpawner> spawners) {
        this.sources = new HashMap<>();
        spawners.stream().forEach(s -> this.sources.put(s, s.getHp()));
    }
    
    public HeldBanner(BannerSpawner spawner) {
        this.sources = Collections.singletonMap(spawner, spawner.getHp());
    }

    public Collection<BannerSpawner> getSpawners() {
        return Collections.unmodifiableSet(sources.keySet());
    }

    public byte getHp() {
        return (byte) sources.keySet().stream().mapToInt(BannerSpawner::getHp).max().orElse(0);
    }

    public byte getPoint() {
        return (byte) sources.keySet().stream().mapToInt(BannerSpawner::getPoint).sum();
    }

    public HeldBanner append(BannerSpawner spawner) {
        this.sources.put(spawner, spawner.getHp());
        return this;
    }

    public HeldBanner append(Collection<BannerSpawner> spawners) {
        spawners.stream().forEach(s -> this.sources.put(s, s.getHp()));
        return this;
    }

    public ItemStack getBanner(TeamColor color) {
        ItemStack is = new ItemStack(Material.BANNER, 1);
        BlockStateMeta bs = (BlockStateMeta) Bukkit.getItemFactory().getItemMeta(Material.BANNER);
        Banner b = (Banner) bs.getBlockState();
        BannerUtils.paintNum(b, getPoint(), DyeColor.WHITE, color.getDyeColor());
        bs.setBlockState(b);
        is.setItemMeta(bs);
        return is;
    }

    public boolean isBroken() {
        return sources.isEmpty();
    }

    public Set<BannerSpawner> damage() {
        Set<BannerSpawner> result = new HashSet<>(this.sources.size());
        Iterator<Map.Entry<BannerSpawner, Byte>> it = sources.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BannerSpawner, Byte> e = it.next();
            if (e.getValue() == 1) {
                result.add(e.getKey());
                it.remove();
            } else {
                e.setValue((byte) (e.getValue() - 1));
            }
        }
        return result;
    }

    public Set<BannerSpawner> destroy() {
        Set<BannerSpawner> result = new HashSet<>(this.sources.keySet());
        this.sources.clear();
        return result;
    }

}
