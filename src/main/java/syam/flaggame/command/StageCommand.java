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
package syam.flaggame.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import jp.llv.flaggame.database.DatabaseException;
import syam.flaggame.FlagGame;

import syam.flaggame.command.queue.Queueable;
import syam.flaggame.event.StageCreateEvent;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.StageReservedException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 * StageCommand (StageCommand.java)
 *
 * @author syam(syamn)
 */
public class StageCommand extends BaseCommand implements Queueable {

    public StageCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = true;
        name = "stage";
        argLength = 0;
        usage = "<action> [stage] <- management stages";
    }

    @Override
    public void execute() throws CommandException {
        // サブ引数なし
        if (args.size() <= 0) {
            sendAvailableAction();
            return;
        }

        // アクション取得
        StageAction action = null;
        for (StageAction check : StageAction.values()) {
            if (check.name().equalsIgnoreCase(args.get(0))) {
                action = check;
                break;
            }
        }
        if (action == null) {
            sendAvailableAction();
            return;
        }

        // アクションによって処理を分ける
        switch (action) {
            case CREATE:
                if (checkPerm(Perms.CREATE)) {
                    create();
                }
                return;
            case DELETE:
                if (checkPerm(Perms.DELETE)) {
                    delete();
                }
                return;

            // 定義漏れ
            default:
                Actions.message(sender, "&アクションが不正です 開発者にご連絡ください");
                log.log(Level.WARNING, logPrefix + "Undefined action: {0}! Please report this!", action.name());
                break;
        }
    }

    /* ***** ここから各アクション関数 ****************************** */
    private void create() throws CommandException {
        if (args.size() <= 1) {
            throw new CommandException("&cステージ名を指定してください！");
        }

        if (!Stage.NAME_REGEX.matcher(args.get(1)).matches()) {
            throw new CommandException("&cこのステージ名は使用できません！");
        }

        if (this.plugin.getStages().getStage(args.get(1)).isPresent()) {
            throw new CommandException("&cそのステージ名は既に存在します！");
        }

        // Call event
        Stage stage = new Stage(args.get(1));
        StageCreateEvent stageCreateEvent = new StageCreateEvent(sender, stage);
        plugin.getServer().getPluginManager().callEvent(stageCreateEvent);
        if (stageCreateEvent.isCancelled()) {
            return;
        }

        // 新規ゲーム登録
        try {
            stage.setAvailable(false);
            stage.setProtected(false);
        } catch (StageReservedException ex) {
            throw new RuntimeException("Is illegal opperation executed? Please report this?");
        }
        this.plugin.getStages().addStage(args.get(1), stage);

        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(stage);

        // update dynmap, save stage
        plugin.getDynmap().updateRegions();
        try {
            plugin.getStages().saveStages();
        } catch (DatabaseException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to connect database!", ex);
            throw new CommandException("&cデータベースへの保存に失敗しました！");
        }

        Actions.message(sender, "&a新規ステージ'" + stage.getName() + "'を登録して選択しました！");
    }

    private void delete() throws CommandException {
        if (args.size() <= 1) {
            throw new CommandException("&cステージ名を入力してください！");
        }
        Stage stage = this.plugin.getStages().getStage(args.get(1))
                .orElseThrow(() -> new CommandException("&cその名前のステージは存在しません！"));

        if (stage.isReserved()) {
            throw new CommandException("&cそのステージは現在受付中または開始中のため削除できません");
        }

        // confirmキュー追加
        plugin.getConfirmQueue().addQueue(sender, this, args, 10);
        Actions.message(sender, "&dステージ'&7" + args.get(1) + "&d'を削除しようとしています！");
        Actions.message(sender, "&d続行するには &a/flag confirm &dコマンドを入力してください！");
        Actions.message(sender, "&a/flag confirm &dコマンドは10秒間のみ有効です。");
    }

    /* ***** ここまで ********************************************** */
 /*
     * キュー実行処理
     */
    @Override
    public void executeQueue(List<String> args) throws CommandException {
        if (StageAction.DELETE.name().equalsIgnoreCase(args.get(0))) {
            if (args.size() <= 1) {
                Actions.message(sender, "&cステージ名が不正です");
                return;
            }
            Stage stage = this.plugin.getStages().getStage(args.get(1))
                    .orElseThrow(() -> new CommandException("&cその名前のステージは存在しません！"));

            if (stage.isReserved()) {
                Actions.message(sender, "&cそのステージは現在受付中または開始中のため削除できません");
                return;
            }

            // ゲームリストから削除
            this.plugin.getStages().removeStage(args.get(0));

            // ゲームデータファイルを削除
            String fileDir = plugin.getDataFolder() + System.getProperty("file.separator") + "stageData";
            boolean deleted = false;
            try {
                File file = new File(fileDir + System.getProperty("file.separator") + stage.getFileName());
                if (file.exists()) {
                    deleted = file.delete();
                }
            } catch (Exception ex) {
                deleted = false;
                ex.printStackTrace();
            }

            if (!deleted) {
                Actions.message(sender, "&cステージ'" + args.get(1) + "'のデータファイル削除中にエラーが発生しました！");
            } else {
                Actions.message(sender, "&aステージ'" + args.get(1) + "'を削除しました！");
                plugin.getDynmap().updateRegions();
            }
        } else {
            Actions.message(sender, "&c内部エラーが発生しました。開発者までご連絡ください。");
            log.log(Level.WARNING, logPrefix + "{0} send invalid queue! (StageCommand.class)", sender.getName());
        }
    }

    /**
     * アクションごとの権限をチェックする
     *
     * @param perm Perms
     * @return bool
     */
    private boolean checkPerm(Perms perm) {
        if (perm.has(sender)) {
            return true;
        } else {
            Actions.message(sender, "&cこのアクションを実行する権限がありません！");
            return false;
        }
    }

    /**
     * 指定可能なステージアクション stageAction (StageCommand.java)
     *
     * @author syam(syamn)
     */
    enum StageAction {

        CREATE, DELETE,;
    }

    /**
     * 指定可能なアクションをsenderに送信する
     */
    private void sendAvailableAction() {
        List<String> col = new ArrayList<>();
        for (StageAction action : StageAction.values()) {
            col.add(action.name());
        }
        Actions.message(sender, "&cそのアクションは存在しません！");
        Actions.message(sender, "&6 " + String.join("/", col).toLowerCase());
    }

    @Override
    public boolean permission() {
        return (Perms.CREATE.has(sender) || Perms.DELETE.has(sender) || Perms.ROLLBACK.has(sender));
    }
}
