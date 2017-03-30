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
package syam.flaggame.util;

import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import syam.flaggame.FlagGame;

public class Actions {

    // Logger
    public static final String MESSAGE_PREFIX = "&6[FlagGame] &r";

    // メッセージ送信系関数
    /* メッセージをユニキャスト
     * 
     * @param sender
     *            Sender (null可)
     * @param player
     *            Player (null可)l
     * @param message
     *            メッセージ
     */
    public static void message(CommandSender sender, String message) {
        if (message != null && sender != null) {
            message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
            sender.sendMessage(message);
        }
    }

    public static void message(CommandSender sender, ChatMessageType type, BaseComponent... message) {
        if (message != null && sender != null) {
            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(type, message);
            } else {
                sender.sendMessage(BaseComponent.toLegacyText(message));
            }
        }
    }

    public static void message(CommandSender sender, ChatMessageType type, String message) {
        if (message != null && sender != null) {
            message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(type, TextComponent.fromLegacyText(message));
            } else {
                sender.sendMessage(message);
            }
        }
    }

    public static void sendPrefixedMessage(CommandSender sender, String message) {
        message(sender, MESSAGE_PREFIX + "\u00A7r" + message);
    }

    public static void sendPrefixedMessage(CommandSender sender, ChatMessageType type, BaseComponent... message) {
        if (type == ChatMessageType.CHAT) {
            BaseComponent[] prefix = TextComponent.fromLegacyText(MESSAGE_PREFIX.replaceAll("&([0-9a-fk-or])", "\u00A7$1"));
            BaseComponent[] concat = new BaseComponent[prefix.length + message.length];
            System.arraycopy(prefix, 0, concat, 0, prefix.length);
            System.arraycopy(message, 0, concat, prefix.length, message.length);
            message(sender, type, concat);
        } else {
            message(sender, type, message);
        }
    }

    public static void sendPrefixedMessage(CommandSender sender, ChatMessageType type, String message) {
        sendPrefixedMessage(sender, type, new TextComponent(message.replaceAll("&([0-9a-fk-or])", "\u00A7$1")));
    }

    public static void sendTitle(Player player, String title, String subTitle, int in, int stay, int out) {
        player.sendTitle(
                title.replaceAll("&([0-9a-fk-or])", "\u00A7$1"),
                subTitle.replaceAll("&([0-9a-fk-or])", "\u00A7$1"),
                in,
                stay,
                out
        );
    }

    // ユーティリティ
    public static String getBlockLocationString(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    /**
     * 引数の秒を読みやすい形式の文字列に変換して返す
     *
     * @param sec 正の秒数
     * @return 変換後の文字列
     */
    public static String getTimeString(int sec) {
        // 負数は許容しない
        if (sec < 0) {
            return "0秒";
        }

        // 60秒以下はそのまま返す
        if (sec < 60) {
            return sec + "秒";
        }

        // 60秒で割り切れれば分だけを返す
        if (sec % 60 == 0) {
            return sec / 60 + "分";
        }

        // 当て嵌まらなければ n分n秒 で返す
        int m = sec / 60; // 小数点以下は切られるのでこれで問題ないはず..
        int s = sec % 60;
        return m + "分" + s + "秒";
    }

    public static String getTimeString(long sec) {
        return getTimeString(((int) sec / 1000));
    }

    /**
     * *************************************
     */
    // 所持金操作系関数 - Vault
    /**
     * *************************************
     */
    /**
     * 指定したユーザーにお金を加える
     *
     * @param uuid ユーザーのuuid
     * @param amount 金額
     * @return 成功ならtrue、失敗ならfalse
     */
    public static boolean addMoney(UUID uuid, double amount) {
        if (amount < 0) {
            return false; // 負数は許容しない
        }
        return FlagGame.getInstance().getEconomy()
                .map(e -> e.depositPlayer(Bukkit.getOfflinePlayer(uuid), amount).transactionSuccess()).orElse(false);
    }

    /**
     * 指定したユーザーからお金を引く
     *
     * @param uuid ユーザーのuuid
     * @param amount 金額
     * @return 成功ならtrue、失敗ならfalse
     */
    public static boolean takeMoney(UUID uuid, double amount) {
        if (amount < 0) {
            return false; // 負数は許容しない
        }
        return FlagGame.getInstance().getEconomy()
                .map(e -> e.withdrawPlayer(Bukkit.getOfflinePlayer(uuid), amount).transactionSuccess()).orElse(false);
    }

    /**
     * 指定したユーザーがお金を持っているか
     *
     * @param uuid ユーザーのuuid
     * @param amount 金額
     * @return 持っていればtrue、無ければfalse
     */
    public static boolean checkMoney(UUID uuid, double amount) {
        return FlagGame.getInstance().getEconomy()
                .map(e -> e.has(Bukkit.getOfflinePlayer(uuid), amount)).orElse(false);
    }
}
