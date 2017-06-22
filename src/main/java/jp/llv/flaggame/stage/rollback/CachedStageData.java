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
package jp.llv.flaggame.stage.rollback;

import jp.llv.flaggame.api.stage.rollback.SerializeTask;
import jp.llv.flaggame.api.stage.rollback.StageData;
import jp.llv.flaggame.api.exception.RollbackException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Consumer;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.stage.Stage;
import org.bukkit.World;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author SakuraServerDev
 * @param <C> cache data type
 */
public abstract class CachedStageData<C> implements StageData {

    private long loadTiming = 0L;
    private C cache;

    public long getLoadTiming() {
        return loadTiming;
    }

    public void setLoadTiming(long loadTiming) {
        if (loadTiming < 0) {
            throw new IllegalArgumentException("Negative load timing");
        }
        this.loadTiming = loadTiming;
    }

    protected abstract C read(FlagGameAPI api, World world, InputStream is) throws IOException, RollbackException;

    @Override
    public void read(FlagGameAPI api, World world, byte[] data) throws RollbackException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            cache = this.read(api, world, bis);
        } catch (IOException ex) {
            throw new RollbackException(ex);
        }
    }

    protected abstract void write(FlagGameAPI api, World world, OutputStream os, C cache) throws IOException, RollbackException;

    @Override
    public byte[] write(FlagGameAPI api, World world) throws RollbackException {
        Objects.requireNonNull(cache);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            this.write(api, world, bos, cache);
            return bos.toByteArray();
        } catch (IOException ex) {
            throw new RollbackException(ex);
        }
    }

    protected abstract SerializeTask deserialize(Stage stage, Cuboid area, C source, Consumer<RollbackException> callback);

    @Override
    public SerializeTask load(Stage stage, Cuboid area, Consumer<RollbackException> callback) {
        Objects.requireNonNull(cache);
        return this.deserialize(stage, area, cache, callback);
    }

    protected abstract class CachableSerializeTask extends SerializeTask {

        private boolean finished = false;

        public CachableSerializeTask(Consumer<RollbackException> callback) {
            super(callback);
        }

        @Override
        public void step() throws RollbackException {
            C result = call();
            if (result != null) {
                CachedStageData.this.cache = result;
                finished = true;
            }
        }

        public abstract C call() throws RollbackException;

        @Override
        public boolean isFinished() {
            return finished;
        }

    }

}
