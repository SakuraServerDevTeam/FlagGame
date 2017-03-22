/*
 * Copyright (C) 2017 toyblocks
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

import java.nio.file.Path;
import jp.llv.nbt.storage.LoadOption;
import org.bukkit.Bukkit;
import syam.flaggame.game.Stage;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author toyblocks
 */
public enum RollbackTarget implements StructureRollbacker {

    ENTITIES(new FileRollbacker(Bukkit.getServer(),
            LoadOption.REMOVE_ENTITIES_FIRST,
            LoadOption.LOAD_BLOCKS,
            LoadOption.LOAD_ENTITIES
    )),
    BLOCKS(new FileRollbacker(Bukkit.getServer(),
            LoadOption.LOAD_BLOCKS
    )),
    LEGACY(new LegacyRollbacker()),
    NONE(new VoidRollbacker()),;

    private final StructureRollbacker serializer;

    private RollbackTarget(StructureRollbacker serializer) {
        this.serializer = serializer;
    }

    @Override
    public byte[] serialize(Stage stage, Cuboid area) throws RollbackException {
        return serializer.serialize(stage, area);
    }

    @Override
    public void deserialize(Stage stage, Cuboid area, byte[] source) throws RollbackException {
        serializer.deserialize(stage, area, source);
    }

}
