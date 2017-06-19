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

import java.util.Calendar;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;

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

    public void execute() throws FlagGameException {
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
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        this.queueable.executeQueue(args, sender, player); // 実行
    }

    public CommandSender getSender() {
        return this.sender;
    }
}
