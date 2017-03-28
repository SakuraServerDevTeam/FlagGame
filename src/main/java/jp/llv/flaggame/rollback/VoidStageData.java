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
import jp.llv.flaggame.rollback.SerializeTask.CompletedSerializeTask;
import org.bukkit.World;
import syam.flaggame.FlagGame;
import syam.flaggame.game.Stage;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author SakuraServerDev
 */
public class VoidStageData extends StageData {

    @Override
    public void read(FlagGame plugin, World world, byte[] data) {
    }

    @Override
    public byte[] write(FlagGame plugin, World world) {
        return new byte[0];
    }

    @Override
    public SerializeTask save(FlagGame plugin, Stage stage, Cuboid area, Consumer<RollbackException> callback) {
        return new CompletedSerializeTask(callback);
    }

    @Override
    public SerializeTask load(FlagGame plugin, Stage stage, Cuboid area, Consumer<RollbackException> callback) {
        return new CompletedSerializeTask(callback);
    }

    @Override
    public StageDataType getType() {
        return StageDataType.NONE;
    }

}
