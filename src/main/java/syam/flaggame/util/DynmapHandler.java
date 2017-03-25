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
package syam.flaggame.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import syam.flaggame.FlagGame;
import syam.flaggame.game.Stage;

public class DynmapHandler {
    // Logger
    public static final Logger log = FlagGame.logger;
    private static final String logPrefix = FlagGame.logPrefix;
    private static final String msgPrefix = FlagGame.msgPrefix;

    private final FlagGame plugin;

    // Dynmap
    private Plugin dynmap;
    private DynmapAPI api;
    private MarkerAPI markerapi;

    // Markers
    private MarkerSet set;
    private Map<String, AreaMarker> markers = new HashMap<>();
    private String infowindow;
    private boolean use3D;

    // flags
    private boolean activated = false;

    // final
    private final static String dynmapID = "flaggame.markers";
    private final static String defaultInfowindow = "<div class=\"infowindow\">" + "<u>フラッグゲームステージ情報</u><br />" + "<span style=\"font-size:120%;font-weight:bold;\">%gamename%</span><br />" + "<span style=\"font-weight:bold;\">フラッグ数:</span> %flagcount%<br />" + "<span style=\"font-weight:bold;\">チェスト数:</span> %chestcount%<br />" + "<span style=\"font-weight:bold;\">ゲーム時間:</span> %gametime%<br />"
            + "<span style=\"font-weight:bold;\">チーム毎人数制限:</span> %teamlimit%<br />" + "<span style=\"font-weight:bold;\">参加料:</span> %entryfee%<br />" + "<span style=\"font-weight:bold;\">賞金:</span> %award%<br />" + "<span style=\"font-weight:bold;\">観戦席:</span> %specspawn%<br />" + "</div>";

    public DynmapHandler(final FlagGame plugin) {
        this.plugin = plugin;
    }

    /**
     * 初期化
     */
    public void init() {
        PluginManager pm = plugin.getServer().getPluginManager();

        // Get dynmap
        dynmap = pm.getPlugin("dynmap");
        if (dynmap == null) {
            log.severe(logPrefix + "Cannot find dynmap!");
            return;
        }

        // Get dynmap API
        api = (DynmapAPI) dynmap;

        // Regist Listener
        pm.registerEvents(new OurServerListener(), plugin);

        // dynmapが有効なら有効化
        if (dynmap.isEnabled()) activate();
    }

    /**
     * 有効化
     */
    private void activate() {
        // 既に有効化済みなら何もしない
        if (activated) return;

        // Get markers API
        markerapi = api.getMarkerAPI();
        if (markerapi == null) {
            log.severe(logPrefix + "Cannot loading Dynmap marker API!");
            return;
        }

        // TODO: Load config.yml
        infowindow = defaultInfowindow;
        use3D = true;

        // Set markers
        set = markerapi.getMarkerSet(dynmapID);
        if (set == null) {
            set = markerapi.createMarkerSet(dynmapID, "フラッグゲーム", null, false);
        } else {
            set.setMarkerSetLabel("フラッグゲーム");
        }

        if (set == null) {
            log.severe(logPrefix + "Cannot creating dynmap marker set!");
            return;
        }
        // set.setMinZoom(0);
        set.setLayerPriority(10);
        set.setHideByDefault(false);

        log.info(logPrefix + "Hooked to dynmap!");
        activated = true;

        updateRegions();
    }

    /**
     * ステージ領域をアップデートする
     */
    public void updateRegions() {
        if (!activated) return;

        Map<String, AreaMarker> newmap = new HashMap<>();

        plugin.getStages().getStages().values().stream().forEach(stage -> handleStage(stage, newmap));

        // 古いマーカーを削除
        markers.values().stream().forEach(oldm -> oldm.deleteMarker());

        // 新マーカーセット
        markers.clear();
        markers = newmap;
    }

    public void updateRegion(Stage stage) {
        if (!activated) return;

        Map<String, AreaMarker> newmap = new HashMap<>();
        handleStage(stage, newmap);

        // 更新するゲームの古いマーカーを削除
        for (String updateName : newmap.keySet()) {
            if (markers.containsKey(updateName)) {
                markers.get(updateName).deleteMarker();
                markers.remove(updateName);
            }
        }

        // 更新マーカーを追加
        markers.putAll(newmap);
    }

    private void handleStage(Stage stage, Map<String, AreaMarker> newmap) {
        String gameName = stage.getName();
        double[] x;
        double[] z;

        // ステージエリア未設定ならスキップ
        Cuboid region = stage.getAreas().getStageArea();
        if (region == null) return;

        // ステージエリアの頂点を取得
        Location l0 = region.getPos1();
        Location l1 = region.getPos2();
        World world = l0.getWorld();

        x = new double[4];
        z = new double[4];

        x[0] = l0.getX();
        z[0] = l0.getZ();
        x[1] = l0.getX();
        z[1] = l1.getZ() + 1.0;
        x[2] = l1.getX() + 1.0;
        z[2] = l1.getZ() + 1.0;
        x[3] = l1.getX() + 1.0;
        z[3] = l0.getZ();

        // 既にマーカーが存在すれば更新、なければ新規追加
        String markerid = world.getName() + "_" + gameName;
        AreaMarker m = markers.remove(markerid);
        if (m == null) {
            // 新規マーカー登録
            m = set.createAreaMarker(markerid, gameName, false, world.getName(), x, z, false);

            if (m == null) return;
        }
        // マーカーデータ更新
        else {
            m.setCornerLocations(x, z);
            m.setLabel(gameName);
        }

        // 3D表示設定
        if (use3D) {
            m.setRangeY(l1.getY() + 1.0D, l0.getY());
        }

        // ポップアップするバルーンに詳細情報を設定する
        m.setDescription(formatInfoWindow(stage, m));

        // 新マーカーマップに追加
        newmap.put(markerid, m);
    }

    /**
     * dynmapのエリアマーカー用の表示内容フォーマッティングする
     * 
     * @param project
     * @param m
     * @return
     */
    private String formatInfoWindow(Stage stage, AreaMarker m) {
        String s = "<div class=\"regioninfo\">" + infowindow + "</div>";
        // Build game name
        s = s.replaceAll("%gamename%", stage.getName());

        // Build flag/chest count
        s = s.replaceAll("%flagcount%", stage.getFlags().size() + "個");
        s = s.replaceAll("%chestcount%", stage.getChests().size() + "個");

        // Build options
        s = s.replaceAll("%gametime%", Actions.getTimeString(stage.getGameTimeInSec()));
        s = s.replaceAll("%teamlimit%", stage.getTeamLimit() + "人");

        // Build Award/EntryFee

        // Build specspawn
        if (stage.getSpecSpawn() == null) {
            s = s.replaceAll("%specspawn%", "なし");
        } else {
            s = s.replaceAll("%specspawn%", "あり");
        }

        // Build players - included by own ProjectManager plugin
        /*
         * String managers = "(none)"; String members = "(none)"; if
         * (project.getPlayersByType(MemberType.MANAGER).size() >= 1){ managers
         * = Util.join(project.getPlayersByType(MemberType.MANAGER), ", "); } if
         * (project.getPlayersByType(MemberType.MEMBER).size() >= 1){ members =
         * Util.join(project.getPlayersByType(MemberType.MEMBER), ", "); } s =
         * s.replaceAll("%managers%", managers); s = s.replaceAll("%members%",
         * members);
         */

        return s;
    }

    /**
     * dynmap連携を無効にする
     */
    public void disableDynmap() {
        if (markerapi != null) {
            set = markerapi.getMarkerSet(dynmapID);

            if (set != null) {
                set.deleteMarkerSet();
                set = null;
            }
            markers.clear();
        }
        activated = false;
    }

    /**
     * dynmap有効時にイベントを取る
     * 
     * @author syam
     */
    private class OurServerListener implements Listener {
        @SuppressWarnings("unused")
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPluginEnable(final PluginEnableEvent event) {
            Plugin plugin = event.getPlugin();
            String name = plugin.getDescription().getName();
            if (name.equals("dynmap")) {
                if (dynmap.isEnabled()) activate();
            }
        }
    }

    /* getter / setter */
    public Server getServer() {
        return plugin.getServer();
    }

    public boolean isActivated() {
        return activated;
    }

    public MarkerAPI getMarkerAPI() {
        return markerapi;
    }
}
