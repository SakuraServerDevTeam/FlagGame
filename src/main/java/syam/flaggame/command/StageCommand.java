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
import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        super(
                plugin,
                true,
                0,
                "<action> [stage] <- management stages",
                "stage"
        );
    
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        // サブ引数なし
        if (args.size() <= 0) {
            sendAvailableAction(player);
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
            sendAvailableAction(player);
            return;
        }

        // アクションによって処理を分ける
        switch (action) {
            case CREATE:
                if (checkPerm(player, Perms.CREATE)) {
                    create(args, player);
                }
                return;
            case DELETE:
                if (checkPerm(player, Perms.DELETE)) {
                    delete(args, player);
                }
                return;

            // 定義漏れ
            default:
                throw new CommandException("&アクションが不正です 開発者にご連絡ください");
        }
    }

    /* ***** ここから各アクション関数 ****************************** */
    private void create(List<String> args, Player player) throws CommandException {
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
        StageCreateEvent stageCreateEvent = new StageCreateEvent(player, stage);
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

        // update dynmap
        plugin.getDynmap().updateRegions();
        Actions.message(player, "&a新規ステージ'" + stage.getName() + "'を登録して選択しました！");
    }

    private void delete(List<String> args, Player player) throws CommandException {
        if (args.size() <= 1) {
            throw new CommandException("&cステージ名を入力してください！");
        }
        Stage stage = this.plugin.getStages().getStage(args.get(1))
                .orElseThrow(() -> new CommandException("&cその名前のステージは存在しません！"));

        if (stage.isReserved()) {
            throw new CommandException("&cそのステージは現在受付中または開始中のため削除できません");
        }

        // confirmキュー追加
        plugin.getConfirmQueue().addQueue(player, this, args, 10);
        Actions.message(player, "&dステージ'&7" + args.get(1) + "&d'を削除しようとしています！");
        Actions.message(player, "&d続行するには &a/flag confirm &dコマンドを入力してください！");
        Actions.message(player, "&a/flag confirm &dコマンドは10秒間のみ有効です。");
    }

    /* ***** ここまで ********************************************** */
 /*
     * キュー実行処理
     */
    @Override
    public void executeQueue(List<String> args) throws CommandException {
        if (StageAction.DELETE.name().equalsIgnoreCase(args.get(0))) {
            if (args.size() <= 1) {
                Actions.message(player, "&cステージ名が不正です");
                return;
            }
            Stage stage = this.plugin.getStages().getStage(args.get(1))
                    .orElseThrow(() -> new CommandException("&cその名前のステージは存在しません！"));

            if (stage.isReserved()) {
                Actions.message(player, "&cそのステージは現在受付中または開始中のため削除できません");
                return;
            }

            // ゲームリストから削除
            this.plugin.getStages().removeStage(args.get(1));
            Actions.message(player, "&aステージ'" + args.get(1) + "'を削除しました！");
            plugin.getDynmap().updateRegions();
        } else {
            throw new CommandException("&c内部エラーが発生しました。開発者までご連絡ください。");
        }
    }

    /**
     * アクションごとの権限をチェックする
     *
     * @param perm Perms
     * @return bool
     */
    private boolean checkPerm(Player player, Perms perm) {
        if (perm.has(player)) {
            return true;
        } else {
            Actions.message(player, "&cこのアクションを実行する権限がありません！");
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
    private void sendAvailableAction(Player player) {
        List<String> col = new ArrayList<>();
        for (StageAction action : StageAction.values()) {
            col.add(action.name());
        }
        Actions.message(player, "&cそのアクションは存在しません！");
        Actions.message(player, "&6 " + String.join("/", col).toLowerCase());
    }

    @Override
    public boolean hasPermission(Permissible target) {
        return (Perms.CREATE.has(target) || Perms.DELETE.has(target) || Perms.ROLLBACK.has(target));
    }
}
