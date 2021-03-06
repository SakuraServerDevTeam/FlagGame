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
package syam.flaggame.queue;

import jp.llv.flaggame.api.queue.Queueable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.queue.ConfirmQueueAPI;

/**
 * ConfirmQueue (ConfirmQueue.java)
 *
 * @author syam(syamn)
 */
public class ConfirmQueue implements ConfirmQueueAPI {

    private final List<QueuedCommand> queue;

    /**
     * コンストラクタ
     */
    public ConfirmQueue() {
        queue = new ArrayList<>();
    }

    /*
     * キューにコマンドを追加する
     */
    @Override
    public void addQueue(CommandSender sender, Queueable queueable, List<String> args, int seconds) {
        cancelQueue(sender);
        this.queue.add(new QueuedCommand(sender, queueable, args, seconds));
    }

    /*
     * キューのコマンドを実行する
     * 
     * @param sender コマンド送信者
     */
    @Override
    public boolean confirmQueue(CommandSender sender) throws FlagGameException {
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
    @Override
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
