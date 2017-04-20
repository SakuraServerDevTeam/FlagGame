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
package jp.llv.flaggame.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 * One line message builder.
 *
 * @author SakuraServerDev
 */
public class OnelineBuilder {

    private static final char QUOTE = '\'';
    private static final char SPACE = ' ';
    private static final char BUTTON_PREFIX = '[';
    private static final char BUTTON_SUFFIX = ']';
    private static final String CLICK = "click";

    private final TextBuilder<BaseComponent[]> text = TextBuilder.newBuilder();
    private boolean inValue = false;

    private OnelineBuilder() {
    }

    public OnelineBuilder info(Object obj) {
        if (inValue) {
            text.gray(QUOTE);
            inValue = false;
        }
        text.green(obj);
        return this;
    }

    public OnelineBuilder warn(Object obj) {
        if (inValue) {
            text.gray(QUOTE);
            inValue = false;
        }
        text.red(obj);
        return this;
    }

    public OnelineBuilder value(Object obj) {
        if (!inValue) {
            text.gray(QUOTE);
            inValue = true;
        }
        text.gold(obj);
        return this;
    }

    public OnelineBuilder text(ChatColor color, Object obj) {
        text.text(color, obj);
        return this;
    }

    public OnelineBuilder space() {
        text.text(SPACE);
        return this;
    }

    public CommandBuilder<OnelineBuilder> buttonRun(String name) {
        if (inValue) {
            text.gray(QUOTE);
            inValue = false;
        }
        space();
        text.lightPurple(BUTTON_PREFIX + name + BUTTON_SUFFIX).showing().gray(CLICK).create();
        return CommandBuilder.newBuilder(s -> {
            text.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s));
            return this;
        });
    }
    
    public OnelineBuilder buttonTp(String name, Player player, Location loc) {
        return buttonRun("tp").append("tp").append(player.getName())
                .append(loc.getX()).append(loc.getY()).append(loc.getZ())
                .append(loc.getYaw()).append(loc.getPitch())
                .append(loc.getWorld().getName()).create();
    }

    public CommandBuilder<OnelineBuilder> buttonSuggest(String name) {
        if (inValue) {
            text.gray(QUOTE);
            inValue = false;
        }
        space();
        text.lightPurple(BUTTON_PREFIX + name + BUTTON_SUFFIX).showing().gray(CLICK).create();
        return CommandBuilder.newBuilder(s -> {
            text.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s + SPACE));
            return this;
        });
    }

    public BaseComponent[] create() {
        if (inValue) {
            text.gray(QUOTE);
        }
        return text.create();
    }

    public void sendTo(CommandSender... targets) {
        for (CommandSender target : targets) {
            Actions.sendPrefixedMessage(target, ChatMessageType.CHAT, create());
        }
    }

    public void sendTo(Iterable<GamePlayer> players) {
        GamePlayer.sendMessage(players, create());
    }

    public static OnelineBuilder newBuilder() {
        return new OnelineBuilder();
    }

}
