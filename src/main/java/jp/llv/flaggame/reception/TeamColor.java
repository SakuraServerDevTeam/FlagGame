/* 
 * Copyright (C) 2017 Syamn, SakuraServerDev
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
package jp.llv.flaggame.reception;

import org.bukkit.DyeColor;
import org.bukkit.boss.BarColor;

/**
 * ゲームチーム
 *
 * @author syam
 */
public enum TeamColor {

    RED("赤", 0xE, 'c', DyeColor.RED, BarColor.RED),
    BLUE("青", 0xB, '9', DyeColor.BLUE, BarColor.BLUE),
    GREEN("緑", 0x5, 'a', DyeColor.GREEN, BarColor.GREEN),
    ORANGE("橙", 0x1, '6', DyeColor.ORANGE, BarColor.YELLOW),
    PINK("桃", 0x6, 'd', DyeColor.PINK, BarColor.PINK),
    SKYBLUE("水", 0x3, 'b', DyeColor.LIGHT_BLUE, BarColor.WHITE),
    YELLOW("黄", 0x4, 'e', DyeColor.YELLOW, BarColor.YELLOW),
    PURPLE("紫", 0x2, '5', DyeColor.PURPLE, BarColor.PURPLE);

    private static final char COLOR_PREFIX = '\u00A7';
    private final String teamName;
    private final byte blockData;
    private final char colorTag;
    private final DyeColor dyeColor;
    private final BarColor barColor;

    private TeamColor(String teamName, int blockData, char colorTag, DyeColor dyeColor, BarColor barColor) {
        this.teamName = teamName;

        if (blockData < Byte.MIN_VALUE || blockData > Byte.MAX_VALUE) {
            blockData = Byte.MIN_VALUE;
        }

        this.blockData = (byte) blockData;

        this.colorTag = colorTag;
        this.dyeColor = dyeColor;
        this.barColor = barColor;
    }

    /**
     * このチームの名前を返す
     *
     * @return
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * このチームのブロックデータ値を返す
     *
     * @return
     */
    public byte getBlockData() {
        return blockData;
    }

    /**
     * チームの色タグ "&(char)" を返す
     *
     * @return
     */
    public String getColor() {
        return new StringBuilder().append(COLOR_PREFIX).append(this.colorTag).toString();
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    public BarColor getBarColor() {
        return barColor;
    }

    public String getRichName() {
        return this.getColor() + this.getTeamName() + "チーム&r";
    }

    public static TeamColor getByColorData(byte data) {
        for (TeamColor t : values()) {
            if (t.blockData == data) {
                return t;
            }
        }
        return null;
    }

    public static TeamColor of(String name) {
        return valueOf(name.toUpperCase());
    }

}
