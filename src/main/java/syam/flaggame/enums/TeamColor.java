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
package syam.flaggame.enums;

/**
 * ゲームチーム
 *
 * @author syam
 */
public enum TeamColor {

    RED("赤", 0xE, 'c'),
    BLUE("青", 0xB, '9'),
    GREEN("緑", 0x5, 'a'),
    ORANGE("橙", 0x1, '6'),
    PINK("桃", 0x6, 'd'),
    SKYBLUE("水", 0x3, 'b'),
    YELLOW("黄", 0x4, 'e'),
    PURPLE("紫", 0x2, '5');

    private static final char COLOR_PREFIX = '\u00A7';
    private final String teamName;
    private final byte blockData;
    private final char colorTag;

    private TeamColor(String teamName, int blockData, char colorTag) {
        this.teamName = teamName;

        if (blockData < Byte.MIN_VALUE || blockData > Byte.MAX_VALUE) {
            blockData = Byte.MIN_VALUE;
        }

        this.blockData = (byte) blockData;

        this.colorTag = colorTag;
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
