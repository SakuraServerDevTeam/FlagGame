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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import jp.llv.flaggame.api.reception.TeamColor;

public class Flag extends StageObjective {

    public static final Material[] FLAG_BLOCK_IDS = {Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS};
    private final double type; // フラッグの種類
    private final boolean producing;

    /*
     * コンストラクタ
     * 
     * @param plugin
     */
    public Flag(final Location loc, final double type, boolean producing) {
        super(loc, ObjectiveType.FLAG, true);
        this.type = type;
        this.producing = producing;
    }

    /**
     * 今のブロックデータを返す
     *
     * @return Block
     */
    public Block getNowBlock() {
        return getLocation().getBlock();
    }

    /**
     * ブロックを元のブロックにロールバックする
     */
    public void rollback() {
        Block block = getNowBlock();
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
    public double getFlagPoint() {
        return type;
    }

    public boolean isProducing() {
        return producing;
    }

    public String getTypeName() {
        return Double.toString(type);
    }

    public TeamColor getOwner() {
        Block b = getNowBlock();
        if (!isFlag(b.getType())) {
            return null;
        }
        return TeamColor.getByColorData(b.getData());
    }

    public static boolean isFlag(Material material) {
        for (Material m : FLAG_BLOCK_IDS) {
            if (material == m) {
                return true;
            }
        }
        return false;
    }
}
