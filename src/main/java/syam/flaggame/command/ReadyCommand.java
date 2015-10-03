/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.command;

import syam.flaggame.event.GameReadyEvent;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Game;
import syam.flaggame.game.Stage;
import syam.flaggame.manager.GameManager;
import syam.flaggame.manager.StageManager;
import syam.flaggame.permission.Perms;

public class ReadyCommand extends BaseCommand {

    public ReadyCommand() {
        bePlayer = false;
        name = "ready";
        argLength = 1;
        usage = "<stage> <- ready game";
    }

    @Override
    public void execute() throws CommandException {
        // flag ready - ゲームを開始準備中にする
        if (args.isEmpty()) {
            throw new CommandException("&cステージ名を入力してください！");
        }

        Stage stage;
        boolean random = false;

        // ランダムゲーム
        if (args.get(0).equalsIgnoreCase("random")) {
            if (GameManager.getRandomGame() != null) {
                if (GameManager.getRandomGame().getState() == Game.State.ENTRY) {
                    throw new CommandException("&c現在、既にランダムステージが参加受付中です");
                } else {
                    GameManager.setRandomGame(null);
                }
            }

            stage = StageManager.getRandomAvailableStage();
            random = true;
        } // 通常のゲーム
        else {
            stage = StageManager.getStage(args.get(0));
        }

        if (stage == null) {
            throw new CommandException("&cステージ'" + args.get(0) + "'が見つかりません");
        }

        // ** ステージチェック **
        if (!stage.isAvailable()) {
            throw new CommandException("&cステージ'" + stage.getName() + "'は現在使えません");
        }

        if (stage.isUsing()) {
            if (stage.getGame() == null) {
                throw new CommandException("&cステージ'" + stage.getName() + "'は現在使用中です");
            } else {
                if (stage.getGame().getState() == Game.State.STARTED) {
                    throw new CommandException("&cこのゲームは既に始まっています");
                } else if (stage.getGame().getState() == Game.State.ENTRY) {
                    throw new CommandException("&cこのゲームは既に参加受付中です");
                }
            }
        }

        // ステージエリアチェック
        if (stage.getStage() == null) {
            throw new CommandException("&cステージエリアが正しく設定されていません");
        }

        // スポーン地点チェック
        if (stage.getSpawns().size() != 2) {
            throw new CommandException("&cチームスポーン地点が正しく設定されていません");
        }

        // Call event
        GameReadyEvent readyEvent = new GameReadyEvent(stage, sender, random);
        plugin.getServer().getPluginManager().callEvent(readyEvent);
        if (readyEvent.isCancelled()) {
            return;
        }

        // ready
        Game game = new Game(plugin, readyEvent.getStage(), readyEvent.isRandom());
        game.ready(sender);
    }

    @Override
    public boolean permission() {
        return Perms.READY.has(sender);
    }
}
