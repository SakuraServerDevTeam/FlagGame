/*
 * Copyright (C) 2017 toyblocks
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
package jp.llv.flaggame.api.queue;

import java.util.List;
import jp.llv.flaggame.api.exception.FlagGameException;
import org.bukkit.command.CommandSender;

/**
 *
 * @author toyblocks
 */
public interface ConfirmQueueAPI {

    /*
     * キューにコマンドを追加する
     */
    void addQueue(CommandSender sender, Queueable queueable, List<String> args, int seconds);

    /*
     * キューから指定したコマンド送信者のコマンドを削除する
     *
     * @param sender
     *            CommandSender
     */
    void cancelQueue(CommandSender sender);

    /*
     * キューのコマンドを実行する
     *
     * @param sender コマンド送信者
     */
    boolean confirmQueue(CommandSender sender) throws FlagGameException;
    
}
