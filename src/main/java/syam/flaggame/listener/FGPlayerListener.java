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
package syam.flaggame.listener;

import jp.llv.flaggame.game.Game;
import syam.flaggame.game.objective.BannerSlot;
import syam.flaggame.game.objective.BannerSpawner;
import jp.llv.flaggame.reception.GameReception;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import syam.flaggame.FlagGame;
import syam.flaggame.game.objective.Flag;
import syam.flaggame.game.objective.Nexus;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Banner;
import jp.llv.flaggame.util.OnelineBuilder;
import syam.flaggame.exception.ObjectiveCollisionException;
import syam.flaggame.game.objective.ObjectiveType;
import syam.flaggame.game.Stage;
import syam.flaggame.game.objective.GameChest;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.player.SetupSession;
import syam.flaggame.util.Actions;

public class FGPlayerListener implements Listener {

    private final FlagGame plugin;

    public FGPlayerListener(final FlagGame plugin) {
        this.plugin = plugin;
    }

    /* 登録するイベントはここから下に */
    // プレイヤーがブロックをクリックした
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);

        // 管理モードで権限を持ち、かつ設定したツールでブロックを右クリックした
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK
              && gPlayer.getSetupSession().isPresent()
              && event.getHand() == EquipmentSlot.HAND
              && player.getInventory().getItemInMainHand().getTypeId() == plugin.getConfigs().getToolID()
              && Perms.SET.has(player))) {
            return;
        }
        SetupSession sess = gPlayer.getSetupSession().get();
        ObjectiveType conf = sess.getSetting();
        Stage stage = sess.getSelectedStage();
        if (stage == null) {
            Actions.message(player, "&c先に編集するゲームを選択してください！");
            return;
        }

        Location loc = block.getLocation();

        // ゲーム用ワールドでなければ返す
        if (loc.getWorld() != Bukkit.getWorld(plugin.getConfigs().getGameWorld())) {
            Actions.message(player, "&cここはゲーム用ワールドではありません！");
            return;
        }

        try {
            if (stage.isObjective(conf.getType(), loc)) { //既にオブジェクティブ
                stage.removeObjective(loc);
                OnelineBuilder.newBuilder()
                        .info("ステージ").value(stage.getName()).info("の")
                        .value(conf.getName()).info("を削除しました！")
                        .sendTo(player);
                return;
            }

            switch (conf) {
                // フラッグモード
                case FLAG:
                    // フラッグタイプを取得
                    Byte type = sess.getSelectedPoint().byteValue();

                    // 新規フラッグ登録
                    stage.addObjective(new Flag(loc, type, false));
                    Actions.message(player, "&aステージ'" + stage.getName() + "'の" + type + "ポイントフラッグを登録しました！");
                    break;

                case NEXUS:
                    stage.addObjective(new Nexus(loc, sess.getSelectedColor(), sess.getSelectedPoint()));
                    Actions.message(player, "&aステージ'" + stage.getName() + "'の"
                                            + (sess.getSelectedColor() != null ? sess.getSelectedColor().getRichName() : "全チーム") + "&aの"
                                            + sess.getSelectedPoint() + "ポイント目標を登録しました！");
                    break;
                case BANNER_SLOT:
                    stage.addObjective(new BannerSlot(loc, sess.getSelectedColor()));
                    Actions.message(player, "&aステージ'" + stage.getName() + "'の"
                                            + (sess.getSelectedColor() != null ? sess.getSelectedColor().getRichName() : "全チーム") + "&aの"
                                            + "スロットを登録しました！");
                    break;
                case BANNER_SPAWNER:
                    Block b = loc.getBlock();
                    if (b.getType() != Material.BANNER && b.getType() != Material.WALL_BANNER) {
                        Actions.message(player, "&cバナーのスポーン位置にバナーを予め設置してください!");
                        return;
                    }
                    boolean wall = b.getType() == Material.WALL_BANNER;
                    BlockFace face = ((Banner) b.getState().getData()).getAttachedFace();
                    stage.addObjective(new BannerSpawner(loc, sess.getSelectedPoint().byteValue(), sess.getSelectedHp(), false, wall, face));
                    Actions.message(player, "&aステージ'" + stage.getName() + "'の"
                                            + sess.getSelectedPoint() + "ポイントバナースポナーを登録しました！");
                    break;
                // チェストモード
                case CHEST:
                    // チェスト、かまど、ディスペンサーのどれかでなければ返す
                    if (block.getType() != Material.CHEST && block.getType() != Material.FURNACE && block.getType() != Material.DISPENSER) {
                        Actions.message(player, "&cこのブロックはコンテナインターフェースを持っていません！");
                        return;
                    }
                    // 選択
                    stage.addObjective(new GameChest(loc));
                    Actions.message(player, "&aステージ'" + stage.getName() + "'のチェストを設定しました！");
                    break;

                // 他は何もしない
                default:
                    break;
            }
        } catch (ObjectiveCollisionException ex) {
            Actions.message(player, "&cそこには既に別のオブジェクティブが存在します！");
        }

        event.setCancelled(true);
        event.setUseInteractedBlock(Result.DENY);
        event.setUseItemInHand(Result.DENY);
    }

    // プレイヤーがログインした
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        // ログイン時のMOTDなどの最後に表示別スレッドで実行する
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            for (GameReception reception : this.plugin.getReceptions()) {
                // 待機中ゲーム
                if (reception.getState().toGameState() == Game.State.PREPARATION) {
                    Actions.message(player, "&b* ===================================");
                    Actions.sendPrefixedMessage(player, "&2フラッグゲーム'&6" + reception.getName() + "&2'の参加受付が行われています！");
                    Actions.sendPrefixedMessage(player, "&2 '&6/flag join " + reception.getID() + "&2' コマンドで参加してください！");
                    Actions.message(player, "&b* ===================================");

                } // 開始中ゲーム
                else if (reception.getState().toGameState() == Game.State.STARTED) {
                    // 観戦アナウンス
                    Actions.message(player, "&b* ===================================");
                    Actions.sendPrefixedMessage(player, "&2フラッグゲーム'&6" + reception.getName() + "&2'が始まっています！");
                    Actions.sendPrefixedMessage(player, "&2 '&6/flag watch " + reception.getID() + "&2' コマンドで観戦することができます！");
                    Actions.message(player, "&b* ===================================");
                }
            }
        }, 20L);
    }

}
