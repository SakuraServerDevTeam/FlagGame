/* 
 * Copyright (c) 2015 SakuraServerDev All rights reserved.
 */
package syam.flaggame.game;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import syam.flaggame.FlagGame;
import syam.flaggame.enums.*;
import syam.flaggame.event.GameFinishedEvent;
import syam.flaggame.event.GameStartEvent;
import syam.flaggame.exception.GameStateException;
import syam.flaggame.manager.GameManager;
import syam.flaggame.player.FGPlayer;
import syam.flaggame.player.PlayerManager;
import syam.flaggame.player.PlayerProfile;
import syam.flaggame.util.Actions;

/**
 * Game (Game.java)
 *
 * @author syam(syamn)
 */
public class Game {

    public enum State {

        PREPARATION, ENTRY, STARTED, FINISHED,;
    }

    // Logger
    private static final String msgPrefix = FlagGame.msgPrefix;

    // プラグインインスタンス
    private final FlagGame plugin;

    private String gameID; // 一意なゲームID ログ用
    private Stage stage;
    private boolean random = false;

    private int remainSec; // 1ゲームの制限時間
    private int timerThreadID = -1; // タイマータスクのID
    private int starttimerInSec = 10;
    private int starttimerThreadID = -1;
    private State state = State.PREPARATION;

    // 参加プレイヤー
    private Map<GameTeam, Set<FGPlayer>> playersMap = new EnumMap<>(GameTeam.class);
    private Set<FGPlayer> redPlayers = Collections.newSetFromMap(new ConcurrentHashMap<FGPlayer, Boolean>());
    private Set<FGPlayer> bluePlayers = Collections.newSetFromMap(new ConcurrentHashMap<FGPlayer, Boolean>());

    // Tabリスト表示名変更
    private Map<FGPlayer, String> tabListMap = new ConcurrentHashMap<>();

    // Kill/Death記録
    private Map<GameTeam, Integer> teamKilledCount = new EnumMap<>(GameTeam.class);

    /*
     * コンストラクタ
     *
     * @param plugin
     * @param stage
     */
    public Game(final FlagGame plugin, final Stage stage, final boolean random) {
        this.plugin = plugin;
        this.stage = stage;
        this.random = random;

        // 例外チェック
        if (!stage.isAvailable()) {
            throw new GameStateException("This stage is not available!");
        }
        if (GameManager.getGames().containsKey(stage)) {
            this.plugin.getLogger().log(Level.SEVERE, "Stage {0} is duplicate!", stage.getName());
            return;
        }

        this.remainSec = stage.getGameTime();

        // ゲームマネージャにゲーム登録
        GameManager.addGame(stage.getName(), this);
    }

    /*
     * このゲームを開始待機中にする
     */
    public void ready(CommandSender sender) {
        if (this.state != State.PREPARATION) {
            throw new GameStateException("This game is already using!");
        }

        // 一度プレイヤーリスト初期化
        redPlayers.clear();
        bluePlayers.clear();
        // 再マッピング
        mappingPlayersList();

        // ステージエリアチェック
        if (stage.getStage() == null) {
            throw new GameStateException("Stage area is not defined properly!");
        }

        // スポーン地点チェック
        if (stage.getSpawns().size() != 2) {
            throw new GameStateException("Team spawn area is not defined properly!");
        }

        // 保護チェック
        if (!stage.isStageProtected()) {
            stage.setStageProtected(true);
        }

        // 待機
        this.state = State.ENTRY;
        stage.setUsing(true);
        stage.setGame(this);

        // 賞金系メッセージ
        String entryFeeMsg = String.valueOf(stage.getEntryFee()) + "Coin";
        String awardMsg = String.valueOf(stage.getAward()) + "Coin";
        if (stage.getEntryFee() <= 0) {
            entryFeeMsg = "&7FREE!";
        }
        if (stage.getAward() <= 0) {
            awardMsg = "&7なし";
        }

        // アナウンス
        if (!random) {
            Actions.broadcastMessage(msgPrefix + "&2フラッグゲーム'&6" + stage.getName() + "&2'の参加受付が開始されました！");
            Actions.broadcastMessage(msgPrefix + "&2 参加料:&6 " + entryFeeMsg + "&2   賞金:&6 " + awardMsg);
            Actions.broadcastMessage(msgPrefix + "&2 '&6/flag join " + stage.getName() + "&2' コマンドで参加してください！");
        } else {
            Actions.broadcastMessage(msgPrefix + "&2フラッグゲーム'&6ランダムステージ&2'の参加受付が開始されました！");
            Actions.broadcastMessage(msgPrefix + "&2 参加料:&6 " + entryFeeMsg + "&2   賞金:&6 " + awardMsg);
            Actions.broadcastMessage(msgPrefix + "&2 '&6/flag join random&2' コマンドで参加してください！");

            GameManager.setRandomGame(this);
        }

        // ロギング
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd-HHmmss");
        this.gameID = stage.getName() + "_" + sdf.format(new Date());

        log("========================================");
        log("Sender " + sender.getName() + " Ready to Game");
        log("Stage: " + stage.getName() + " (" + stage.getFileName() + ")");
        log("TeamPlayerLimit: " + stage.getTeamLimit() + " GameTime: " + stage.getGameTime() + " sec");
        log("Award: " + stage.getAward() + " EntryFee:" + stage.getEntryFee());
        log("========================================");
    }

    /*
     * ゲームを開始する
     */
    public void start(CommandSender sender) {
        // Call event
        GameStartEvent startEvent = new GameStartEvent(this.stage, this.random, sender,
                Collections.unmodifiableSet(redPlayers), Collections.unmodifiableSet(bluePlayers));
        plugin.getServer().getPluginManager().callEvent(startEvent);
        if (startEvent.isCancelled()) {
            return;
        }

        if (this.state != State.ENTRY) {
            Actions.message(sender, "&cこのゲームは開始待機中ではありません");
            return;
        }

        if (random) {
            GameManager.setRandomGame(null);
        }

        // チームの人数チェック
        /*
         * if (redPlayers.size() != bluePlayers.size()){ Actions.message(sender,
         * null, "&c各チームのプレイヤー数が同じになるまでお待ちください"); return; }
         */
        if (redPlayers.size() <= 0 || bluePlayers.size() <= 0) {
            Actions.message(sender, "&cプレイヤーが参加していないチームがあります");
            return;
        }

        // ステージエリアチェック
        if (stage.getStage() == null) {
            Actions.message(sender, "&cステージエリアが正しく設定されていません");
            return;
        }

        // スポーン地点の再チェック
        if (stage.getSpawns().size() != 2) {
            Actions.message(sender, "&cチームスポーン地点が正しく設定されていません");
            return;
        }

        // フラッグブロックとチェストをロールバックする
        stage.rollbackFlags();
        stage.rollbackChests(sender);

        // 参加プレイヤーをスポーン地点に移動させる
        tpSpawnLocation();

        // チャンクロード
        // getSpawnLocation(GameTeam.RED).getChunk().load();
        // getSpawnLocation(GameTeam.BLUE).getChunk().load();
        // 開始
        timer(); // タイマースタート
        state = State.STARTED;

        // プロファイル更新
        stage.getProfile().updateLastPlayedStage();
        stage.getProfile().addPlayed();

        // アナウンス
        if (!random) {
            Actions.broadcastMessage(msgPrefix + "&2フラッグゲーム'&6" + stage.getName() + "&2'が始まりました！");
        } else {
            Actions.broadcastMessage(msgPrefix + "&2フラッグゲーム'&6ランダムステージ&2'が始まりました！");
            Actions.broadcastMessage(msgPrefix + "&2ステージ'&6" + stage.getName() + "&2'が選択されました！");
        }
        Actions.broadcastMessage(msgPrefix + "&f &a制限時間: &f" + Actions.getTimeString(stage.getGameTime()) + "&f | &b青チーム: &f" + bluePlayers.size() + "&b人&f | &c赤チーム: &f" + redPlayers.size() + "&c人");
        if (stage.getSpecSpawn() != null) {
            Actions.broadcastMessage(msgPrefix + "&2 '&6/flag watch " + stage.getName() + "&2' コマンドで観戦することができます！");
        }

        tabListMap.clear();

        // 試合に参加する全プレイヤーを回す
        for (Map.Entry<GameTeam, Set<FGPlayer>> entry : playersMap.entrySet()) {
            GameTeam team = entry.getKey();
            for (FGPlayer fgplayer : entry.getValue()) {
                Player player = fgplayer.getPlayer();
                // オフラインプレイヤーをスキップ
                if (!player.isOnline()) {
                    continue;
                }

                // ゲームモード強制変更
                player.setGameMode(GameMode.SURVIVAL);

                // アイテムクリア
                player.getInventory().clear();
                // 頭だけ羊毛に変える
                player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short) 0, team.getBlockData()));
                // player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);

                // 回復させる
                player.setHealth(20);
                player.setFoodLevel(20);

                // 効果のあるポーションをすべて消す
                if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                    player.removePotionEffect(PotionEffectType.JUMP);
                }
                if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                }
                if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                    player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
                if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                    player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                }
                if (player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                }
                if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                    player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                }

                // 参加カウント追加
                PlayerManager.getProfile(player).updateLastJoinedGame();
                PlayerManager.getProfile(player).addPlayed();

                // 参加ゲーム更新
                PlayerManager.getPlayer(player).setPlayingGame(this);

                // メッセージ通知
                Actions.message(player, msgPrefix + "&a *** " + team.getColor() + "あなたは " + team.getTeamName() + "チーム です！ &a***");

                // Tabリスト名変更
                tabListMap.put(fgplayer, player.getPlayerListName());
                String tabname = ("§" + team.getColor().charAt(1) + player.getName());
                if (tabname.length() > 16) {
                    tabname = tabname.substring(0, 12) + ChatColor.WHITE + "..";
                }
                player.setPlayerListName(tabname);
            }
        }
        String red = redPlayers.stream().map(FGPlayer::getName).collect(Collectors.joining(", ")),
                blue = bluePlayers.stream().map(FGPlayer::getName).collect(Collectors.joining(", "));

        log("========================================");
        log("Sender " + sender.getName() + " Start Game");
        log("RedTeam(" + redPlayers.size() + "): " + red);
        log("BlueTeam(" + bluePlayers.size() + "): " + blue);
        log("========================================");
    }

    /**
     * タイマー終了によって呼ばれるゲーム終了処理
     */
    public void finish() {
        // ポイントチェック
        int redP = 0, blueP = 0, noneP = 0;
        String redS = "", blueS = "", noneS = "";
        Map<GameTeam, Map<Byte, Integer>> pointsMap = stage.checkFlag();
        // 赤チームチェック
        if (pointsMap.containsKey(GameTeam.RED)) {
            Map<Byte, Integer> points = pointsMap.get(GameTeam.RED);
            for (Map.Entry<Byte, Integer> entry : points.entrySet()) {
                Byte ft = entry.getKey();
                // 総得点に加算
                redP = redP + (ft * entry.getValue());
                // 文章組み立て
                redS = redS + ft + "ポイントフラッグ: &f" + entry.getValue() + " | ";
            }
            redS = redS.substring(0, redS.length() - 3);
        }
        // 青チームチェック
        if (pointsMap.containsKey(GameTeam.BLUE)) {
            Map<Byte, Integer> points = pointsMap.get(GameTeam.BLUE);
            for (Map.Entry<Byte, Integer> entry : points.entrySet()) {
                Byte ft = entry.getKey();
                // 総得点に加算
                blueP = blueP + (ft * entry.getValue());
                // 文章組み立て
                blueS = blueS + ft + "ポイントフラッグ: &f" + entry.getValue() + " | ";
            }
            blueS = blueS.substring(0, blueS.length() - 3);
        }
        // NONEチームチェック
        if (pointsMap.containsKey(null)) {
            Map<Byte, Integer> points = pointsMap.get(null);
            for (Map.Entry<Byte, Integer> entry : points.entrySet()) {
                Byte ft = entry.getKey();
                // 総得点に加算
                noneP = noneP + (ft * entry.getValue());
                // 文章組み立て
                noneS = noneS + ft + "ポイントフラッグ: &f" + entry.getValue() + " | ";
            }
            noneS = noneS.substring(0, noneS.length() - 3);
        }

        // 勝敗判定
        GameTeam winTeam = null;
        if (redP > blueP) {
            winTeam = GameTeam.RED;
        } else if (blueP > redP) {
            winTeam = GameTeam.BLUE;
        }

        // 引き分けはKill数比較
        if (winTeam == null) {
            if (getKillCount(GameTeam.RED) > getKillCount(GameTeam.BLUE)) {
                winTeam = GameTeam.RED;
            } else if (getKillCount(GameTeam.BLUE) > getKillCount(GameTeam.RED)) {
                winTeam = GameTeam.BLUE;
            }
        }

        // アナウンス
        Actions.broadcastMessage(msgPrefix + "&2フラッグゲーム'&6" + stage.getName() + "&2'が終わりました！");
        if (!"".equals(redS)) {
            Actions.broadcastMessage("&c赤チーム: &6" + redP + "&c点&f (" + redS + "&f)");
        }
        if (!"".equals(blueS)) {
            Actions.broadcastMessage("&b青チーム得点: &6" + blueP + "&b点&f (" + blueS + "&f)");
        }
        if (!"".equals(noneS)) {
            Actions.broadcastMessage("&7無効フラッグ: &6" + noneP + "&7点&f (" + noneS + "&f)");
        }
        if (winTeam != null) {
            Actions.broadcastMessage(msgPrefix + winTeam.getColor() + winTeam.getTeamName() + "チーム の勝利です！ &7(&c" + redP + "&7 - &b" + blueP + "&7)");
        } else {
            Actions.broadcastMessage(msgPrefix + "&6このゲームは引き分けです！ &7(&c" + redP + "&7 - &b" + blueP + "&7)");
        }
        Actions.broadcastMessage("&c赤チームKill数: &6" + getKillCount(GameTeam.RED) + "&b 青チームKill数: &6" + getKillCount(GameTeam.BLUE));

        log("========================================");

        // 賞金支払い
        if (winTeam != null && stage.getAward() > 0) {
            for (FGPlayer fgp : playersMap.get(winTeam)) {
                Player player = fgp.getPlayer();
                if (player != null && player.isOnline()) {
                    // 入金
                    if (Actions.addMoney(fgp.getUUID(), stage.getAward())) {
                        Actions.message(player, "&a[+]おめでとうございます！賞金として" + stage.getAward() + "Coinを得ました！");
                        log("+ Player " + fgp.getUUID() + " received " + stage.getAward() + "Coin!");
                    } else {
                        Actions.message(player, "&c報酬受け取りにエラーが発生しました。管理人までご連絡ください。");
                        log("* [Error] Player " + fgp.getName() + " failed to receive " + stage.getAward() + "Coin ?");
                    }
                }
            }
        }

        // カウント
        if (winTeam != null) {
            addPlayerResultCounts(GameResult.TEAM_WIN, winTeam);
        } else {
            addPlayerResultCounts(GameResult.DRAW, null);
        }

        // Logging
        log("========================================");
        log(" * FlagGame Finished");
        log(" RedTeam: " + redP + " point - " + redS);
        log("BlueTeam: " + blueP + " point - " + blueS);
        log(" Invalid: " + noneP + " point - " + noneS);
        log("========================================");
        if (winTeam == null) {
            log(" *** DRAW GAME *** (" + redP + " - " + blueP + ")");
        } else {
            log(" *** WIN TEAM: " + winTeam.name() + " *** (" + redP + " - " + blueP + ")");
        }
        log("========================================");

        // ログの終わり
        gameID = null;

        // 参加プレイヤーをスポーン地点に移動させる
        tpSpawnLocation();

        // 同じゲーム参加者のインベントリをクリア
        for (FGPlayer name : getPlayersSet()) {
            if (name == null) {
                continue;
            }
            Player player = name.getPlayer();

            // オフラインプレイヤーはスキップ
            if (player != null && player.isOnline()) {
                // アイテムクリア
                player.getInventory().clear();
                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);

                // TABリスト名を戻す
                restorePlayerListColor(player);

                // 参加中のゲーム情報更新
                PlayerManager.getPlayer(player.getName()).setPlayingGame(null);
            }
        }

        // Call event
        GameFinishedEvent finishedEvent
                = new GameFinishedEvent(stage, (winTeam == null) ? GameResult.DRAW : GameResult.TEAM_WIN, winTeam, playersMap);
        plugin.getServer().getPluginManager().callEvent(finishedEvent);

        // 後処理
        stage.setUsing(false);
        stage.setGame(null);
        GameManager.removeGame(this.getName());

        // 破壊
        clean();

        // フラッグブロックロールバック 終了時はロールバックしない
        // rollbackFlags();
        // 初期化
        // init();
    }

    /*
     * 結果を指定してゲームを終了する
     *
     * @param result
     *            結果
     * @param winTeam
     *            GameResult.TEAM_WIN の場合の勝利チーム
     */
    public void finish(GameResult result, GameTeam winTeam, String reason) {
        if (result == null || (result == GameResult.TEAM_WIN && winTeam == null)) {
            this.plugin.getLogger().warning("Error on method finish(GameResult, GameTeam)! Please report this!");
            return;
        }

        Actions.broadcastMessage(msgPrefix + "&2フラッグゲーム'&6" + stage.getName() + "&2'は中断されました");

        // 指定した結果で追加処理
        switch (result) {
            case TEAM_WIN:
                Actions.broadcastMessage(msgPrefix + "&2このゲームは" + winTeam.getColor() + winTeam.getTeamName() + "チーム&2の勝ちになりました");
                addPlayerResultCounts(GameResult.TEAM_WIN, winTeam);
                break;
            case DRAW:
                Actions.broadcastMessage(msgPrefix + "&2このゲームは引き分けになりました");
                addPlayerResultCounts(GameResult.DRAW, null);
                break;
            case STOP:
                Actions.broadcastMessage(msgPrefix + "&2このゲームは&c無効&2になりました");
                addPlayerResultCounts(GameResult.STOP, null);
                break;
            default:
                this.plugin.getLogger().warning("Undefined GameResult! Please report this!");
                return;
        }

        if (reason != null && !"".equals(reason)) {
            Actions.broadcastMessage(msgPrefix + "&2理由: " + reason);
        }

        // Logging
        log("========================================");
        log(" * FlagGame Finished (Manually)");
        log(" Result: " + result.name());
        if (result == GameResult.TEAM_WIN) {
            log("WinTeam: " + winTeam.name());
        }
        log(" Reason: " + reason);
        log("========================================");

        // ログの終わり
        gameID = null;

        // 参加プレイヤーをスポーン地点に移動させる
        tpSpawnLocation();
        // 同じゲーム参加者のインベントリをクリア
        for (FGPlayer name : getPlayersSet()) {
            if (name == null) {
                continue;
            }
            Player player = name.getPlayer();

            // オフラインプレイヤーはスキップ
            if (player != null && player.isOnline()) {
                // アイテムクリア
                player.getInventory().clear();
                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);

                // TABリスト名を戻す
                restorePlayerListColor(player);

                // 参加中のゲーム情報更新
                PlayerManager.getPlayer(player.getName()).setPlayingGame(null);
            }
        }
        // Call event
        GameFinishedEvent finishedEvent = new GameFinishedEvent(stage, result, winTeam, reason, playersMap);
        plugin.getServer().getPluginManager().callEvent(finishedEvent);

        // 後処理
        stage.setUsing(false);
        stage.setGame(null);
        GameManager.removeGame(this.getName());

        cancelTimerTask();
        clean();

        // 初期化
        // init();
    }

    public void restorePlayerListColor(final Player player) {
        final String tabname = tabListMap.remove(PlayerManager.getPlayer(player));
        if (tabname != null) {
            player.setPlayerListName(tabname);
        }
    }

    /**
     * メモリ使用削減・GCで拾われやすくするために、このインスタンスを可能な限り破棄する finish()
     * が呼ばれ、再度このインスタンスを参照しなくなった時に呼び出す
     */
    private void clean() {
        if (stage != null && this.equals(stage.getGame())) {
            stage.setUsing(false);
            stage.setGame(null);
        }

        cancelTimerTask();

        this.playersMap = null;
        this.redPlayers = null;
        this.bluePlayers = null;
        this.teamKilledCount = null;
        this.gameID = null;
        this.stage = null;
    }

    /**
     * プレイヤーのプロフィールのゲーム成績を更新する
     *
     * @param result 結果
     * @param winTeam 勝利チーム
     */
    private void addPlayerResultCounts(GameResult result, GameTeam winTeam) {
        // オフラインプレイヤーリスト
        List<FGPlayer> offlines = new ArrayList<>();
        offlines.clear();

        if (result == GameResult.STOP) {
            for (Set<FGPlayer> names : playersMap.values()) {
                for (FGPlayer fgp : names) {
                    PlayerProfile prof = fgp.getProfile();
                    prof.setPlayed(prof.getPlayed() - 1);
                }
            }
            return;
        } else if (result == GameResult.DRAW) {
            // 引き分け
            for (Map.Entry<GameTeam, Set<FGPlayer>> entry : playersMap.entrySet()) {
                for (FGPlayer fgp : entry.getValue()) {
                    Player player = fgp.getPlayer();
                    if (player.isOnline()) {
                        fgp.getProfile().addDraw(); // draw++
                    } else {
                        offlines.add(fgp);
                    }
                }
            }
        } else if (result == GameResult.TEAM_WIN) {
            // Set Lose team
            GameTeam loseTeam = null;
            if (winTeam.equals(GameTeam.RED)) {
                loseTeam = GameTeam.BLUE;
            } else if (winTeam.equals(GameTeam.BLUE)) {
                loseTeam = GameTeam.RED;
            }

            // Win team
            for (FGPlayer fgplayer : playersMap.get(winTeam)) {
                Player player = fgplayer.getPlayer();
                if (player.isOnline()) {
                    fgplayer.getProfile().addWin(); // win++
                } else {
                    offlines.add(fgplayer);
                }
            }

            // Lose team
            if (loseTeam != null) {
                for (FGPlayer fgplayer : playersMap.get(loseTeam)) {
                    Player player = fgplayer.getPlayer();
                    if (player.isOnline()) {
                        fgplayer.getProfile().addLose(); // lose++
                    } else {
                        offlines.add(fgplayer);
                    }
                }
            }
        }

        // オフラインユーザーは途中退場カウント追加
        offlines.stream().map(FGPlayer::getProfile).forEach(PlayerProfile::addExit);
    }

    /**
     * プレイヤーリストをマップにマッピングする
     */
    private void mappingPlayersList() {
        // 一度クリア
        playersMap.clear();
        // マッピング
        playersMap.put(GameTeam.RED, redPlayers);
        playersMap.put(GameTeam.BLUE, bluePlayers);
    }

    /* ***** 参加プレイヤー関係 ***** */
    /**
     * プレイヤーを少ないチームに自動で参加させる 同じなら赤チーム
     *
     * @param player 参加させるプレイヤー
     */
    public void join(FGPlayer player) {
        // 赤チームのが少ないか、または同じなら赤チームに追加 それ以外は青チームに追加
        if (redPlayers.size() <= bluePlayers.size()) {
            join(player, GameTeam.RED);
        } else {
            join(player, GameTeam.BLUE);
        }
    }

    /**
     * プレイヤーリストにプレイヤーを追加する
     *
     * @param player 追加するプレイヤー
     * @param team 追加するチーム
     */
    public void join(FGPlayer player, GameTeam team) {
        // チームの存在確認
        if (player == null || team == null || !playersMap.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        // 人数チェック
        if (playersMap.get(team).size() >= stage.getTeamLimit()) {
            throw new IllegalStateException("The team is full");
        }
        // 追加
        playersMap.get(team).add(player);
        log("+ Player " + player.getName() + " joined " + team.name() + " Team!");
    }

    /**
     * プレイヤーリストからプレイヤーを削除する
     *
     * @param player 対象のプレイヤー
     * @param team 対象チーム nullなら全チームから
     */
    public void leave(FGPlayer player, GameTeam team) {
        if (player == null || (team != null && !playersMap.containsKey(team))) {
            throw new IllegalArgumentException();
        }
        // 削除
        if (team != null) {
            playersMap.get(team).remove(player);
        } else {
            // チームがnullなら全チームから削除
            playersMap.values().forEach(m -> m.remove(player));
        }
    }

    public void leave(FGPlayer player) {
        if (player == null) {
            throw new NullPointerException();
        }
        this.playersMap.values().forEach(m -> m.remove(player));
    }

    /**
     * プレイヤーが所属しているチームを返す
     *
     * @param player 対象のプレイヤー
     * @return GameTeam または所属なしの場合 null
     */
    public GameTeam getPlayerTeam(FGPlayer player) {
        String name = player.getName();
        for (Map.Entry<GameTeam, Set<FGPlayer>> ent : playersMap.entrySet()) {
            // すべてのチームセットを回す
            if (ent.getValue().contains(player)) {
                return ent.getKey();
            }
        }
        // 一致なし nullを返す
        return null;
    }

    /**
     * プレイヤーマップを返す
     *
     * @return
     */
    public Map<GameTeam, Set<FGPlayer>> getPlayersMap() {
        return playersMap;
    }

    public Set<FGPlayer> getPlayersSet() {
        Set<FGPlayer> ret = new HashSet<>();
        for (Set<FGPlayer> teamSet : playersMap.values()) {
            ret.addAll(teamSet);
        }
        return ret;
    }

    public boolean isJoined(FGPlayer player) {
        return getPlayersSet().contains(player);
    }

    public boolean isJoined(Player player) {
        if (player == null) {
            return false;
        }
        return this.isJoined(PlayerManager.getPlayer(player));
    }

    /* ***** 参加しているプレイヤーへのアクション関係 ***** */

    /*
     * ゲーム参加者全員にメッセージを送る
     *
     * @param msg
     *            メッセージ
     */
    public void message(String message) {
        // イベントワールド全員に送る？ 全チームメンバーに送る？
        // とりあえずワールドキャストする → ワールドキャストの場合同時進行が行えない
        // Actions.worldcastMessage(Bukkit.getWorld(plugin.getConfigs().gameWorld),
        // msg);

        // 全チームメンバーにメッセージを送る
        for (Set<FGPlayer> set : playersMap.values()) {
            for (FGPlayer name : set) {
                if (name == null) {
                    continue;
                }
                Player player = name.getPlayer();
                if (player != null && player.isOnline()) {
                    Actions.message(player, message);
                }
            }
        }
    }

    /*
     * 特定チームにのみメッセージを送る
     *
     * @param msg
     *            メッセージ
     * @param team
     *            対象のチーム
     */
    public void message(GameTeam team, String message) {
        if (team == null || !playersMap.containsKey(team)) {
            return;
        }

        // チームメンバーでループさせてメッセージを送る
        playersMap.get(team).stream()
                .filter(name -> name != null)
                .map(FGPlayer::getPlayer)
                .filter(player -> player != null)
                .filter(Player::isOnline)
                .forEach(player -> Actions.message(player, message));
    }

    /**
     * 参加プレイヤーをスポーン地点へ移動させる
     */
    public void tpSpawnLocation() {
        // 参加プレイヤーマップを回す
        for (Map.Entry<GameTeam, Set<FGPlayer>> entry : playersMap.entrySet()) {
            GameTeam team = entry.getKey();
            Location loc = stage.getSpawn(team);
            // チームのスポーン地点が未設定の場合何もしない
            if (loc == null) {
                continue;
            }

            // チームの全プレイヤーをスポーン地点にテレポート
            for (FGPlayer name : entry.getValue()) {
                if (name == null) {
                    continue;
                }
                final Player player = name.getPlayer();
                if (player != null && player.isOnline()) {
                    // イスに座っているときにワープできない不具合修正
                    Entity vehicle = player.getVehicle();
                    if (vehicle != null) {
                        // アイテムに座っている＝イスプラグインを使って座っている
                        if (vehicle instanceof Item) {
                            vehicle.remove(); // アイテム削除
                        } else {
                            // その他、ボートやマインカートなら単にeject
                            // vehicle.eject();
                            player.leaveVehicle();
                        }
                    }

                    // 現在地点が別ワールドならプレイヤーデータに戻る地点を書き込む
                    if (!player.getWorld().equals(loc.getWorld())) {
                        PlayerManager.getProfile(player).setTpBackLocation(player.getLocation());
                    }

                    player.teleport(loc, TeleportCause.PLUGIN);
                }
            }
        }
    }

    /**
     * 指定したチームのプレイヤーセットを返す
     *
     * @param team 取得するチーム
     * @return プレイヤーセット またはnull
     */
    public Set<FGPlayer> getPlayersSet(GameTeam team) {
        if (team == null || !playersMap.containsKey(team)) {
            return null;
        }

        return Collections.unmodifiableSet(playersMap.get(team));
    }

    /* ***** Kill/Death関係 ***** */
    public void addKillCount(GameTeam team) {
        if (!teamKilledCount.containsKey(team)) {
            teamKilledCount.put(team, 1);
        } else {
            teamKilledCount.put(team, teamKilledCount.get(team) + 1);
        }
    }

    public int getKillCount(GameTeam team) {
        if (!teamKilledCount.containsKey(team)) {
            return 0;
        } else {
            return teamKilledCount.get(team);
        }
    }

    /* ***** タイマー関係 ***** */

    /*
     * 開始時のカウントダウンタイマータスクを開始する
     */
    public void start_timer(final CommandSender sender) {
        // カウントダウン秒をリセット
        starttimerInSec = plugin.getConfigs().getStartCountdownInSec();
        if (starttimerInSec <= 0) {
            start(sender);
            return;
        }

        if (!random) {
            Actions.broadcastMessage(msgPrefix + "&2まもなくゲーム'&6" + stage.getName() + "&2'が始まります！");
        } else {
            Actions.broadcastMessage(msgPrefix + "&2まもなくゲーム'&6ランダムステージ&2'が始まります！");
        }

        // タイマータスク起動
        // starttimerThreadID =
        // plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin,
        // new Runnable() {
        starttimerThreadID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {
                /* 1秒ごとに呼ばれる */

                // 残り時間がゼロになった
                if (starttimerInSec <= 0) {
                    cancelTimerTask(); // タイマー停止
                    start(sender); // ゲーム開始
                    return;
                }

                message(msgPrefix + "&aあと" + starttimerInSec + "秒でこのゲームが始まります！");
                starttimerInSec--;
            }
        }, 0L, 20L);
    }

    /**
     * メインのタイマータスクを開始する
     */
    public void timer() {
        // タイマータスク
        // timerThreadID =
        // plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin,
        // new Runnable() {
        timerThreadID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new GameTimerTask(this.plugin, this), 0L, 20L);

        /*
         * timerThreadID =
         * plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
         * new Runnable() { public void run(){ // 1秒ごとに呼ばれる
         *
         * // 残り時間がゼロになった if (remainSec <= 0){ cancelTimerTask(); // タイマー停止
         * finish(); // ゲーム終了 return; }
         *
         * // 15秒以下 if (remainSec <= 15){ message(msgPrefix+
         * "&aゲーム終了まで あと "+remainSec+" 秒です！"); } // 30秒前 else if (remainSec ==
         * 30){ message(msgPrefix+ "&aゲーム終了まで あと "+remainSec+" 秒です！"); } //
         * 60秒間隔 else if ((remainSec % 60) == 0){ int remainMin = remainSec /
         * 60; message(msgPrefix+ "&aゲーム終了まで あと "+remainMin+" 分です！"); }
         *
         * remainSec--; } }, 0L, 20L);
         */
    }

    /**
     * タイマータスクが稼働中の場合停止する
     */
    public void cancelTimerTask() {
        if (this.state == State.ENTRY && starttimerThreadID != -1) {
            plugin.getServer().getScheduler().cancelTask(starttimerThreadID);
        }
        if (this.state == State.STARTED && timerThreadID != -1) {
            // タスクキャンセル
            plugin.getServer().getScheduler().cancelTask(timerThreadID);
        }
    }

    /**
     * このゲームの残り時間(秒)を取得する
     *
     * @return 残り時間(秒)
     */
    public int getRemainTime() {
        return remainSec;
    }

    public void tickRemainTime() {
        remainSec--;
    }

    /* getter / setter */
    public String getName() {
        return this.getStage().getName();
    }

    public State getState() {
        return this.state;
    }

    public boolean isRandom() {
        return this.random;
    }

    public int getStarttimerThreadID() {
        return starttimerThreadID;
    }

    public Stage getStage() {
        return this.stage;
    }

    /**
     * 各ゲームごとのログを取る
     *
     * @param line ログ
     */
    public void log(String line) {
        if (gameID != null) {
            String filepath = plugin.getConfigs().getDetailDirectory() + gameID + ".log";
            Actions.log(filepath, line);
        }
    }
}
