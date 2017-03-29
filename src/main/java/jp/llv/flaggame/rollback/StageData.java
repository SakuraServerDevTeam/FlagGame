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
package jp.llv.flaggame.rollback;

import java.util.function.Consumer;
import org.bukkit.World;
import syam.flaggame.FlagGame;
import syam.flaggame.game.Stage;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author SakuraServerDev
 */
public abstract class StageData {

    public abstract void read(FlagGame plugin, World world, byte[] data) throws RollbackException;

    public abstract byte[] write(FlagGame plugin, World world) throws RollbackException;

    public abstract SerializeTask save(FlagGame plugin, Stage stage, Cuboid area, Consumer<RollbackException> callback);

    public abstract SerializeTask load(FlagGame plugin, Stage stage, Cuboid area, Consumer<RollbackException> callback);

    public abstract StageDataType getType();
    
}
