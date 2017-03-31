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
import jp.llv.flaggame.game.basic.objective.BannerSlot;
import jp.llv.flaggame.game.basic.objective.BannerSpawner;
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
import jp.llv.flaggame.game.basic.objective.Flag;
import jp.llv.flaggame.game.basic.objective.Nexus;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Banner;
import syam.flaggame.command.objective.ObjectiveType;
import syam.flaggame.exception.StageReservedException;
import syam.flaggame.game.Stage;
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
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
            && gPlayer.getSetupSession().isPresent()
            && event.getHand() == EquipmentSlot.HAND
            && player.getInventory().getItemInMainHand().getTypeId() == plugin.getConfigs().getToolID()
            && Perms.SET.has(player)) {
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

            switch (conf) {
                // フラッグモード
                case FLAG:
                    // 既にフラッグブロックなら解除する
                    if (stage.getFlag(loc).isPresent()) {
                        try {
                            stage.removeFlag(loc);
                        } catch (StageReservedException ex) {
                            Actions.message(player, "&cステージ" + stage.getName() + "は現在編集不可です!");
                            return;
                        }
                        Actions.message(player, "&aステージ'" + stage.getName() + "'のフラッグを削除しました！");
                        return;
                    }

                    // フラッグタイプを取得
                    Byte type = sess.getSelectedPoint().byteValue();

                    // 新規フラッグ登録
                    try {
                        stage.addFlag(new Flag(loc, type, false));
                    } catch (StageReservedException ex) {
                        Actions.message(player, "&cステージ" + stage.getName() + "は現在編集不可です!");
                        return;
                    }
                    Actions.message(player, "&aステージ'" + stage.getName() + "'の" + type + "ポイントフラッグを登録しました！");
                    break;

                case NEXUS:
                    if (stage.getNexus(loc).isPresent()) {
                        try {
                            stage.removeNexus(loc);
                        } catch (StageReservedException ex) {
                            Actions.message(player, "&cステージ" + stage.getName() + "は現在編集不可です!");
                            return;
                        }
                        Actions.message(player, "&aステージ'" + stage.getName() + "'の目標を削除しました！");
                        return;
                    }

                    try {
                        stage.addNexus(new Nexus(loc, sess.getSelectedColor(), sess.getSelectedPoint()));
                    } catch (StageReservedException ex) {
                        Actions.message(player, "&cステージ" + stage.getName() + "は現在編集不可です!");
                        return;
                    }
                    Actions.message(player, "&aステージ'" + stage.getName() + "'の"
                                            + (sess.getSelectedColor() != null ? sess.getSelectedColor().getRichName() : "全チーム") + "&aの"
                                            + sess.getSelectedPoint() + "ポイント目標を登録しました！");
                    break;
                case BANNER_SLOT:
                    if (stage.getBannerSlot(loc).isPresent()) {
                        try {
                            stage.removeBannerSlot(loc);
                        } catch (StageReservedException ex) {
                            Actions.message(player, "&cステージ" + stage.getName() + "は現在編集不可です!");
                            return;
                        }
                        Actions.message(player, "&aステージ'" + stage.getName() + "'のスロットを削除しました！");
                        return;
                    }

                    try {
                        stage.addBannerSlot(new BannerSlot(loc, sess.getSelectedColor()));
                    } catch (StageReservedException ex) {
                        Actions.message(player, "&cステージ" + stage.getName() + "は現在編集不可です!");
                        return;
                    }
                    Actions.message(player, "&aステージ'" + stage.getName() + "'の"
                                            + (sess.getSelectedColor() != null ? sess.getSelectedColor().getRichName() : "全チーム") + "&aの"
                                            + "スロットを登録しました！");
                    break;
                case BANNER_SPAWNER:
                    if (stage.getBannerSpawner(loc).isPresent()) {
                        try {
                            stage.removeBannerSpawner(loc);
                        } catch (StageReservedException ex) {
                            Actions.message(player, "&cステージ" + stage.getName() + "は現在編集不可です!");
                            return;
                        }
                        Actions.message(player, "&aステージ'" + stage.getName() + "'のバナースポナーを削除しました！");
                        return;
                    }

                    Block b = loc.getBlock();
                    if (b.getType() != Material.BANNER && b.getType() != Material.WALL_BANNER) {
                        Actions.message(player, "&cバナーのスポーン位置にバナーを予め設置してください!");
                        return;
                    }
                    boolean wall = b.getType() == Material.WALL_BANNER;
                    BlockFace face = ((Banner) b.getState().getData()).getAttachedFace();

                    try {
                        stage.addBannerSpawner(new BannerSpawner(loc, sess.getSelectedPoint().byteValue(), sess.getSelectedHp(), false, wall, face));
                    } catch (StageReservedException ex) {
                        Actions.message(player, "&cステージ" + stage.getName() + "は現在編集不可です!");
                        return;
                    }
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
                    // 既にチェストブロックになっているか判定
                    try {
                        if (stage.isChest(loc)) {
                            // 削除
                            stage.removeChest(loc);
                            Actions.message(player, "&aステージ'" + stage.getName() + "'のチェストを削除しました！");
                        } else {
                            // 選択
                            stage.setChest(loc);
                            Actions.message(player, "&aステージ'" + stage.getName() + "'のチェストを設定しました！");
                        }
                    } catch (StageReservedException ex) {
                        Actions.message(player, "&cステージ" + stage.getName() + "は現在編集不可です!");
                        return;
                    }
                    break;

                // 他は何もしない
                default:
                    break;
            }

            event.setCancelled(true);
            event.setUseInteractedBlock(Result.DENY);
            event.setUseItemInHand(Result.DENY);
        }
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
                    // 賞金系メッセージ
                    String entryFeeMsg = reception.getEntryFee() > 0 ? reception.getEntryFee() + "Coin" : "Free";
                    String awardMsg = reception.getMaxAward() > 0 ? reception.getMaxAward() + "Coin" : "None";

                    Actions.message(player, "&b* ===================================");
                    Actions.sendPrefixedMessage(player, "&2フラッグゲーム'&6" + reception.getName() + "&2'の参加受付が行われています！");
                    Actions.sendPrefixedMessage(player, "&2 参加料:&6 " + entryFeeMsg + "&2   賞金:&6 " + awardMsg);
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
