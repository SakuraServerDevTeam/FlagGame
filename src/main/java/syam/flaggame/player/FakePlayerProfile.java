/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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
package syam.flaggame.player;

import java.util.UUID;

/**
 *
 * @author Toyblocks
 */
public class FakePlayerProfile implements PlayerProfile {

    @Override
    public void save() {
    }

    @Override
    public UUID getUUID() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "FAKE";
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public void setKill(int kill) {
    }

    @Override
    public int getKill() {
        return 0;
    }

    @Override
    public void setDeath(int death) {
    }

    @Override
    public int getDeath() {
        return 0;
    }

    @Override
    public void setPlayed(int played) {
    }

    @Override
    public int getPlayed() {
        return 0;
    }

    @Override
    public void setExited(int exited) {
    }

    @Override
    public int getExited() {
        return 0;
    }

    @Override
    public void setWonGame(int wonGame) {
    }

    @Override
    public int getWonGame() {
        return 0;
    }

    @Override
    public void setLostGame(int lostGame) {
    }

    @Override
    public int getLostGame() {
        return 0;
    }

    @Override
    public void setBrokenFlag(int times) {
    }

    @Override
    public int getBrokenFlag() {
        return 0;
    }

    @Override
    public void setPlacedFlag(int times) {
    }

    @Override
    public int getPlacedFlag() {
        return 0;
    }

    @Override
    public void setLastPlayedAt(long epoch) {
    }

    @Override
    public long getLastPlayedAt() {
        return 0;
    }

    @Override
    public void setBrokenNexus(int times) {
    }

    @Override
    public int getBrokenNexus() {
        return 0;
    }

    @Override
    public void setCapturedBanner(int times) {
    }

    @Override
    public int getCapturedBanner() {
        return 0;
    }
    
}
