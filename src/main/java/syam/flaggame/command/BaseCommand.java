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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.util.Actions;

public abstract class BaseCommand {
    // Logger
    protected static final Logger log = FlagGame.logger;
    protected static final String logPrefix = FlagGame.logPrefix;
    protected static final String msgPrefix = FlagGame.msgPrefix;

    /* コマンド関係 */
    // 初期化必要無し
    protected final FlagGame plugin;
    protected CommandSender sender;
    protected String command;

    // 初期化必要
    protected Player player;
    protected List<String> args = new ArrayList<>();

    // プロパティ
    protected boolean bePlayer = true;
    protected String name;
    protected int argLength = 0;
    protected String usage;

    public BaseCommand(FlagGame plugin) {
        this.plugin = plugin;
    }
    
    public boolean run(CommandSender sender, String[] preArgs, String cmd) {
        this.sender = sender;
        this.command = cmd;

        // init
        init();

        // 引数をソート
        args.clear();
        args.addAll(Arrays.asList(preArgs));

        // 引数からコマンドの部分を取り除く
        // (コマンド名に含まれる半角スペースをカウント、リストの先頭から順にループで取り除く)
        for (int i = 0; i < name.split(" ").length && i < args.size(); i++)
            args.remove(0);

        // 引数の長さチェック
        if (argLength > args.size()) {
            sendUsage();
            return true;
        }

        // 実行にプレイヤーであることが必要かチェックする
        if (bePlayer && !(sender instanceof Player)) {
            Actions.message(sender, "&cThis command cannot run from Console!");
            return true;
        }
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        // 権限チェック
        if (!permission()) {
            Actions.message(sender, "&cYou don't have permission to use this!");
            return true;
        }

        // 実行
        try {
            execute();
        } catch (CommandException ex) {
            Throwable error = ex;
            while (error instanceof CommandException) {
                Actions.message(sender, error.getMessage());
                error = error.getCause();
            }
        }
        return true;
    }

    // init commands
    private void init() {
        this.args.clear();
        player = null;
    }

    /**
     * コマンドを実際に実行する
     * 
     * @throws CommandException
     */
    public abstract void execute() throws CommandException;

    /**
     * コマンド実行に必要な権限を持っているか検証する
     * 
     * @return trueなら権限あり、falseなら権限なし
     */
    public abstract boolean permission();

    public String getName() {
        return this.name;
    }

    /**
     * コマンドの使い方を送信する
     */
    public void sendUsage() {
        Actions.message(sender, "&c/" + this.command + " " + name + " " + usage);
    }
}
