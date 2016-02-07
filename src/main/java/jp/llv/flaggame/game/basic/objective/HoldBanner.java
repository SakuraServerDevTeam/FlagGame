/*
 * Copyright (C) 2015 Toyblocks
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import jp.llv.flaggame.util.BannerUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Toyblocks
 */
public class HoldBanner {

    private final Map<BannerSpawner, Byte> spawner;

    public HoldBanner(BannerSpawner ... spawner) {
        this.spawner = new HashMap<>();
        for (BannerSpawner bs : spawner) {
            this.spawner.put(bs, bs.getHp());
        }
    }

    public Collection<BannerSpawner> getSpawners() {
        return Collections.unmodifiableSet(spawner.keySet());
    }
    
    public byte getHp() {
        return (byte) spawner.keySet().stream().mapToInt(BannerSpawner::getHp).max().orElse(0);
    }
    
    public byte getPoint() {
        return (byte) spawner.keySet().stream().mapToInt(BannerSpawner::getPoint).sum();
    }
    
    public ItemStack getBanner() {
        ItemStack is = new ItemStack(Material.BANNER, 1);
        Banner b = (Banner) Bukkit.getItemFactory().getItemMeta(Material.BANNER);
        BannerUtils.paintNum(b, getPoint(), DyeColor.WHITE, DyeColor.CYAN);
    }
    
    public boolean isBroken() {
        return this.spawner.values().stream().allMatch(b -> b<=0);
    }
    
    public Optional<HoldBanner> damage() {
        this.spawner.entrySet().stream().forEach(e -> e.setValue((byte) (e.getValue()-1)));
    }

}
