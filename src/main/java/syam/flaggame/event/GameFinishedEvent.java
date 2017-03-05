/* 
 * Copyright (C) 2017 Syamn, SakuraServerDev
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
package syam.flaggame.event;

import java.util.Map;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import jp.llv.flaggame.reception.TeamColor;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;

/**
 * GameFinishedEvent (GameFinishedEvent.java)
 *
 * @author syam(syamn)
 */
public class GameFinishedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Stage stage;
    private GameResult result;
    private TeamColor winTeam;
    private String reason;
    private Map<TeamColor, Set<GamePlayer>> players;

    /**
     * コンストラクタ
     *
     * @param stage
     * @param result
     * @param winTeam
     * @param reason
     * @param playersMap
     */
    public GameFinishedEvent(Stage stage, GameResult result, TeamColor winTeam, String reason, Map<TeamColor, Set<GamePlayer>> playersMap) {
        this.stage = stage;
        this.result = result;
        this.winTeam = winTeam;
        this.reason = reason;
        this.players = playersMap;
    }

    public GameFinishedEvent(Stage stage, GameResult result, TeamColor winTeam, Map<TeamColor, Set<GamePlayer>> playersMap) {
        this(stage, result, winTeam, null, playersMap);
    }

    public Stage getStage() {
        return this.stage;
    }

    public GameResult getResult() {
        return this.result;
    }

    public TeamColor getWinTeam() {
        return this.winTeam;
    }

    public String getReason() {
        return this.reason;
    }

    public Map<TeamColor, Set<GamePlayer>> getPlayers() {
        return this.players;
    }

    /* ******************** */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum GameResult {
        TEAM_WIN, DRAW, STOP,;
    }

}
