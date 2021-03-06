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

/**
 *
 * @author toyblocks
 */
public enum GamePermission {

    BREAK,
    PLACE,
    BUCKET_FILL,
    BUCKET_EMPTY,
    SHEAR,
    BURN,
    FADE,
    FORM,
    GROW,
    IGNITE,
    DECAY,
    HANGING_PLACE,
    HANGING_BREAK,
    CRAFT,
    ARMOR_STAND,
    VEHICLE_ENTER,
    VEHICLE_EXIT,
    VEHICLE_DAMAGE,
    ENTITY_DAMAGE,
    GODMODE,
    REGENERATION,
    DAMAGE,
    CONTAINER,
    DOOR,
    WALL_KICK,
    FEATHER_FALL,
    SUPER_JUMP,
    ;

    public static GamePermission of(String name) {
        return valueOf(name.toUpperCase());
    }

}
