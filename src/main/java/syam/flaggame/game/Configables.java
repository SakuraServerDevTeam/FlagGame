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
package syam.flaggame.game;

/**
 * ゲーム毎に設定可能な設定を表す /flag set コマンドで使用する列挙クラス
 *
 * @author syam
 */
public enum Configables {

    // 一般
    SPAWN(ConfigType.POINT),
    FLAG(ConfigType.MANAGER),
    CHEST(ConfigType.MANAGER),
    NEXUS(ConfigType.MANAGER),
    BANNER_SPAWNER(ConfigType.MANAGER),
    BANNER_SLOT(ConfigType.MANAGER),
    SPECSPAWN(ConfigType.POINT),

    // オプション
    GAMETIME(ConfigType.SIMPLE),
    TEAMLIMIT(ConfigType.SIMPLE),
    AVAILABLE(ConfigType.SIMPLE),
    PROTECT(ConfigType.SIMPLE),
    KILLSCORE(ConfigType.SIMPLE),
    DEATHSCORE(ConfigType.SIMPLE),
    COOLDOWN(ConfigType.SIMPLE),
    
    // ショートカット
    STAGE(ConfigType.AREA),
    BASE(ConfigType.AREA),
    
    // ステージ解説
    AUTHOR(ConfigType.SIMPLE),
    DESCRIPTION(ConfigType.SIMPLE),
    GUIDE(ConfigType.SIMPLE),;

    private final ConfigType configType;

    private Configables(ConfigType configType) {
        this.configType = configType;
    }

    public ConfigType getType() {
        return this.configType;
    }

    public enum ConfigType {
        POINT, // プレイヤーの現在値を取得する設定
        MANAGER, // マネージャモードに入る設定
        SIMPLE, // お金など単にそのコマンドだけで変更可能な設定
        AREA, // 範囲設定
    }
}
