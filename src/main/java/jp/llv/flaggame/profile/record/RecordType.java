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
package jp.llv.flaggame.profile.record;

import java.util.function.Function;
import org.bson.Document;

/**
 *
 * @author toyblocks
 */
public enum RecordType {
    RECEPTION_OPEN(ReceptionOpenRecord::new),
    RECEPTION_CLOSE(ReceptionCloseRecord::new),
    GAME_START(GameStartRecord::new),
    GAME_FINISH(GameFinishRecord::new),
    PLAYER_LOGIN(LoginRecord::new),
    PLAYER_LOGOUT(LogoutRecord::new),
    PLAYER_ENTRY(PlayerEntryRecord::new),
    PLAYER_LEAVE(PlayerLeaveRecord::new),
    PLAYER_KILL(PlayerKillRecord::new),
    PLAYER_DEATH(PlayerDeathRecord::new),
    PLAYER_WIN(PlayerWinRecord::new),
    PLAYER_LOSE(PlayerLoseRecord::new),
    PLAYER_DRAW(PlayerDrawRecord::new),
    FLAG_CAPTURE(FlagCaptureRecord::new),
    FLAG_BREAK(FlagBreakRecord::new),
    FLAG_SCORE(FlagScoreRecord::new),
    BANNER_HOLD(BannerHoldRecord::new),
    BANNER_DEPLOY(BannerDeployRecord::new),
    NEXUS_BREAK(NexusBreakRecord::new),
    STAGE_RATE(StageRateRecord::new),;
    
    public static final String FIELD_TYPE = "type";
    
    private final Function<Document, ? extends GameRecord> constructor;
    
    private RecordType(Function<Document, ? extends GameRecord> constructor) {
        this.constructor = constructor;
    }
    
    public GameRecord read(Document document) {
        return constructor.apply(document);
    }
    
    public static GameRecord load(Document document) {
        String typeString = document.getString(FIELD_TYPE);
        RecordType type = valueOf(typeString.toUpperCase());
        return type.read(document);
    }
    
}
