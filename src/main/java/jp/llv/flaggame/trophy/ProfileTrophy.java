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
package jp.llv.flaggame.trophy;

import java.util.Objects;
import javax.script.ScriptException;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.profile.PlayerProfile;

/**
 *
 * @author toyblocks
 */
public class ProfileTrophy extends NashornTrophy {
    
    public static final String TYPE_NAME = "profile";
    
    public ProfileTrophy(String name) {
        super(name);
    }

    @Override
    public String getType() {
        return TYPE_NAME;
    }

    public boolean test(FlagGameAPI api, PlayerProfile profile) throws ScriptException {
        return super.test(api, bindings -> {
            bindings.put("profile", Objects.requireNonNull(profile));
        });
    }
    
}
