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
package jp.llv.flaggame.api.stage.objective;

import jp.llv.flaggame.util.BannerUtils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;

/**
 *
 * @author Toyblocks
 */
public class BannerSpawner extends StageObjective {

    public static final Material[] BANNER_IDS = {Material.BANNER, Material.WALL_BANNER};

    private final byte point;
    private final byte hp;
    private final boolean producing;

    private final boolean wall;
    private final BlockFace face;

    public BannerSpawner(Location loc, byte point, byte hp, boolean producing, boolean wall, BlockFace face) {
        super(loc, ObjectiveType.BANNER_SPAWNER, true);
        this.point = point;
        this.hp = hp;
        this.producing = producing;
        this.wall = wall;
        this.face = face;
    }

    public byte getPoint() {
        return point;
    }

    public byte getHp() {
        return (byte) Math.abs(hp);
    }

    public boolean isSnatchable() {
        return this.hp < 0;
    }

    public boolean isProducing() {
        return producing;
    }

    public boolean isWall() {
        return wall;
    }

    public BlockFace getFace() {
        return face;
    }

    public void spawnBanner() {
        Location loc = getLocation();
        loc.getBlock().setType(wall ? Material.WALL_BANNER : Material.BANNER);
        Banner b = (Banner) loc.getBlock().getState();
        b.setBaseColor(DyeColor.CYAN);
        BannerUtils.paintNum(b, point, DyeColor.WHITE, DyeColor.CYAN);
    }

}
