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
package syam.flaggame.util;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import syam.flaggame.FlagGame;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import java.util.logging.Level;

/**
 * WorldEditの選択領域を取得するためのWorldEditハンドラ
 *
 * @author syam
 *
 */
public class WorldEditHandler {

    // Logger
    public static final Logger log = FlagGame.logger;
    private static final String logPrefix = FlagGame.logPrefix;
    private static final String msgPrefix = FlagGame.msgPrefix;

    /**
     * WorldEditプラグインインスタンスを返す
     *
     * @param bPlayer BukkitPlayer
     * @return WorldEditPlugin or null
     */
    private static WorldEditPlugin getWorldEdit(final Player bPlayer) {
        // WorldEditプラグイン取得
        Plugin plugin = FlagGame.getInstance().getServer().getPluginManager().getPlugin("WorldEdit");

        // プラグインが見つからない
        if (plugin == null) {
            if (bPlayer != null && bPlayer.isOnline()) {
                Actions.message(bPlayer, msgPrefix + "&cWorldEdit is not loaded!");
            }
            return null;
        }

        return (WorldEditPlugin) plugin;
    }

    /**
     * WorldEditがインストールされ使用可能かどうかチェックする
     *
     * @return 使用可能ならtrue, 違えばfalse
     */
    public static boolean isAvailable() {
        WorldEditPlugin we = getWorldEdit(null);
        if (we == null) {
            return false;
        } else {
            return we.isEnabled();
        }
    }

    /**
     * 指定したプレイヤーが選択中のWorldEdit領域を取得する
     *
     * @param bPlayer WorldEditで領域を指定しているプレイヤー
     * @return 選択された領域の両端のブロック配列[2] エラーならnull
     */
    @Deprecated
    public static Block[] getWorldEditRegion(final Player bPlayer) {
        WorldEditPlugin we = getWorldEdit(bPlayer);
        if (we == null) {
            return null;
        }

        BukkitPlayer player = new BukkitPlayer(we, we.getServerInterface(), bPlayer);
        LocalSession session = we.getWorldEdit().getSessionManager().get(player);

        // セレクタが立方体セレクタか判定
        if (!(session.getRegionSelector(session.getSelectionWorld()) instanceof CuboidRegionSelector)) {
            Actions.message(bPlayer, msgPrefix + "&cFlagGame supports only cuboid regions!");
            return null;
        }

        CuboidRegionSelector selector = (CuboidRegionSelector) session.getRegionSelector(session.getSelectionWorld());

        try {
            CuboidRegion region = selector.getRegion();

            // 選択範囲の端と端のブロックを格納する配列
            Block[] corners = new Block[2];

            Vector v1 = region.getPos1();
            Vector v2 = region.getPos2();

            corners[0] = bPlayer.getWorld().getBlockAt(v1.getBlockX(), v1.getBlockY(), v1.getBlockZ());
            corners[1] = bPlayer.getWorld().getBlockAt(v2.getBlockX(), v2.getBlockY(), v2.getBlockZ());

            // 角のブロック配列[2]を返す
            return corners;
        } catch (IncompleteRegionException ex) {
            // 正しく領域が選択されていない例外
            Actions.message(bPlayer, msgPrefix + "&cWorldEdit region is not fully selected!");
        } catch (Exception ex) {
            // その他一般例外
            log.log(Level.WARNING, logPrefix + "Error while retreiving WorldEdit region: {0}", ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public static Cuboid getSelectedArea(Player player) throws IllegalStateException {
        WorldEditPlugin we = getWorldEdit(player);
        if (we == null) {
            return null;
        }
        LocalSession session = we.getWorldEdit().getSessionManager().get(
                new BukkitPlayer(we, we.getServerInterface(), player)
        );
        if (!(session.getRegionSelector(session.getSelectionWorld()) instanceof CuboidRegionSelector)) {
            throw new IllegalStateException("Cuboid regions are not supported");
        }
        CuboidRegionSelector selector = (CuboidRegionSelector) session.getRegionSelector(session.getSelectionWorld());
        try {
            Vector v1 = selector.getRegion().getPos1();
            Vector v2 = selector.getRegion().getPos2();
            return new Cuboid(
                    new Location(player.getWorld(), v1.getBlockX(), v1.getBlockY(), v1.getBlockZ()),
                    new Location(player.getWorld(), v2.getBlockX(), v2.getBlockY(), v2.getBlockZ())
            );
        } catch (IncompleteRegionException ex) {
            throw new IllegalStateException("WorldEdit region is not fully selected");
        }
    }

    /**
     * 指定したプレイヤーでWorldEditの領域を設定する
     *
     * @param bPlayer BukkitPlayer
     * @param pos1 Location Pos1
     * @param pos2 Location Pos2
     * @return 成功すればtrue, 失敗すればfalse
     */
    public static boolean selectWorldEditRegion(final Player bPlayer, final Location pos1, final Location pos2) {
        // 不正な引数
        if (bPlayer == null || pos1 == null || pos2 == null) {
            return false;
        }
        if (!pos1.getWorld().equals(pos2.getWorld()) || !pos1.getWorld().equals(bPlayer.getWorld())) {
            return false;
        }

        WorldEditPlugin we = getWorldEdit(bPlayer);
        if (we == null) {
            return false;
        }

        BukkitPlayer player = new BukkitPlayer(we, we.getServerInterface(), bPlayer);
        com.sk89q.worldedit.world.World world = player.getWorld();
        LocalSession session = we.getWorldEdit().getSessionManager().get(player);

        try {
            CuboidRegionSelector selector = new CuboidRegionSelector(world);

            selector.selectPrimary(new Vector(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ()), null);
            selector.selectSecondary(new Vector(pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ()), null);

            session.setRegionSelector(world, selector);
            session.dispatchCUISelection(player);
        } catch (Exception ex) {
            // 一般例外
            log.log(Level.WARNING, logPrefix + "Error while selecting WorldEdit region: {0}", ex.getMessage());
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 指定したプレイヤーでWorldEditの領域を設定する
     *
     * @param bPlayer BukkitPlayer
     * @param pos1 Block pos1
     * @param pos2 Block Pos2
     * @return 成功すればtrue, 失敗すればfalse
     */
    public static boolean selectWorldEditRegion(final Player bPlayer, final Block pos1, final Block pos2) {
        if (pos1 == null || pos2 == null) {
            return false;
        }
        return selectWorldEditRegion(bPlayer, pos1.getLocation(), pos2.getLocation());
    }
    
    public static boolean setSelectedArea(Player player, Cuboid area) {
        return selectWorldEditRegion(player, area.getPos1(), area.getPos2());
    }
    
}
