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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import jp.llv.nbt.CuboidSerializable;
import jp.llv.nbt.LocationSerializable;
import jp.llv.nbt.StructureLibAPI;
import jp.llv.nbt.storage.LoadOption;
import jp.llv.nbt.storage.async.AsyncSerializeTask;
import jp.llv.nbt.storage.async.AsyncStorageType;
import jp.llv.nbt.tag.TagCompound;
import org.bukkit.World;
import syam.flaggame.FlagGame;
import syam.flaggame.game.Stage;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author SakuraServerDev
 */
public class StructureLibStageData extends CachedStageData<TagCompound> {

    private static final int DEFAULT_STEPS_PER_TICK = 0xf00;

    private final boolean entities;
    private final boolean gzip;

    public StructureLibStageData(boolean entities, boolean gzip) {
        this.entities = entities;
        this.gzip = gzip;
    }

    @Override
    protected TagCompound read(FlagGame plugin, World world, InputStream is) throws IOException, RollbackException {
        TagCompound result = new TagCompound();
        result.read(is, gzip);
        return result;
    }

    @Override
    protected void write(FlagGame plugin, World world, OutputStream os, TagCompound cache) throws IOException, RollbackException {
        cache.write(os, gzip);
    }

    @Override
    protected SerializeTask deserialize(FlagGame plugin, Stage stage, Cuboid area, TagCompound source, Consumer<RollbackException> callback) {
        return new StructureLibStageDataDeserializeTask(area.serialize().getOrigin(), source, callback);
    }

    @Override
    public SerializeTask save(FlagGame plugin, Stage stage, Cuboid area, Consumer<RollbackException> callback) {
        return new StructureLibStageDataSerializeTak(callback, area.serialize());
    }

    @Override
    public StageDataType getType() {
        return entities ? StageDataType.SL_ENTITIES : StageDataType.SL_BLOCKS;
    }

    private class StructureLibStageDataDeserializeTask extends SerializeTask {

        private final AsyncSerializeTask<Void> task;

        public StructureLibStageDataDeserializeTask(LocationSerializable origin, TagCompound source, Consumer<RollbackException> callback) {
            super(callback);
            StructureLibAPI api = StructureLibAPI.Version.getDetectedVersion(origin.getWorld());
            task = entities
                    ? AsyncStorageType.STRUCTURE.load(api, source, origin,
                            LoadOption.REMOVE_ENTITIES_FIRST,
                            LoadOption.LOAD_BLOCKS,
                            LoadOption.LOAD_ENTITIES
                    ) : AsyncStorageType.STRUCTURE.load(api, source, origin,
                            LoadOption.LOAD_BLOCKS
                    );
        }

        @Override
        public void step() throws RollbackException {
            for (int i = 0; i < DEFAULT_STEPS_PER_TICK; i++) {
                task.step();
                if (task.isFinished()) {
                    return;
                }
            }
        }

        @Override
        public long getEstimatedTickRemaining() {
            return task.getEstimatedStepsRemaining() / DEFAULT_STEPS_PER_TICK;
        }

        @Override
        public boolean isFinished() {
            return task.isFinished();
        }

    }

    private class StructureLibStageDataSerializeTak extends CachableSerializeTask {

        private final AsyncSerializeTask<TagCompound> task;

        public StructureLibStageDataSerializeTak(Consumer<RollbackException> callback, CuboidSerializable cuboid) {
            super(callback);
            StructureLibAPI api = StructureLibAPI.Version.getDetectedVersion(cuboid.getOrigin().getWorld());
            task = AsyncStorageType.STRUCTURE.save(api, cuboid, entities);
        }

        @Override
        public TagCompound call() throws RollbackException {
            for (int i = 0; i < DEFAULT_STEPS_PER_TICK; i++) {
                task.step();
                if (task.isFinished()) {
                    return task.getResult();
                }
            }
            return null;
        }

        @Override
        public long getEstimatedTickRemaining() {
            return task.getEstimatedStepsRemaining() / DEFAULT_STEPS_PER_TICK;
        }

    }

}
