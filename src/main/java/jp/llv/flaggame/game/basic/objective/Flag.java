/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import syam.flaggame.enums.TeamColor;

public class Flag {
    
    private static final int[] FLAG_BLOCK_IDS = {35};
    private final Location loc; // フラッグ座標
    private final byte type; // フラッグの種類

    /*
     * コンストラクタ
     * 
     * @param plugin
     */
    public Flag(final Location loc, final byte type) {
        if (loc == null) throw new NullPointerException();

        // フラッグデータ登録
        this.loc = loc;
        this.type = type;
    }

    /**
     * 今のブロックデータを返す
     * 
     * @return Block
     */
    public Block getNowBlock() {
        return loc.getBlock();
    }

    /**
     * ブロックを元のブロックにロールバックする
     */
    public void rollback() {
        Block block = loc.getBlock();
        // 既に同じブロックの場合は何もしない
        if (block.getTypeId() != 0 || block.getData() != 0) {
            // ブロック変更
            block.setTypeIdAndData(0, (byte) 0, false);
        }
    }

    /* フラッグ設定系 */
    /**
     * このフラッグの点数を返す
     * 
     * @return フラッグの点数
     */
    public byte getFlagPoint() {
        return type;
    }

    public String getTypeName() {
        return Byte.toString(type);
    }

    public Location getLocation() {
        return loc;
    }
    
    public TeamColor getOwner() {
        Block b = this.loc.getBlock();
        if (!isFlag(b.getType())) {
            return null;
        }
        return TeamColor.getByColorData(b.getData());
    }
    
    public static boolean isFlag(Material material) {
        for (int id : FLAG_BLOCK_IDS) {
            if (material.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
