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
package syam.flaggame.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import syam.flaggame.FlagGame;

import syam.flaggame.enums.TeamColor;
import syam.flaggame.enums.config.ConfigType;
import syam.flaggame.enums.config.Configables;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;
import syam.flaggame.util.Util;
import syam.flaggame.util.WorldEditHandler;

public class SetCommand extends BaseCommand {
    /*
     * TODO: 設定によってコンソールから実行可能にする Confiable列挙にbePlayer (boolean)
     * を追加するか、ConfigType.Area
     */

    public SetCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = true;
        name = "set";
        argLength = 0;
        usage = "<option> [value] <- set option";
    }

    /**
     * コマンド実行時に呼ばれる
     *
     * @throws CommandException
     */
    @Override
    public void execute() throws CommandException {
        // flag set のみ (サブ引数なし)
        if (args.size() <= 0) {
            Actions.message(player, "&c設定項目を指定してください！");
            sendAvailableConf();
            return;
        }
        
        // ゲーム取得
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        Stage stage = gPlayer.getSetupSession()
                .orElseThrow(() -> new CommandException("&c先に編集するゲームを選択してください")).getSelectedStage();

        // 開始中でないかチェック
        if (stage.isReserved()) {
            Actions.message(sender, "&cこのステージは受付中か開始中のため設定変更できません！");
            return;
        }

        // 設定可能項目名を回す
        Configables conf;
        try {
            conf = Configables.valueOf(args.get(0).toUpperCase());
        } catch (IllegalArgumentException ex) {
            Actions.message(sender, "&cその設定項目は存在しません！");
            sendAvailableConf();
            return;
        }

        // 設定タイプが ConfigType.SIMPLE の場合はサブ引数が2つ以上必要
        if (conf.getConfigType() == ConfigType.SIMPLE) {
            if (args.size() < 2) {
                throw new CommandException("&c引数が足りません！ 設定する値を入力してください！");
            }
        }

        // 設定項目によって処理を分ける
        switch (conf) {
            /* 一般 */
            case STAGE: // ステージ設定
                setStage(stage);
                return;
            case BASE: // 拠点設定
                setBase(stage);
                return;
            case SPAWN: // スポーン地点設定
                setSpawn(stage);
                return;
            case FLAG: // フラッグ設定
                setFlag(stage);
                return;
            case CHEST: // チェスト設定
                setChest(stage);
                return;
            case SPECSPAWN: // 観戦者スポーン設定
                setSpecSpawn(stage);
                return;

            /* オプション */
            case GAMETIME: // 制限時間
                setGameTime(stage);
                return;
            case TEAMLIMIT: // チーム人数制限
                setTeamLimit(stage);
                return;
            case PROTECT: // ステージ保護
                setStageProtect(stage);
                return;
            case AVAILABLE: // 有効設定
                setStageAvailable(stage);
                return;

            // 定義漏れ
            default:
                Actions.message(sender, "&c設定項目が不正です 開発者にご連絡ください");
                log.warning(logPrefix + "Undefined configables! Please report this!");
                break;
        }
    }

    /* ***** ここから各設定関数 ****************************** */
    // 一般
    private void setStage(Stage game) throws CommandException {
        // WorldEdit選択領域取得
        Block[] corners = WorldEditHandler.getWorldEditRegion(player);
        // エラー プレイヤーへのメッセージ送信はWorldEditHandlerクラスで処理
        if (corners == null || corners.length != 2) {
            return;
        }

        Block block1 = corners[0];
        Block block2 = corners[1];

        // ワールドチェック
        if (block1.getWorld() != Bukkit.getWorld(plugin.getConfigs().getGameWorld())) {
            throw new CommandException("&c指定しているエリアはゲームワールドではありません！");
        }

        // ステージ設定
        game.setStageArea(block1.getLocation(), block2.getLocation());

        Actions.message(player, "&aステージ'" + game.getName() + "'のエリアを設定しました！");
        plugin.getDynmap().updateRegion(game);
    }

    /**
     * 拠点エリア設定
     *
     * @param game
     * @return true
     * @throws CommandException
     */
    private void setBase(Stage game) throws CommandException {
        // 引数チェック
        if (args.size() < 2) {
            throw new CommandException("&c引数が足りません！設定するチームを指定してください！");
        }

        // チーム取得
        TeamColor team = null;
        for (TeamColor tm : TeamColor.values()) {
            if (tm.name().toLowerCase().equalsIgnoreCase(args.get(1))) {
                team = tm;
                break;
            }
        }
        if (team == null) {
            throw new CommandException("&cチーム'" + args.get(1) + "'が見つかりません！");
        }

        // WorldEdit選択領域取得
        Block[] corners = WorldEditHandler.getWorldEditRegion(player);
        // エラー プレイヤーへのメッセージ送信はWorldEditHandlerクラスで処理
        if (corners == null || corners.length != 2) {
            return;
        }

        Block block1 = corners[0];
        Block block2 = corners[1];

        // ワールドチェック
        if (block1.getWorld() != Bukkit.getWorld(plugin.getConfigs().getGameWorld())) {
            throw new CommandException("&c指定しているエリアはゲームワールドではありません！");
        }

        // 拠点設定
        game.setBase(team, block1.getLocation(), block2.getLocation());

        Actions.message(player, "&a" + team.getTeamName() + "チームの拠点を設定しました！");
        plugin.getDynmap().updateRegion(game);
    }

    /**
     * スポーン地点設定
     *
     * @param game
     * @return true
     * @throws CommandException
     */
    private void setSpawn(Stage game) throws CommandException {
        // 引数チェック
        if (args.size() < 2) {
            throw new CommandException("&c引数が足りません！設定するチームを指定してください！");
        }

        // チーム取得
        TeamColor team = null;
        for (TeamColor tm : TeamColor.values()) {
            if (tm.name().toLowerCase().equalsIgnoreCase(args.get(1))) {
                team = tm;
                break;
            }
        }
        if (team == null) {
            throw new CommandException("&cチーム'" + args.get(1) + "'が見つかりません！");
        }

        // スポーン地点設定
        game.setSpawn(team, player.getLocation());

        Actions.message(player, "&a" + team.getTeamName() + "チームのスポーン地点を設定しました！");
        plugin.getDynmap().updateRegion(game);
    }

    /**
     * フラッグ管理モード
     *
     * @param game
     * @return true
     * @throws CommandException
     */
    private void setFlag(Stage game) throws CommandException {
        // 引数チェック
        if (args.size() < 2) {
            throw new CommandException("&c引数が足りません！フラッグの得点を指定してください！");
        }

        // フラッグタイプチェック
        byte type;
        try {
            type = Byte.parseByte(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("フラッグの得点を正しく指定してください!", ex);
        }

        // マネージャーセット
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(game).setSetting(Configables.FLAG).setSelectedFlagType(type);
        String tool = Material.getMaterial(plugin.getConfigs().getToolID()).name();
        Actions.message(player, "&aフラッグ管理モードを開始しました。選択ツール: " + tool);
    }

    /**
     * チェスト管理モード
     *
     * @param game
     * @return true
     */
    private void setChest(Stage game) {
        // マネージャーセット
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(game).setSetting(Configables.CHEST);
        String tool = Material.getMaterial(plugin.getConfigs().getToolID()).name();
        Actions.message(player, "&aチェスト管理モードを開始しました。選択ツール: " + tool);
    }

    /**
     * 観戦者スポーン地点
     *
     * @param game 設定対象のゲームイン寸タンス
     * @return
     */
    private void setSpecSpawn(Stage game) {
        // 観戦者スポーン地点設定
        game.setSpecSpawn(player.getLocation());

        Actions.message(player, "&aステージ'" + game.getName() + "'の観戦者スポーン地点を設定しました！");
        plugin.getDynmap().updateRegion(game);
    }

    // オプション
    private void setGameTime(Stage game) throws CommandException {
        int num = 60 * 10; // デフォルト10分
        try {
            num = Integer.parseInt(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("&cオプションの値が整数ではありません！");
        }

        if (num <= 0) {
            throw new CommandException("&c値が不正です！正数を入力してください！");
        }
        game.setGameTimeInSec(num);

        String sec = num + "秒";
        if (num >= 60) {
            sec = sec + "(" + Actions.getTimeString(num) + ")";
        }
        Actions.message(sender, "&aステージ'" + game.getName() + "'のゲーム時間は " + sec + " に設定されました！");
    }

    private void setTeamLimit(Stage game) throws CommandException {
        int cnt = 8; // デフォルト8人
        try {
            cnt = Integer.parseInt(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("&cオプションの値が整数ではありません！");
        }

        if (cnt <= 0) {
            throw new CommandException("&c値が不正です！正数を入力してください！");
        }

        game.setTeamLimit(cnt);

        Actions.message(sender, "&aステージ'" + game.getName() + "'のチーム毎人数上限値は " + cnt + "人 に設定されました！");
        plugin.getDynmap().updateRegion(game);
    }

    private void setStageProtect(Stage stage) throws CommandException {
        Boolean protect = true; // デフォルトtrue
        String value = args.get(1).trim();

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")) {
            protect = true;
        } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no")) {
            protect = false;
        } else {
            throw new CommandException("&c値が不正です！true または false を指定してください！");
        }

        String result = "&a有効";
        if (!protect) {
            result = "&c無効";
        }

        stage.setStageProtected(protect);
        Actions.message(sender, "&aステージ'" + stage.getName() + "'の保護は " + result + " &aに設定されました！");
        plugin.getDynmap().updateRegion(stage);
    }

    private void setStageAvailable(Stage stage) throws CommandException {
        Boolean available = true; // デフォルトtrue
        String value = args.get(1).trim();

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")) {
            available = true;
        } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no")) {
            available = false;
        } else {
            throw new CommandException("&c値が不正です！true または false を指定してください！");
        }

        // TODO:ステージを有効化するときは、ステージが開始出来る状態かチェックする
        if (available) {

        }

        String result = "&a可能";
        if (!available) {
            result = "&c不可";
        }

        stage.setAvailable(available);
        Actions.message(sender, "&aステージ'" + stage.getName() + "'は使用" + result + "&aに設定されました！");
    }

    /* ***** ここまで **************************************** */
    /**
     * 設定可能な設定とヘルプをsenderに送信する
     */
    private void sendAvailableConf() {
        List<String> col = new ArrayList<>();
        for (Configables conf : Configables.values()) {
            col.add(conf.name());
        }

        Actions.message(sender, "&6 " + Util.join(col, "/").toLowerCase());
        // Actions.message(sender,
        // "&6 stage / base / spawn / flag / chest / gametime / teamlimit / award / entryfee / protect");
    }

    @Override
    public boolean permission() {
        return Perms.SET.has(sender);
    }
}
