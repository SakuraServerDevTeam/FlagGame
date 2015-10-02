package syam.flaggame.enums;

import syam.flaggame.exception.FlagGameException;

/**
 * ゲームチーム
 *
 * @author syam
 */
public enum GameTeam {

    RED("赤", 35, 14, "&c"), // 赤チーム
    BLUE("青", 35, 11, "&b"), // 青チーム
    ;

    private final String teamName;
    private final int blockID;
    private final byte blockData;
    private final String colorTag;

    GameTeam(String teamName, int blockID, int blockData, String colorTag) {
        this.teamName = teamName;

        // 例外回避
        if (blockID < 0) {
            blockID = 0;
        }
        if (blockData < 0 || blockData > 127) {
            blockData = 0;
        }

        this.blockID = blockID;
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
    public GameTeam getAgainstTeam() {
        return getAgainstTeam(this);
    }

    /*
     * 相手のGameTeamを返す
     * 
     * @return GameTeam
     */
    public static GameTeam getAgainstTeam(final GameTeam team) {
        if (team.equals(GameTeam.RED)) {
            return GameTeam.BLUE;
        } else if (team.equals(GameTeam.BLUE)) {
            return GameTeam.RED;
        } else {
            String error = "Request team is not defined";
            if (team != null) {
                error += ": " + team.name();
            }
            throw new FlagGameException(error);
        }
    }

    public static GameTeam getByColorData(byte data) {
        for (GameTeam t : values()) {
            if (t.blockData == data) {
                return t;
            }
        }
        return null;
    }
}
