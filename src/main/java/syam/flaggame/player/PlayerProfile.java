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
public interface PlayerProfile {
    
    void save();
    
    UUID getUUID();
    
    /*=====*/
    
    void setName(String name);
    
    String getName();
    
    /*=====*/
    
    void setKill(int kill);
    
    int getKill();
    
    default void addKill() {
        setKill(getKill()+1);
    }
    
    void setDeath(int death);
    
    int getDeath();
    
    default void addDeath() {
        setDeath(getDeath()+1);
    }
    
    /*=====*/
    
    void setPlayed(int played);
    
    int getPlayed();
    
    default void addPlayed() {
        setPlayed(getPlayed()+1);
    }
    
    default void decreasePlayed() {
        setPlayed(getPlayed()-1);
    }
    
    void setExited(int exited);
    
    int getExited();
    
    default void addExited() {
        setExited(getExited()+1);
    }
    
    /*=====*/
    
    void setWonGame(int wonGame);
    
    int getWonGame();
    
    default void addWonGame() {
        setWonGame(getWonGame()+1);
    }
    
    void setLostGame(int lostGame);
    
    int getLostGame();
    
    default void addLostGame() {
        setLostGame(getLostGame()+1);
    }
    
    /*=====*/
    
    void setBrokenFlag(int times);
    
    int getBrokenFlag();
    
    default void addBrokenFlag() {
        setBrokenFlag(getBrokenFlag()+1);
    }
    
    void setPlacedFlag(int times);
    
    int getPlacedFlag();
    
    default void addPlacedFlag() {
        setBrokenFlag(getBrokenFlag()+1);
    }
    
    void setBrokenNexus(int times);
    
    int getBrokenNexus();
    
    default void addBrokenNexus() {
        setBrokenNexus(getBrokenNexus()+1);
    }
    
    /*=====*/
    
    void setCapturedBanner(int times);
    
    int getCapturedBanner();
    
    default void addCapturedBanner() {
        setCapturedBanner(getCapturedBanner()+1);
    }
    
    /*=====*/
    
    void setLastPlayedAt(long epoch);
    
    default void setLastPlayedAtNow() {
        this.setLastPlayedAt(System.currentTimeMillis());
    }
    
    long getLastPlayedAt();
    
    /*=====*/
    
    default int getDrewGame() {
        return this.getPlayed() - this.getExited() - this.getWonGame() - this.getLostGame();
    }
    
    default double getKD() {
        int d = getDeath();
        return getKill() / (d == 0 ? 1 : d);
    }
    
    default String getFormattedKD() {
        double kd = getKD();
        String cc = "&7"; // 灰色 (1.0 or 0.0)
        if (kd > 1.0D) {
            cc = "&a"; // 緑色 (1+)
        } else if (kd < 1.0D && kd != 0.0D) {
            cc = "&c"; // 赤色 (1-)
        }
        return cc + String.format("%.3f", kd);
    }
    
    default double getWinningRate() {
        int l = this.getPlayed();
        return getWonGame() / (l == 0 ? 1 : l);
    }
    
    default String getFormattedWinningRate() {
        double wl = getWinningRate();
        String cc = wl >= 0.5D ? "&a" : "&c";
        return cc + String.format("%.3f", wl);
    }
    
}
