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
package syam.flaggame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jp.llv.nbt.CuboidSerializable;
import jp.llv.nbt.LocationSerializable;

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
        if (!Objects.equals(point1.getWorld(), point2.getWorld())) {
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

    public Cuboid(CuboidSerializable cuboid) {
        this(cuboid.getOrigin().toLocation(), cuboid.getCorner().toLocation());
    }

    /**
     * 指定した座標が立体領域内かチェック
     *
     * @param loc チェックする座標
     * @return 領域内ならtrue 違えばfalse
     */
    public boolean contains(Location loc) {
        return this.getWorld().equals(loc.getWorld())
               && min.getX() <= loc.getX() && loc.getX() <= max.getX()
               && min.getY() <= loc.getY() && loc.getY() <= max.getY()
               && min.getZ() <= loc.getZ() && loc.getZ() <= max.getZ();
    }

    public boolean contains(Cuboid other) {
        return min.getX() <= other.getPos1().getX()
               && min.getY() <= other.getPos1().getY()
               && min.getZ() <= other.getPos1().getZ()
               && other.getPos2().getX() <= max.getX()
               && other.getPos2().getY() <= max.getY()
               && other.getPos2().getZ() <= max.getZ();
    }

    /**
     * X軸の幅を取得(int)
     *
     * @return X軸の幅
     */
    public int getXWidth() {
        return max.getBlockX() - min.getBlockX() + 1;
    }

    /**
     * Y軸の幅を取得(int)
     *
     * @return Y座標の幅
     */
    public int getZWidth() {
        return max.getBlockZ() - min.getBlockZ() + 1;
    }

    /**
     * Z幅を取得(int)
     *
     * @return Y座標の幅
     */
    public int getHeight() {
        return max.getBlockY() - min.getBlockY() + 1;
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
     * @return world, where the location is
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

    public CuboidSerializable serialize() {
        return new CuboidSerializable(new LocationSerializable(min), new LocationSerializable(max));
    }

}
