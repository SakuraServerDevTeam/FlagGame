/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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

import java.util.logging.Level;
import java.util.logging.Logger;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.reception.GameReception;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import syam.flaggame.FlagGame;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.enums.SignAction;
import syam.flaggame.enums.config.Configables;
import syam.flaggame.game.Flag;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.player.SetupSession;
import syam.flaggame.util.Actions;

public class FGPlayerListener implements Listener {

    public static final Logger log = FlagGame.logger;
    private static final String logPrefix = FlagGame.logPrefix;
    private static final String msgPrefix = FlagGame.msgPrefix;

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
                && player.getItemInHand().getTypeId() == plugin.getConfigs().getToolID()
                && Perms.SET.has(player)) {
            SetupSession sess = gPlayer.getSetupSession().get();
            Configables conf = sess.getSetting();
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
                    if (stage.isFlag(loc)) {
                        stage.removeFlag(loc);
                        Actions.message(player, "&aステージ'" + stage.getName() + "'のフラッグを削除しました！");
                        return;
                    }

                    // フラッグタイプを取得
                    Byte type = sess.getSelectedFlagType().get();
                    if (type == null) {
                        Actions.message(player, "&cフラッグの種類が指定されていません！");
                        return;
                    }

                    // 新規フラッグ登録
                    stage.addFlag(new Flag(loc, type));
                    Actions.message(player, "&aステージ'" + stage.getName() + "'の" + type + "ポイントフラッグを登録しました！");
                    break;

                // チェストモード
                case CHEST:
                    // チェスト、かまど、ディスペンサーのどれかでなければ返す
                    if (block.getType() != Material.CHEST && block.getType() != Material.FURNACE && block.getType() != Material.DISPENSER) {
                        Actions.message(player, "&cこのブロックはコンテナインターフェースを持っていません！");
                        return;
                    }
                    // 既にチェストブロックになっているか判定
                    if (stage.isChest(loc)) {
                        // 削除
                        stage.removeChest(loc);
                        Actions.message(player, "&aステージ'" + stage.getName() + "'のチェストを削除しました！");
                    } else {
                        // 選択
                        stage.setChest(loc);
                        Actions.message(player, "&aステージ'" + stage.getName() + "'のチェストを設定しました！");
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

        // 看板を右クリックした
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            // 1行目チェック
            if (sign.getLine(0).equals("§a[FlagGame]")) {
                clickFlagSign(player, block);
            }
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
                        Actions.message(player, msgPrefix + "&2フラッグゲーム'&6" + reception.getName() + "&2'の参加受付が行われています！");
                        Actions.message(player, msgPrefix + "&2 参加料:&6 " + entryFeeMsg + "&2   賞金:&6 " + awardMsg);
                        Actions.message(player, msgPrefix + "&2 '&6/flag join " + reception.getID() + "&2' コマンドで参加してください！");
                        Actions.message(player, "&b* ===================================");

                } // 開始中ゲーム
                else if (reception.getState().toGameState() == Game.State.STARTED) {
                    // 観戦アナウンス
                    Actions.message(player, "&b* ===================================");
                    Actions.message(player, msgPrefix + "&2フラッグゲーム'&6" + reception.getName() + "&2'が始まっています！");
                    Actions.message(player, msgPrefix + "&2 '&6/flag watch " + reception.getID() + "&2' コマンドで観戦することができます！");
                    Actions.message(player, "&b* ===================================");
                }
            }
        }, 20L);
    }

    private void clickFlagSign(Player player, Block block) {
        if (!(block.getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) block.getState();
        String line2 = sign.getLine(1); // 2行目
        String line3 = sign.getLine(2); // 3行目

        SignAction action;
        try {
            action = SignAction.valueOf(line2.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            Actions.message(player, "&cThis sign is broken! Please contact server staff!");
            return;
        }
        
        GamePlayer fgp = this.plugin.getPlayers().getPlayer(player);

        // 処理を分ける
        switch (action) {
            // 回復
            case HEAL:
                if (!"".equals(line3) && !line3.isEmpty()) {//特定チーム限定
                    TeamColor signTeam = null;
                    
                    try {
                        signTeam = TeamColor.valueOf(line3.trim().toUpperCase());
                    } catch (IllegalArgumentException ex) {
                        Actions.message(player, "&cThis sign is broken! Please contact server staff!");
                        return;
                    }
                    
                    if (!fgp.getTeam().isPresent()) {
                        Actions.message(player, "&cこの看板はフラッグゲーム中にのみ使うことができます");
                        return;
                    }

                    if (fgp.getTeam().get().getColor() != signTeam) {
                        Actions.message(player, "&cこれはあなたのチームの看板ではありません！");
                        return;
                    }
                }

                // 20以上にならないように体力とお腹ゲージを+2(ハート、おにく1つ分)回復させる
                double nowHP = player.getHealth();
                nowHP = nowHP + 2;
                if (nowHP > 20) {
                    nowHP = 20;
                }

                int nowFL = player.getFoodLevel();
                nowFL = nowFL + 2;
                if (nowFL > 20) {
                    nowFL = 20;
                }

                // プレイヤーにセット
                player.setHealth(nowHP);
                player.setFoodLevel(nowFL);
                player.setFireTicks(0); // 燃えてれば消してあげる

                Actions.message(player, msgPrefix + "&aHealed!");

                break;

            // 自殺
            case KILL:
                if (fgp.getTeam().isPresent()) {
                    Game game = fgp.getGame().get();
                    GamePlayer.sendMessage(game.getReception(), "&6["+game.getName()+"]&6 '"+fgp.getColoredName()+"&6'が自殺しました。");
                }
                player.setHealth(0);
                player.setFoodLevel(0);
                break;

            default:
                Actions.message(player, msgPrefix + "&cSorry I forgot this sign-action. Please contact server staff!");
                log.log(Level.WARNING, logPrefix + "{0}: Sorry I forgot this sign-action. Please contact server staff!", player.getName());
        }

    }
}
