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
package jp.llv.flaggame.api.profile;

import java.util.UUID;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.stage.Stage;

/**
 *
 * @author toyblocks
 */
public interface ProfileAPI {

    PlayerProfile getProfile(UUID uuid);

    StageProfile getProfile(String name);

    void loadPlayerProfile(UUID uuid, boolean notifyLevelUp);

    void loadPlayerProfiles(Iterable<? extends GamePlayer> players, boolean notifyLevelUp);

    void loadStageProfile(String stage);

    void loadStageProfile(Stage stage);

}
