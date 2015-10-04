/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.enums;

import syam.flaggame.exception.FlagGameException;

/**
 * ゲームチーム
 *
 * @author syam
 */
public enum TeamColor {

    RED("赤", 14, "&c"), // 赤チーム
    BLUE("青", 11, "&b"), // 青チーム
    ;

    private final String teamName;
    private final byte blockData;
    private final String colorTag;

    TeamColor(String teamName,int blockData, String colorTag) {
        this.teamName = teamName;
        
        if (blockData < Byte.MIN_VALUE || blockData > Byte.MAX_VALUE) {
            blockData = Byte.MIN_VALUE;
        }
        
        this.blockData = (byte) blockData;

        this.colorTag = colorTag;
    }

    /**
     * このチームの名前を返す
     *
     * @return
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * このチームのブロックデータ値を返す
     *
     * @return
     */
    public byte getBlockData() {
        return blockData;
    }

    /**
     * チームの色タグ "&(char)" を返す
     *
     * @return
     */
    public String getColor() {
        return colorTag;
    }

    /**
     * 相手のGameTeamを返す
     *
     * @return GameTeam
     */
    public TeamColor getAgainstTeam() {
        return getAgainstTeam(this);
    }

    /*
     * 相手のGameTeamを返す
     * 
     * @return GameTeam
     */
    public static TeamColor getAgainstTeam(final TeamColor team) {
        if (team.equals(TeamColor.RED)) {
            return TeamColor.BLUE;
        } else if (team.equals(TeamColor.BLUE)) {
            return TeamColor.RED;
        } else {
            String error = "Request team is not defined";
            if (team != null) {
                error += ": " + team.name();
            }
            throw new FlagGameException(error);
        }
    }

    public static TeamColor getByColorData(byte data) {
        for (TeamColor t : values()) {
            if (t.blockData == data) {
                return t;
            }
        }
        return null;
    }
}
