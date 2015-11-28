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
package syam.flaggame.enums.config;

/**
 * ゲーム毎に設定可能な設定を表す /flag set コマンドで使用する列挙クラス
 *
 * @author syam
 */
public enum Configables {

    // 一般

    STAGE("ステージエリア", ConfigType.AREA), // エリア保護を行うステージ全体の領域設定
    BASE("拠点エリア", ConfigType.AREA), // 各チーム拠点の領域設定
    SPAWN("スポーン地点", ConfigType.POINT), // 各チームのスポーン地点
    FLAG("フラッグ", ConfigType.MANAGER), // 各チーム拠点の領域設定
    CHEST("チェスト", ConfigType.MANAGER), // 各チーム拠点の領域設定

    SPECSPAWN("観戦者スポーン地点", ConfigType.POINT), // 観戦時にテレポートする位置 SPECTATE / SPEC /
    // SSPAWN ..etc?

    // オプション
    GAMETIME("ゲームの制限時間(秒)", ConfigType.SIMPLE),
    TEAMLIMIT("チーム毎の人数制限", ConfigType.SIMPLE),
    PROTECT("ステージ保護", ConfigType.SIMPLE),
    AVAILABLE("ステージ有効", ConfigType.SIMPLE),;

    private final String configName;
    private final ConfigType configType;

    private Configables(String configName, ConfigType configType) {
        this.configName = configName;
        this.configType = configType;
    }

    /**
     * 設定名を返す
     *
     * @return String
     */
    public String getConfigName() {
        return this.configName;
    }

    /**
     * 設定種類を返す
     *
     * @return ConfigType
     */
    public ConfigType getConfigType() {
        return this.configType;
    }
}
