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
package jp.llv.flaggame.trophie;

import javax.script.ScriptException;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.profile.RecordType;
import jp.llv.flaggame.profile.record.PlayerRecord;

/**
 *
 * @author toyblocks
 */
public class RecordTrophie extends NashornTrophie {
    
    public static final String TYPE_NAME = "record";

    private RecordType target = null;

    public RecordTrophie(String name) {
        super(name);
    }

    @Override
    public String getType() {
        return TYPE_NAME;
    }

    public RecordType getTarget() {
        return target;
    }

    public void setTarget(RecordType target) {
        this.target = target;
    }

    public boolean test(FlagGameAPI api, PlayerRecord record) throws ScriptException {
        if (record.getType() != target) {
            return false;
        }
        return super.test(api, bindings -> {
            bindings.put("record", record);
        });
    }

}
