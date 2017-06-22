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
package jp.llv.flaggame.database.mongo;

import com.mongodb.async.SingleResultCallback;
import java.util.Objects;
import jp.llv.flaggame.database.DatabaseCallback;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.database.DatabaseResult;

/**
 *
 * @author SakuraServerDev
 * @param <T> result type
 */
public class MongoDBCallback<T> implements SingleResultCallback<T> {

    private final DatabaseCallback<? super T, ? super DatabaseException> callback;

    public MongoDBCallback(DatabaseCallback<? super T, ? super DatabaseException> callback) {
        Objects.requireNonNull(callback);
        this.callback = callback;
    }

    @Override
    public void onResult(T t, Throwable thrwbl) {
        if (thrwbl == null) {
            this.callback.call(DatabaseResult.success(t));
        } else {
            this.callback.call(DatabaseResult.fail(new DatabaseException(thrwbl)));
        }
    }

}
