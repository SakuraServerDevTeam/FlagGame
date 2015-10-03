/* 
 * Copyright (c) 2015 SakuraServerDev All rights reserved.
 */
package syam.flaggame.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * 立体領域を表すクラス
 *
 * @author syam
 */
public class Cuboid {

    private final Location min, max;

    /**
     * コンストラクタ
     *
     * @param point1 Pos1
     * @param point2 Pos2
     */
    public Cuboid(Location point1, Location point2) {
        if (!point1.getWorld().equals(point2.getWorld())) {
            throw new IllegalArgumentException("Both of two points must be in the same world!");
        }
        // 各頂点設定
        int xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        int xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        int yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        int yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        int zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        int zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        this.min = new Location(point1.getWorld(), xMin, yMin, zMin);
        this.max = new Location(point1.getWorld(), xMax, yMax, zMax);
    }

    /**
     * 指定した座標が立体領域内かチェック
     *
     * @param loc チェックする座標
     * @return 領域内ならtrue 違えばfalse
     */
    public boolean isIn(Location loc) {
        if (loc.getWorld() != this.getWorld()) {
            return false;
        }
        if (loc.getBlockX() < min.getBlockX()) {
            return false;
        }
        if (loc.getBlockX() > max.getBlockX()) {
            return false;
        }
        if (loc.getBlockY() < min.getBlockY()) {
            return false;
        }
        if (loc.getBlockY() > max.getBlockY()) {
            return false;
        }
        if (loc.getBlockZ() < min.getBlockZ()) {
            return false;
        }
        return loc.getBlockZ() <= max.getBlockX();
    }

    /**
     * X軸の幅を取得(int)
     *
     * @return X軸の幅
     */
    public int getXWidth() {
        return max.getBlockX() - min.getBlockX();
    }

    /**
     * Y軸の幅を取得(int)
     *
     * @return Y座標の幅
     */
    public int getZWidth() {
        return max.getBlockZ() - min.getBlockZ();
    }

    /**
     * Z幅を取得(int)
     *
     * @return Y座標の幅
     */
    public int getHeight() {
        return max.getBlockY() - min.getBlockY();
    }

    /**
     * 指定領域全体のブロック数を取得
     *
     * @return X幅*Y幅*高さ (int)
     */
    public int getArea() {
        return getHeight() * getXWidth() * getZWidth();
    }

    /**
     * 指定領域内のブロックをリストに入れて返す
     *
     * @return 領域内のブロックリスト
     */
    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();

        // 全軸を最小値から最大値まで回す
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); y <= max.getBlockZ(); z++) {
                    blocks.add(this.getWorld().getBlockAt(x, y, z)); // リストにブロックを追加
                }
            }
        }

        return blocks;
    }

    /* getter / setter */
    /**
     * ワールドを返す
     *
     * @return
     */
    public World getWorld() {
        return min.getWorld();
    }

    /**
     * 対角点1を返す
     *
     * @return pos1
     */
    public Location getPos1() {
        return min;
    }

    /**
     * 対角点2を返す
     *
     * @return pos2
     */
    public Location getPos2() {
        return max;
    }
}
