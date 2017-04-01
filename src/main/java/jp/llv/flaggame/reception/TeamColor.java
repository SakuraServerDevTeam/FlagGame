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
package jp.llv.flaggame.reception;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;

/**
 * ゲームチーム
 *
 * @author syam
 */
public enum TeamColor {

    RED("赤", 0xE, Color.RED, ChatColor.RED, DyeColor.RED, BarColor.RED, Material.RED_SHULKER_BOX),
    BLUE("青", 0xB, Color.BLUE, ChatColor.BLUE, DyeColor.BLUE, BarColor.BLUE, Material.BLUE_SHULKER_BOX),
    GREEN("緑", 0x5, Color.GREEN, ChatColor.GREEN, DyeColor.GREEN, BarColor.GREEN, Material.GREEN_SHULKER_BOX),
    ORANGE("橙", 0x1, Color.ORANGE, ChatColor.GOLD, DyeColor.ORANGE, BarColor.YELLOW, Material.YELLOW_SHULKER_BOX),
    PINK("桃", 0x6, Color.FUCHSIA, ChatColor.LIGHT_PURPLE, DyeColor.PINK, BarColor.PINK, Material.PINK_SHULKER_BOX),
    SKYBLUE("水", 0x3, Color.AQUA, ChatColor.AQUA, DyeColor.LIGHT_BLUE, BarColor.WHITE, Material.LIGHT_BLUE_SHULKER_BOX),
    YELLOW("黄", 0x4, Color.YELLOW, ChatColor.YELLOW, DyeColor.YELLOW, BarColor.YELLOW, Material.YELLOW_SHULKER_BOX),
    PURPLE("紫", 0x2, Color.PURPLE, ChatColor.DARK_PURPLE, DyeColor.PURPLE, BarColor.PURPLE, Material.PURPLE_SHULKER_BOX),
    WHITE("白", 0x0, Color.WHITE, ChatColor.WHITE, DyeColor.WHITE, BarColor.WHITE, Material.WHITE_SHULKER_BOX),
    ;

    private final String teamName;
    private final byte blockData;
    private final Color color;
    private final ChatColor chatColor;
    private final DyeColor dyeColor;
    private final BarColor barColor;
    private final Material shulerColor;

    private TeamColor(String teamName, int blockData, Color color, ChatColor chatColor, DyeColor dyeColor, BarColor barColor, Material shulerColor) {
        this.teamName = teamName;

        if (blockData < Byte.MIN_VALUE || blockData > Byte.MAX_VALUE) {
            blockData = Byte.MIN_VALUE;
        }

        this.blockData = (byte) blockData;

        this.color = color;
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
        this.barColor = barColor;
        this.shulerColor = shulerColor;
    }

    /**
     * このチームの名前を返す
     *
     * @return name of the team
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * このチームのブロックデータ値を返す
     *
     * @return data value corresponding to the team
     */
    public byte getBlockData() {
        return blockData;
    }

    public Color getColor() {
        return color;
    }
    
    /**
     * チームの色タグ "&(char)" を返す
     *
     * @return color code corresponding to the team
     */
    public String getChatColor() {
        return chatColor.toString();
    }
    
    public ChatColor getBungeeChatColor() {
        return chatColor;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    public BarColor getBarColor() {
        return barColor;
    }

    public Material getShulerColor() {
        return shulerColor;
    }

    public String getRichName() {
        return this.getChatColor() + this.getTeamName() + "チーム&r";
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
