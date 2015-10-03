/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.command.queue;

import java.util.Calendar;
import java.util.List;

import org.bukkit.command.CommandSender;

import syam.flaggame.util.Actions;

/**
 * QueuedCommand (QueuedCommand.java)
 * 
 * @author syam(syamn)
 */
public class QueuedCommand {
    private final CommandSender sender;
    private final Queueable queueable;
    private final List<String> args;
    private final int timeoutSec;
    private final Calendar requestDate;
    private boolean done = false;

    public QueuedCommand(CommandSender sender, Queueable queueable, List<String> args, int timeoutSec) {
        this.sender = sender;
        this.queueable = queueable;
        this.args = args;
        this.timeoutSec = timeoutSec;

        this.requestDate = Calendar.getInstance();
    }

    public void execute() {
        // タイムアウトチェック
        this.requestDate.add(13, this.timeoutSec);
        if (!this.requestDate.after(Calendar.getInstance())) {
            Actions.message(sender, "&cこのコマンドは時間切れです！元のコマンドをもう一度入力してください！");
            return;
        }

        // 多重進入防止
        if (done) {
            Actions.message(sender, "&cこのコマンドは既に実行されています！再度実行するには元のコマンドから入力し直してください！");
            return;
        }
        done = true;
        this.queueable.executeQueue(this.args); // 実行
    }

    public CommandSender getSender() {
        return this.sender;
    }
}
