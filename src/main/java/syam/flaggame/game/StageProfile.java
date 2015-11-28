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
package syam.flaggame.game;

/**
 * 
 * @author Toyblocks
 */
public class StageProfile {
    
    private Long lastPlayedAt;
    private int played;
    private int kill;
    private int death;
    private int placedFlag;
    private int brokenFlag;

    public Long getLastPlayedAt() {
        return lastPlayedAt;
    }

    public void setLastPlayedAt(Long lastPlayedAt) {
        this.lastPlayedAt = lastPlayedAt;
    }
    
    public void setLastPlayedNow() {
        this.lastPlayedAt = System.currentTimeMillis();
    }

    public int getKill() {
        return kill;
    }

    public void setKill(int kill) {
        this.kill = kill;
    }

    public int getDeath() {
        return death;
    }

    public void setDeath(int death) {
        this.death = death;
    }

    public int getPlacedFlag() {
        return placedFlag;
    }

    public void setPlacedFlag(int placedFlag) {
        this.placedFlag = placedFlag;
    }
    
    public void addPlacedFlag() {
        this.placedFlag++;
    }

    public int getBrokenFlag() {
        return brokenFlag;
    }

    public void setBrokenFlag(int brokenFlag) {
        this.brokenFlag = brokenFlag;
    }
    
    public void addBrokenFlag() {
        this.brokenFlag++;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }
    
    public void addPlayed() {
        this.played++;
    }
    
}
