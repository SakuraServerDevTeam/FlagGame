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
package syam.flaggame.command;

import java.util.ArrayList;
import java.util.List;
import jp.llv.flaggame.game.permission.GamePermission;
import jp.llv.flaggame.game.permission.GamePermissionState;

import org.bukkit.Material;
import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.rollback.StageDataType;
import syam.flaggame.game.Configables;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.StageReservedException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;
import syam.flaggame.util.Cuboid;
import syam.flaggame.util.WorldEditHandler;

public class SetCommand extends BaseCommand {

    /*
     * TODO: 設定によってコンソールから実行可能にする Confiable列挙にbePlayer (boolean)
     * を追加するか、ConfigType.Area
     */
    public SetCommand(FlagGame plugin) {
        super(
                plugin,
                true,
                0,
                "<option> [value] <- set option",
                "set"
        );

    }

    /**
     * コマンド実行時に呼ばれる
     *
     * @throws CommandException
     */
    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        // flag set のみ (サブ引数なし)
        if (args.size() <= 0) {
            Actions.message(player, "&c設定項目を指定してください！");
            sendAvailableConf(player);
            return;
        }

        // ゲーム取得
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        Stage stage = gPlayer.getSetupSession()
                .orElseThrow(() -> new CommandException("&c先に編集するゲームを選択してください")).getSelectedStage();

        // 設定可能項目名を回す
        Configables conf;
        try {
            conf = Configables.valueOf(args.get(0).toUpperCase());
        } catch (IllegalArgumentException ex) {
            Actions.message(player, "&cその設定項目は存在しません！");
            sendAvailableConf(player);
            return;
        }

        // 設定タイプが ConfigType.SIMPLE の場合はサブ引数が2つ以上必要
        if (conf.getType() == Configables.ConfigType.SIMPLE) {
            if (args.size() < 2) {
                throw new CommandException("&c引数が足りません！ 設定する値を入力してください！");
            }
        }

        // 設定項目によって処理を分ける
        try {
            switch (conf) {
                /* 一般 */
                case SPAWN: // スポーン地点設定
                    setSpawn(player, stage, args);
                    return;
                case FLAG: // フラッグ設定
                    setFlag(player, stage, args);
                    return;
                case CHEST: // チェスト設定
                    setChest(player, stage, args);
                    return;
                case NEXUS:
                    setNexus(player, stage, args);
                    return;
                case BANNER_SLOT:
                    setBannerSlot(player, stage, args);
                    return;
                case BANNER_SPAWNER:
                    setBannerSpawner(player, stage, args);
                    return;
                case SPECSPAWN: // 観戦者スポーン設定
                    setSpecSpawn(player, stage, args);
                    return;

                /* オプション */
                case GAMETIME: // 制限時間
                    setGameTime(player, stage, args);
                    return;
                case TEAMLIMIT: // チーム人数制限
                    setTeamLimit(player, stage, args);
                    return;
                case AVAILABLE: // 有効設定
                    setStageAvailable(player, stage, args);
                    return;
                case PROTECT:
                    setStageProtect(player, stage, args);
                    return;
                case KILLSCORE:
                    setKillScore(player, stage, args);
                    return;
                case DEATHSCORE:
                    setDeathScore(player, stage, args);
                    return;
                case ENTRYFEE:
                    setEntryFee(player, stage, args);
                case PRIZE:
                    setPrize(player, stage, args);
                case COOLDOWN:
                    setCooldown(player, stage, args);
                    return;

                /* stage description */
                case AUTHOR:
                    setAuthor(player, stage, args);
                    return;
                case DESCRIPTION:
                    setDescription(player, stage, args);
                    return;
                case GUIDE:
                    setGuide(player, stage, args);
                    return;

                /* shortcut */
                case STAGE:
                    setStage(player, stage, args);
                    return;
                case BASE:
                    setBase(player, stage, args);
                    return;

                // 定義漏れ
                default:
                    throw new CommandException("&c設定項目が不正です 開発者にご連絡ください");
            }
        } catch (StageReservedException ex) {
            Actions.message(player, "&cステージ" + stage.getName() + "は現在編集不可です!");
        }
    }

    /* ***** ここから各設定関数 ****************************** */
    // 一般
    /**
     * スポーン地点設定
     *
     * @param game
     * @return true
     * @throws CommandException
     */
    private void setSpawn(Player player, Stage game, List<String> args) throws CommandException, StageReservedException {
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
        if (team == TeamColor.WHITE) {
            throw new CommandException(team.getRichName() + "&cのスポーン地点を設定することはできません！");
        }
        if (args.size() >= 3 && args.get(2).equalsIgnoreCase("none")) {
            game.setSpawn(team, null);
            Actions.message(player, "&a" + team.getTeamName() + "&aチームのスポーン地点を削除しました！");
            return;
        }

        // スポーン地点設定
        game.setSpawn(team, player.getLocation());

        Actions.message(player, "&a" + team.getTeamName() + "&aチームのスポーン地点を設定しました！");
        plugin.getDynmap().updateRegion(game);
    }

    /**
     * フラッグ管理モード
     *
     * @param game
     * @return true
     * @throws CommandException
     */
    private void setFlag(Player player, Stage game, List<String> args) throws CommandException {
        // 引数チェック
        if (args.size() < 2) {
            throw new CommandException("&c引数が足りません！フラッグの得点を指定してください！");
        }

        // フラッグタイプチェック
        double type;
        try {
            type = Double.parseDouble(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("フラッグの得点を正しく指定してください!", ex);
        }

        // マネージャーセット
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(game).setSetting(Configables.FLAG).setSelectedPoint(type);
        String tool = Material.getMaterial(plugin.getConfigs().getToolID()).name();
        Actions.message(player, "&aフラッグ管理モードを開始しました。選択ツール: " + tool);
    }

    private void setNexus(Player player, Stage stage, List<String> args) throws CommandException {
        if (args.size() < 2) {
            throw new CommandException("&c引数が足りません！目標の得点を正しく指定してください!");
        }

        double point;
        try {
            point = Double.parseDouble(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("目標の得点を正しく指定してください!", ex);
        }

        TeamColor color;
        if (args.size() < 3) {
            color = null;
        } else {
            try {
                color = TeamColor.of(args.get(2));
            } catch (IllegalArgumentException ex) {
                throw new CommandException("目標のチーム色を正しく指定してください!", ex);
            }
        }

        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(stage).setSetting(Configables.NEXUS)
                .setSelectedPoint(point)
                .setSelectedColor(color);
        String tool = Material.getMaterial(plugin.getConfigs().getToolID()).name();
        Actions.message(player, "&a目標管理モードを開始しました。選択ツール: " + tool);
    }

    private void setBannerSpawner(Player player, Stage stage, List<String> args) throws CommandException {
        if (args.size() < 3) {
            throw new CommandException("&c引数が足りません! バナーの得点と耐久度を正しく指定してください!");
        }

        byte point, hp;
        try {
            point = Byte.parseByte(args.get(1));
            hp = Byte.parseByte(args.get(2));
        } catch (NumberFormatException ex) {
            throw new CommandException("&c数値フォーマットが異常です!");
        }

        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(stage).setSetting(Configables.BANNER_SPAWNER)
                .setSelectedPoint(point)
                .setHp(hp);
        String tool = Material.getMaterial(plugin.getConfigs().getToolID()).name();
        Actions.message(player, "&aバナースポナー管理モードを開始しました。選択ツール: " + tool);
    }

    private void setBannerSlot(Player player, Stage stage, List<String> args) throws CommandException {
        TeamColor color;
        if (args.size() < 2) {
            color = null;
        } else {
            try {
                color = TeamColor.of(args.get(1));
            } catch (IllegalArgumentException ex) {
                throw new CommandException("目標のチーム色を正しく指定してください!", ex);
            }
        }

        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(stage).setSetting(Configables.BANNER_SLOT)
                .setSelectedColor(color);
        String tool = Material.getMaterial(plugin.getConfigs().getToolID()).name();
        Actions.message(player, "&aスロット管理モードを開始しました。選択ツール: " + tool);
    }

    /**
     * チェスト管理モード
     *
     * @param game
     * @return true
     */
    private void setChest(Player player, Stage game, List<String> args) {
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
    private void setSpecSpawn(Player player, Stage game, List<String> args) throws StageReservedException {
        // 観戦者スポーン地点設定
        game.setSpecSpawn(player.getLocation());

        Actions.message(player, "&aステージ'" + game.getName() + "'の観戦者スポーン地点を設定しました！");
        plugin.getDynmap().updateRegion(game);
    }

    // オプション
    private void setGameTime(Player player, Stage game, List<String> args) throws CommandException, StageReservedException {
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
        Actions.message(player, "&aステージ'" + game.getName() + "'のゲーム時間は " + sec + " に設定されました！");
    }

    private void setCooldown(Player player, Stage game, List<String> args) throws CommandException, StageReservedException {
        int num = 0; // デフォルト0秒
        try {
            num = Integer.parseInt(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("&cオプションの値が整数ではありません！");
        }

        if (num <= 0) {
            throw new CommandException("&c値が不正です！正数を入力してください！");
        }
        game.setCooldown(num * 20L);

        String sec = num + "秒";
        if (num >= 60) {
            sec = sec + "(" + Actions.getTimeString(num) + ")";
        }
        Actions.message(player, "&aステージ'" + game.getName() + "'のゲーム時間は " + sec + " に設定されました！");
    }

    private void setTeamLimit(Player player, Stage game, List<String> args) throws CommandException, StageReservedException {
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

        Actions.message(player, "&aステージ'" + game.getName() + "'のチーム毎人数上限値は " + cnt + "人 に設定されました！");
        plugin.getDynmap().updateRegion(game);
    }

    private void setStageProtect(Player player, Stage stage, List<String> args) throws CommandException, StageReservedException {
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

        stage.setProtected(protect);
        Actions.message(player, "&aステージ'" + stage.getName() + "'の保護は " + result + " &aに設定されました！");
        plugin.getDynmap().updateRegion(stage);
    }

    private void setStageAvailable(Player player, Stage stage, List<String> args) throws CommandException, StageReservedException {
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
        Actions.message(player, "&aステージ'" + stage.getName() + "'は使用" + result + "&aに設定されました！");
    }

    private void setKillScore(Player player, Stage stage, List<String> args) throws CommandException, StageReservedException {
        double point;
        try {
            point = Double.parseDouble(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("&cオプションの値が数ではありません！");
        }

        stage.setKillScore(point);

        Actions.message(player, "&aステージ'" + stage.getName() + "'のキル得点は " + point + "点 に設定されました！");
    }

    private void setDeathScore(Player player, Stage stage, List<String> args) throws CommandException, StageReservedException {
        double point;
        try {
            point = Double.parseDouble(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("&cオプションの値が数ではありません！");
        }

        stage.setDeathScore(point);

        Actions.message(player, "&aステージ'" + stage.getName() + "'のデス得点は " + point + "点 に設定されました！");
    }

    private void setPrize(Player player, Stage stage, List<String> args) throws CommandException, StageReservedException {
        double prize;
        try {
            prize = Double.parseDouble(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("&cオプションの値が整数ではありません！");
        }
        if (prize < 0) {
            throw new CommandException("&c値が不正です！負数は指定できません！");
        }

        stage.setPrize(prize);

        Actions.message(player, "&aステージ'" + stage.getName() + "'の賞金は " + Actions.formatMoney(prize) + " に設定されました！");
        plugin.getDynmap().updateRegion(stage);
    }

    private void setEntryFee(Player player, Stage stage, List<String> args) throws CommandException, StageReservedException {
        double entryFee;
        try {
            entryFee = Double.parseDouble(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("&cオプションの値が整数ではありません！");
        }
        if (entryFee < 0) {
            throw new CommandException("&c値が不正です！負数は指定できません！");
        }

        stage.setEntryFee(entryFee);

        Actions.message(player, "&aステージ'" + stage.getName() + "'の賞金は " + Actions.formatMoney(entryFee) + " に設定されました！");
        plugin.getDynmap().updateRegion(stage);
    }

    // stage description
    private void setDescription(Player player, Stage stage, List<String> args) throws CommandException, StageReservedException {
        stage.setDescription(args.get(1));
        Actions.message(player, "&aステージ'" + stage.getName() + "'の説明は '" + args.get(1) + "' に設定されました！");
    }

    private void setAuthor(Player player, Stage stage, List<String> args) throws CommandException, StageReservedException {
        stage.setAuthor(args.get(1));
        Actions.message(player, "&aステージ'" + stage.getName() + "'の製作者は '" + args.get(1) + "' に設定されました！");
    }

    private void setGuide(Player player, Stage stage, List<String> args) throws CommandException, StageReservedException {
        stage.setGuide(args.get(1));
        Actions.message(player, "&aステージ'" + stage.getName() + "'の概要は '" + args.get(1) + "' に設定されました！");
    }

    // shortcut
    private void setStage(Player player, Stage stage, List<String> args) throws CommandException, StageReservedException {
        Cuboid region;
        try {
            region = WorldEditHandler.getSelectedArea(player);
        } catch (IllegalStateException ex) {
            throw new CommandException("&c" + ex.getMessage());
        }
        stage.getAreas().setStageArea(region);
        AreaInfo info = stage.getAreas().getStageAreaInfo();
        AreaInfo.RollbackData rollback = info.addRollback("init");
        rollback.setTarget(StageDataType.CLASSIC.newInstance());
        sendMessage(player, "&a'&6" + stage.getName() + "&a'のステージエリアを設定しました！");
        sendMessage(player, "&a'&6" + stage.getName() + "&a'のステージエリアの'&6init&a'をセーブしました！");
    }

    private void setBase(Player player, Stage game, List<String> args) throws CommandException, StageReservedException {
        if (args.size() < 2) {
            throw new CommandException("&c引数が足りません！設定するチームを指定してください！");
        }
        TeamColor team;
        try {
            team = TeamColor.of(args.get(1));
        } catch (IllegalArgumentException ex) {
            throw new CommandException("&cチーム'" + args.get(1) + "'が見つかりません！");
        }
        String id = team.toString().toLowerCase().concat("-base");
        Cuboid area;
        try {
            area = WorldEditHandler.getSelectedArea(player);
        } catch (IllegalStateException ex) {
            throw new CommandException("&c" + ex.getMessage());
        }
        game.getAreas().setArea(id, area);
        AreaInfo info = game.getAreas().getAreaInfo(id);
        info.getPermission(GamePermission.DOOR).setState(team, GamePermissionState.ALLOW);
        info.getPermission(GamePermission.CONTAINER).setState(team, GamePermissionState.ALLOW);
        info.getPermission(GamePermission.GODMODE).setState(team, GamePermissionState.ALLOW);
        sendMessage(player, "&a" + team.getTeamName() + "&aチームの拠点を設定しました！");
        sendMessage(player, "&aステージ'&6" + game.getName() + "&a'のエリア'&6" + id + "&a'での権限'&6" + GamePermission.DOOR + "&a'を状態'" + GamePermissionState.ALLOW.format() + "&a'に変更しました！");
        sendMessage(player, "&aステージ'&6" + game.getName() + "&a'のエリア'&6" + id + "&a'での権限'&6" + GamePermission.CONTAINER + "&a'を状態'" + GamePermissionState.ALLOW.format() + "&a'に変更しました！");
        sendMessage(player, "&aステージ'&6" + game.getName() + "&a'のエリア'&6" + id + "&a'での権限'&6" + GamePermission.GODMODE + "&a'を状態'" + GamePermissionState.ALLOW.format() + "&a'に変更しました！");
        plugin.getDynmap().updateRegion(game);
    }

    /* ***** ここまで **************************************** */
    /**
     * 設定可能な設定とヘルプをsenderに送信する
     */
    private void sendAvailableConf(Player player) {
        List<String> col = new ArrayList<>();
        for (Configables conf : Configables.values()) {
            col.add(conf.name());
        }

        Actions.message(player, "&6 " + String.join("/", col).toLowerCase());
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return Perms.SET.has(target);
    }
}
