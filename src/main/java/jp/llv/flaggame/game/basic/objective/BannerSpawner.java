/*
 * Copyright (c) 2015 Toyblocks All rights reserved.
 */
package jp.llv.flaggame.game.basic.objective;

import java.util.Objects;
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
public class BannerSpawner {

    public static final Material[] BANNER_IDS = {Material.BANNER, Material.WALL_BANNER};
    
    private final Location loc;
    private final byte point;
    private final byte hp;
    
    private final boolean wall;
    private final BlockFace face;

    public BannerSpawner(Location loc, byte point, byte hp, boolean wall, BlockFace face) {
        Objects.requireNonNull(loc, "A banner spawner must be located");
        this.loc = loc;
        this.point = point;
        this.hp = hp;
        this.wall = wall;
        this.face = face;
    }

    public Location getLocation() {
        return loc;
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

    public boolean isWall() {
        return wall;
    }

    public BlockFace getFace() {
        return face;
    }
    
    public void spawnBanner() {
        loc.getBlock().setType(wall ? Material.WALL_BANNER : Material.BANNER);
        Banner b = (Banner) loc.getBlock().getState();
        b.setBaseColor(DyeColor.CYAN);
        BannerUtils.paintNum(b, point, DyeColor.WHITE, DyeColor.CYAN);
    }

}
