package syam.flaggame.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import syam.flaggame.enums.GameTeam;

public class Flag {
    
    private static final int[] FLAG_BLOCK_IDS = {35};
    private Location loc = null; // フラッグ座標
    private byte type = 0; // フラッグの種類

    // 元のブロックデータ
    // TODO: デフォルトブロックを可変にする とりあえず空気に変える
    private int blockID = 0;
    private byte blockData = 0;

    /*
     * コンストラクタ
     * 
     * @param plugin
     */
    public Flag(final Location loc, final byte type, final int blockID, final byte blockData) {

        // フラッグデータ登録
        this.loc = loc;
        this.type = type;

        this.blockID = blockID;
        this.blockData = blockData;
    }

    public Flag(final Location loc, final byte type) {
        this(loc, type, 0, (byte) 0);
    }

    /**
     * 今のブロックデータを返す
     * 
     * @return Block
     */
    public Block getNowBlock() {
        return loc.getBlock();
    }

    /**
     * ブロックを元のブロックにロールバックする
     * 
     * @return ロールバックが発生した場合にだけtrue
     */
    public boolean rollback() {
        Block block = loc.getBlock();
        // 既に同じブロックの場合は何もしない
        if (block.getTypeId() != blockID || block.getData() != blockData) {
            // ブロック変更
            block.setTypeIdAndData(blockID, blockData, false);
            return true;
        }
        return false;
    }

    /* フラッグ設定系 */
    /**
     * このフラッグの点数を返す
     * 
     * @return フラッグの点数
     */
    public byte getFlagPoint() {
        return type;
    }

    public String getTypeName() {
        return Byte.toString(type);
    }

    public Location getLocation() {
        return loc;
    }

    public int getOriginBlockID() {
        return blockID;
    }

    public byte getOriginBlockData() {
        return blockData;
    }
    
    public GameTeam getOwner() {
        Block b = this.loc.getBlock();
        if (!isFlag(b.getType())) {
            return null;
        }
        return GameTeam.getByColorData(b.getData());
    }
    
    public static boolean isFlag(Material material) {
        for (int id : FLAG_BLOCK_IDS) {
            if (material.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
