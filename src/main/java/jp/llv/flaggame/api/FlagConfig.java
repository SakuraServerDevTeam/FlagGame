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
package jp.llv.flaggame.api;

import java.util.List;

/**
 *
 * @author toyblocks
 */
public interface FlagConfig {

    String getDatabaseAddress();

    String getDatabaseDbname();

    int getDatabasePort();

    String getDatabaseUsername();

    String getDatabaseUserpass();

    boolean getDeathWhenLogout();

    List<String> getDisableCommands();

    boolean getDisableRegainHP();

    boolean getDisableTeamPVP();

    String getGameWorld();

    int getGodModeTime();

    List<String> getPermissions();

    double getScoreBannerBreak();

    double getScoreBannerDeploy();

    double getScoreBannerKeep();

    double getScoreBannerSteal();

    double getScoreCombatDeath();

    double getScoreCombatKill();

    double getScoreFlagBreak();

    double getScoreFlagLastPlace();

    double getScoreFlagPlace();

    double getScoreGameExit();

    double getScoreGameJoin();

    double getScoreNexusBreak();

    long getScoreRate();

    int getStartCountdownInSec();

    int getToolID();

    boolean getUseDynmap();

    boolean getUseFlagEffects();

    double getWallKickPowerXZ();

    double getWallKickPowerY();
    
    double getWallKickPitchLimit();

    boolean isDebug();

    boolean isProtected();

    void loadConfig();
    
}
