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
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import syam.flaggame.FlagGame;
import org.bukkit.permissions.Permissible;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

public abstract class BaseCommand {

    public static final String COMMAND_PREFIX = "/f ";
    
    /* コマンド関係 */
    // 初期化必要無し
    protected final FlagGame plugin;

    // プロパティ
    private final boolean bePlayer;
    private final String name;
    private final int argLength;
    private final String usage;
    private final Perms permission;
    private final String[] aliases;
    
    public BaseCommand(FlagGame plugin, boolean bePlayer, int argLength, String usage, Perms permission, String name, String ... aliases) {
        this.plugin = plugin;
        this.bePlayer = bePlayer;
        this.argLength = argLength;
        this.usage = usage;
        this.permission = permission;
        this.name = name;
        this.aliases = aliases;
    }
    
    public BaseCommand(FlagGame plugin, boolean bePlayer, int argLength, String usage, String name, String ... aliases) {
        this(plugin, bePlayer, argLength, usage, null, name, aliases);
    }
    
    public boolean run(CommandSender sender, String[] preArgs, String cmd) {
        List<String> args = new ArrayList<>(Arrays.asList(preArgs));

        // 引数からコマンドの部分を取り除く
        // (コマンド名に含まれる半角スペースをカウント、リストの先頭から順にループで取り除く)
        for (int i = 0; i < cmd.split(" ").length && i < args.size(); i++)
            args.remove(0);

        // 引数の長さチェック
        if (argLength > args.size()) {
            sendUsage(sender);
            return true;
        }

        // 実行にプレイヤーであることが必要かチェックする
        if (bePlayer && !(sender instanceof Player)) {
            Actions.message(sender, "&cThis command cannot run from Console!");
            return true;
        }
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        // 権限チェック
        if (!hasPermission(sender)) {
            Actions.message(sender, "&cYou don't have permission to use this!");
            return true;
        }

        // 実行
        try {
            execute(args, sender, player);
        } catch (CommandException ex) {
            Throwable error = ex;
            while (error instanceof CommandException) {
                Actions.message(sender, error.getMessage());
                error = error.getCause();
            }
        }
        return true;
    }

    /**
     * コマンドを実際に実行する
     * 
     * @param args arguments presented
     * @param sender the sender who executed this command
     * @param player the player who executed this command - equal to sender
     * @throws CommandException
     */
    public abstract void execute(List<String> args, CommandSender sender, Player player) throws CommandException;

    /**
     * コマンド実行に必要な権限を持っているか検証する
     * 
     * @param target target to check permission
     * @return trueなら権限あり、falseなら権限なし
     */
    public boolean hasPermission(Permissible target) {
        return permission == null ? true : permission.has(target);
    }

    public String getName() {
        return this.name;
    }

    public String getUsage() {
        return usage;
    }

    public String[] getAliases() {
        return aliases.clone();
    }

    /**
     * コマンドの使い方を送信する
     * @param sendTo target
     */
    public void sendUsage(CommandSender sendTo) {
        Actions.message(sendTo, "&c" + BaseCommand.COMMAND_PREFIX + " " + name + " " + usage);
    }
    
    public void sendMessage(CommandSender sender, String message) {
        Actions.sendPrefixedMessage(sender, message);
    }
    
}
