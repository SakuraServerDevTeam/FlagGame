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
package jp.llv.flaggame.profile.record;

import java.util.function.Function;
import org.bson.Document;

/**
 *
 * @author toyblocks
 */
public enum RecordType {
    RECEPTION_OPEN("受付開始", ReceptionOpenRecord::new),
    RECEPTION_CLOSE("受付終了", ReceptionCloseRecord::new),
    GAME_START("開始", GameStartRecord::new),
    GAME_FINISH("終了", GameFinishRecord::new),
    LOGIN("ログイン", LoginRecord::new),
    LOGOUT("ログアウト", LogoutRecord::new),
    ENTRY("参加", PlayerEntryRecord::new),
    LEAVE("辞退", PlayerLeaveRecord::new),
    TEAM("チーム参加", PlayerTeamRecord::new),
    KILL("キル", PlayerKillRecord::new),
    DEATH("デス", PlayerDeathRecord::new),
    WIN("勝利", PlayerWinRecord::new),
    LOSE("敗北", PlayerLoseRecord::new),
    DRAW("引き分け", PlayerDrawRecord::new),
    FLAG_CAPTURE("フラッグ獲得", FlagCaptureRecord::new),
    FLAG_BREAK("フラッグ破壊", FlagBreakRecord::new),
    FLAG_SCORE("フラッグ確定", FlagScoreRecord::new),
    BANNER_HOLD("バナー獲得", BannerGetRecord::new),
    BANNER_DEPLOY("バナー設置", BannerDeployRecord::new),
    BANNER_STEAL("バナー強奪", BannerStealRecord::new),
    BANNER_KEEP("バナー死守", BannerKeepRecord::new),
    NEXUS_BREAK("コア破壊", NexusBreakRecord::new),
    RATE("評価", StageRateRecord::new),;
    
    public static final String FIELD_TYPE = "type";
    
    private final String name;
    private final Function<Document, ? extends GameRecord> constructor;
    
    private RecordType(String name, Function<Document, ? extends GameRecord> constructor) {
        this.name = name;
        this.constructor = constructor;
    }

    public String getName() {
        return name;
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
