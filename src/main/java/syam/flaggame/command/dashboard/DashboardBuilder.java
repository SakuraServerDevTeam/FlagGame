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
package syam.flaggame.command.dashboard;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class DashboardBuilder {

    private static final String PREFIX = "[FlagGame]";
    private static final String SEPARATE_LINE = "===";
    private static final char SPACE = ' ';
    private static final char SEPARATOR = ':';
    private static final char BUTTON_PREFIX = '[';
    private static final char BUTTON_SUFFIX = ']';
    private static final String CLICK = "click";

    private final String endline;
    private final TextBuilder<BaseComponent[]> text = TextBuilder.newBuilder();
    private boolean head = true;

    public DashboardBuilder(String name) {
        text.gold(PREFIX).text(SPACE).gray(SEPARATE_LINE).lightPurple(name).gray(SEPARATE_LINE).br();
        char lineChar = SEPARATE_LINE.charAt(0);
        int length = (SEPARATE_LINE.length() * 2) + name.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(lineChar);
        }
        this.endline = sb.toString();
    }

    public DashboardBuilder black(Object obj) {
        text.black(obj);
        return this;
    }

    public DashboardBuilder darkBlue(Object obj) {
        text.darkBlue(obj);
        return this;
    }

    public DashboardBuilder darkGreen(Object obj) {
        text.darkGreen(obj);
        return this;
    }

    public DashboardBuilder darkAqua(Object obj) {
        text.darkAqua(obj);
        return this;
    }

    public DashboardBuilder darkRed(Object obj) {
        text.darkRed(obj);
        return this;
    }

    public DashboardBuilder darkPurple(Object obj) {
        text.darkPurple(obj);
        return this;
    }

    public DashboardBuilder gold(Object obj) {
        text.gold(obj);
        return this;
    }

    public DashboardBuilder gray(Object obj) {
        text.gray(obj);
        return this;
    }

    public DashboardBuilder darkGray(Object obj) {
        text.darkGray(obj);
        return this;
    }

    public DashboardBuilder blue(Object obj) {
        text.blue(obj);
        return this;
    }

    public DashboardBuilder green(Object obj) {
        text.green(obj);
        return this;
    }

    public DashboardBuilder aqua(Object obj) {
        text.aqua(obj);
        return this;
    }

    public DashboardBuilder lightPurple(Object obj) {
        text.lightPurple(obj);
        return this;
    }

    public DashboardBuilder yellow(Object obj) {
        text.yellow(obj);
        return this;
    }

    public DashboardBuilder white(Object obj) {
        text.white(obj);
        return this;
    }

    public DashboardBuilder text(Object obj) {
        text.text(obj);
        return this;
    }

    public DashboardBuilder br() {
        text.br();
        head = true;
        return this;
    }
    
    public DashboardBuilder space() {
        text.text(SPACE);
        return this;
    }
    
    public DashboardBuilder kv(Object key, Object value) {
        if (!head) {
            space();
        }
        text.green(key).gray(SEPARATOR).text(SPACE).gold(value);
        return this;
    }
    
    public CommandBuilder<DashboardBuilder> buttonRun(String name) {
        text.lightPurple(BUTTON_PREFIX + name + BUTTON_SUFFIX).showing().gray(CLICK).create();
        return CommandBuilder.newBuilder(s -> {
            text.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s));
            return this;
        });
    }
    
    public CommandBuilder<DashboardBuilder> buttonSuggest(String name) {
        text.lightPurple(BUTTON_PREFIX + name + BUTTON_SUFFIX).showing().gray(CLICK).create();
        return CommandBuilder.newBuilder(s -> {
            text.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s));
            return this;
        });
    }
    
    public BaseComponent[] create() {
        return text.gold(PREFIX).text(SPACE).gray(endline).create();
    }
    
    public void sendTo(CommandSender target) {
        Actions.message(target, ChatMessageType.CHAT, create());
    }

}
