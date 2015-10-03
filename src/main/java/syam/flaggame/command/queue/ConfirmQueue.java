/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.command.queue;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import syam.flaggame.FlagGame;

/**
 * ConfirmQueue (ConfirmQueue.java)
 * 
 * @author syam(syamn)
 */
public class ConfirmQueue {
    private final FlagGame plugin;
    private final List<QueuedCommand> queue;

    /**
     * コンストラクタ
     * 
     * @param plugin
     */
    public ConfirmQueue(final FlagGame plugin) {
        this.plugin = plugin;

        queue = new ArrayList<>();
    }

    /*
     * キューにコマンドを追加する
     */
    public void addQueue(CommandSender sender, Queueable queueable, List<String> args, int seconds) {
        cancelQueue(sender);
        this.queue.add(new QueuedCommand(sender, queueable, args, seconds));
    }

    /*
     * キューのコマンドを実行する
     * 
     * @param sender コマンド送信者
     */
    public boolean confirmQueue(CommandSender sender) {
        for (QueuedCommand cmd : this.queue) {
            if (cmd.getSender().equals(sender)) {
                cmd.execute();
                this.queue.remove(cmd);
                return true;
            }
        }
        return false;
    }

    /*
     * キューから指定したコマンド送信者のコマンドを削除する
     * 
     * @param sender
     *            CommandSender
     */
    public void cancelQueue(CommandSender sender) {
        QueuedCommand cmd = null;
        for (QueuedCommand check : this.queue) {
            if (check.getSender().equals(sender)) {
                cmd = check;
                break;
            }
        }
        if (cmd != null) {
            this.queue.remove(cmd);
        }
    }
}
