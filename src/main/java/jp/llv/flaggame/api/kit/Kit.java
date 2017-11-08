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
package jp.llv.flaggame.api.kit;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import jp.llv.nbt.StructureLibAPI;
import jp.llv.nbt.tag.TagCompound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author toyblocks
 */
public final class Kit implements Serializable {

    public static final Pattern NAME_REGEX = Pattern.compile("^[a-z0-9](?:[a-z0-9-/]*[a-z0-9])?$");
    
    private final String name;
    private final TagCompound icon;
    private final TagCompound inventory;
    private final TagCompound enderchest;
    private final Map<String, Integer> effects;

    public Kit(String name, TagCompound icon, TagCompound inventory, TagCompound enderchest, Map<String, Integer> effects) {
        this.name = Objects.requireNonNull(name);
        this.icon = Objects.requireNonNull(icon);
        this.inventory = Objects.requireNonNull(inventory);
        this.enderchest = Objects.requireNonNull(enderchest);
        this.effects = Objects.requireNonNull(effects);
    }

    public String getName() {
        return name;
    }

    public TagCompound getIcon() {
        return icon;
    }

    public TagCompound getInventory() {
        return inventory;
    }

    public TagCompound getEnderchest() {
        return enderchest;
    }

    public Map<String, Integer> getEffects() {
        return effects;
    }

    public void applyTo(Player player) {
        StructureLibAPI lib = StructureLibAPI.Version.getDetectedVersion(player);

        lib.loadInventory(inventory).deserialize(player.getInventory());
        lib.loadInventory(enderchest).deserialize(player.getEnderChest());
        player.getActivePotionEffects().forEach(effect
                -> player.removePotionEffect(effect.getType())
        );
        effects.entrySet().forEach(effect
                -> player.addPotionEffect(
                        PotionEffectType.getByName(effect.getKey())
                                .createEffect(Integer.MAX_VALUE, effect.getValue())
                )
        );
    }

}
