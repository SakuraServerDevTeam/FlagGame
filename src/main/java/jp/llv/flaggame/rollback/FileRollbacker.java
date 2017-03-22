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

import java.io.IOException;
import java.nio.file.Path;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.LocationSerializable;
import jp.llv.nbt.StructureLibAPI;
import jp.llv.nbt.storage.LoadOption;
import jp.llv.nbt.storage.StorageType;
import jp.llv.nbt.tag.TagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import syam.flaggame.game.Stage;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author toyblocks
 */
public class FileRollbacker implements StructureRollbacker {

    private static final StorageType STRUCTURE = StorageType.STRUCTURE;
    private final StructureLibAPI api;
    private final LoadOption[] options;

    public FileRollbacker(Server server, LoadOption ... options) {
        this.api = StructureLibAPI.Version.getDetectedVersion(Bukkit.getServer());
        this.options = options;
    }
    
    
    @Override
    public byte[] serialize(Stage stage, Cuboid area) throws RollbackException {
        try {
            return STRUCTURE.save(api, area.serialize(), true).write(true);
        } catch(IncompatiblePlatformException | IOException ex) {
            throw new RollbackException(ex);
        }
    }

    @Override
    public void deserialize(Stage stage, Cuboid area, byte[] source) throws RollbackException {
        try {
            TagCompound data = new TagCompound();
            data.read(source, true);
            STRUCTURE.load(api, data, new LocationSerializable(area.getPos1()), options);
        } catch(IncompatiblePlatformException | IOException ex) {
            throw new RollbackException(ex);
        }
    }

}
