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
package jp.llv.flaggame.api.stage.permission;

import java.util.Locale;
import jp.llv.flaggame.util.StringUtil;
import net.md_5.bungee.api.ChatColor;

/**
 *
 * @author toyblocks
 */
public enum GamePermissionState {
    ALLOW(true, true, ChatColor.GREEN),
    DENY(false, true, ChatColor.RED),
    DEFAULT(false, false, ChatColor.GOLD),;

    private final ChatColor color;
    private final boolean positive;
    private final boolean forceful;

    private GamePermissionState(boolean positive, boolean forceful, ChatColor color) {
        this.positive = positive;
        this.forceful = forceful;
        this.color = color;
    }

    public boolean isPositive() {
        return positive;
    }

    public boolean isForceful() {
        return forceful;
    }

    public String format() {
        return StringUtil.capitalize(name());
    }

    public ChatColor getColor() {
        return color;
    }

    public static GamePermissionState of(String name) {
        return valueOf(name.toUpperCase(Locale.getDefault()));
    }

}
