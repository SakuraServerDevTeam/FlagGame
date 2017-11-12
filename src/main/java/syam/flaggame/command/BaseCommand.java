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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.AccountNotReadyException;
import jp.llv.flaggame.util.FlagTabCompleter;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.permissions.Permissible;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.exception.PermissionException;
import jp.llv.flaggame.api.exception.ReservedException;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

public abstract class BaseCommand {

    public static final String COMMAND_PREFIX = "/flag ";

    /* コマンド関係 */
    // 初期化必要無し
    protected final FlagGameAPI api;

    // プロパティ
    private final boolean bePlayer;
    private final String name;
    private final int argLength;
    private final String usage;
    private final Perms permission;
    private final FlagTabCompleter completer;
    private final String[] aliases;

    public BaseCommand(FlagGameAPI plugin, boolean bePlayer, int argLength, String usage, Perms permission, FlagTabCompleter completer, String name, String... aliases) {
        this.api = Objects.requireNonNull(plugin);
        this.bePlayer = bePlayer;
        this.argLength = argLength;
        this.usage = Objects.requireNonNull(usage);
        this.permission = permission;
        this.completer = completer;
        this.name = Objects.requireNonNull(name);
        this.aliases = Objects.requireNonNull(aliases);
    }

    public BaseCommand(FlagGameAPI plugin, boolean bePlayer, int argLength, String usage, FlagTabCompleter completer, String name, String... aliases) {
        this(plugin, bePlayer, argLength, usage, null, completer, name, aliases);
    }

    public BaseCommand(FlagGameAPI plugin, boolean bePlayer, int argLength, String usage, Perms permission, String name, String... aliases) {
        this(plugin, bePlayer, argLength, usage, permission, null, name, aliases);
    }

    public BaseCommand(FlagGameAPI plugin, boolean bePlayer, int argLength, String usage, String name, String... aliases) {
        this(plugin, bePlayer, argLength, usage, null, null, name, aliases);
    }

    public final boolean run(CommandSender sender, String[] preArgs, String cmd) {
        List<String> args = new ArrayList<>(Arrays.asList(preArgs));

        // 引数の長さチェック
        if (argLength > args.size()) {
            sendUsage(sender, cmd.substring(0, cmd.length() - name.length() - 1));
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
            execute(args, cmd, sender, player);
        } catch (PermissionException ex) {
            Actions.message(sender, "&cYou don't have permission to use this!");
        } catch (CommandException ex) {
            Actions.message(sender, ex.getMessage());
        } catch (ReservedException ex) {
            Actions.message(sender, "&cそのステージは'" + ex.getReservable().getReserver().getName() + "'に占有されています！");
        } catch (AccountNotReadyException ex) {
            Actions.message(sender, "&cこの操作を完了するにはアカウントのロードを待つ必要があります！");
        } catch (FlagGameException ex) {
            Actions.message(sender, "&cAn unhandled plugin error has occured.");
            api.getLogger().warn("Failed to handle command", ex);
        } catch (Exception ex) {
            Actions.message(sender, "&cAn unexpected plugin error has occured.");
            api.getLogger().warn("Failed to handle command", ex);
        }
        return true;
    }

    public final List<String> complete(CommandSender sender, String[] preArgs, String cmd) {
        List<String> args = new ArrayList<>(Arrays.asList(preArgs));
        if ((bePlayer && !(sender instanceof Player))
                || !hasPermission(sender)) {
            return Collections.emptyList();
        }
        try {
            Collection<String> result;
            if (sender instanceof Player) {
                result = complete(args, sender, (Player) sender);
            } else {
                result = complete(args, sender, null);
            }
            if (result instanceof List) {
                return (List<String>) result;
            } else {
                return new ArrayList<>(result);
            }
        } catch (FlagGameException ex) {
        } catch (Exception ex) {
            Actions.message(sender, "&cAn unexpected plugin error has occured.");
            api.getLogger().warn("Failed to complete command", ex);
        }
        return Collections.emptyList();
    }

    protected Collection<String> complete(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        return completer == null ? Collections.emptyList() : completer.complete(api, args, sender);
    }

    protected void execute(List<String> args, String label, CommandSender sender, Player player) throws FlagGameException {
        this.execute(args, sender, player);
    }

    /**
     * コマンドを実際に実行する
     *
     * @param args arguments presented
     * @param sender the sender who executed this command
     * @param player the player who executed this command - equal to sender
     * @throws CommandException when this command fails handling
     */
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        throw new CommandException("&cThis command is not implemented yet.");
    }

    /**
     * コマンド実行に必要な権限を持っているか検証する
     *
     * @param target target to check permission
     * @return trueなら権限あり、falseなら権限なし
     */
    public final boolean hasPermission(Permissible target) {
        return permission == null ? true : permission.has(target);
    }

    public final String getName() {
        return this.name;
    }

    public final String getUsage() {
        return usage;
    }

    public final String[] getAliases() {
        return aliases.clone();
    }

    /**
     * コマンドの使い方を送信する
     *
     * @param sendTo target
     * @param label command arguments before this command name
     */
    public final void sendUsage(CommandSender sendTo, String label) {
        Actions.message(sendTo, "&7/" + label + "&c " + name + "&7 " + usage);
    }

    protected final void sendMessage(CommandSender sender, String message) {
        Actions.sendPrefixedMessage(sender, message);
    }

}
