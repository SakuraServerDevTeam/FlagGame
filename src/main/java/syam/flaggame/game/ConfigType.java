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
package syam.flaggame.game;

/**
 * Configable列挙に紐付ける設定種類を表す列挙クラスです
 * 
 * @author syam
 */
public enum ConfigType {
    AREA, // エリア指定を行う設定
    POINT, // プレイヤーの現在値を取得する設定
    MANAGER, // マネージャモードに入る設定
    SIMPLE, // お金など単にそのコマンドだけで変更可能な設定
    ;
}
