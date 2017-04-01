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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.md_5.bungee.api.ChatColor;
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
    private static final char BRACKET_LEFT = '(';
    private static final char BRACKET_RIGHT = ')';
    private static final String CLICK = "click";

    private final String endline;
    private final TextBuilder<BaseComponent[]> text = TextBuilder.newBuilder();
    private boolean head = true;

    private DashboardBuilder(Object name, Object subtitle) {
        text.gold(PREFIX).text(SPACE).gray(SEPARATE_LINE).gold(name);
        if (subtitle != null) {
            text.gray(BRACKET_LEFT).gray(subtitle).gray(BRACKET_RIGHT);
        }
        text.gray(SEPARATE_LINE).br();
        char lineChar = SEPARATE_LINE.charAt(0);
        int length = (SEPARATE_LINE.length() * 2)
                     + name.toString().length()
                     + (subtitle == null ? 0 : (subtitle.toString().length() + 1));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(lineChar);
        }
        this.endline = sb.toString();
    }

    public DashboardBuilder black(Object obj) {
        head = false;
        text.black(obj);
        return this;
    }

    public DashboardBuilder darkBlue(Object obj) {
        head = false;
        text.darkBlue(obj);
        return this;
    }

    public DashboardBuilder darkGreen(Object obj) {
        head = false;
        text.darkGreen(obj);
        return this;
    }

    public DashboardBuilder darkAqua(Object obj) {
        head = false;
        text.darkAqua(obj);
        return this;
    }

    public DashboardBuilder darkRed(Object obj) {
        head = false;
        text.darkRed(obj);
        return this;
    }

    public DashboardBuilder darkPurple(Object obj) {
        head = false;
        text.darkPurple(obj);
        return this;
    }

    public DashboardBuilder gold(Object obj) {
        head = false;
        text.gold(obj);
        return this;
    }

    public DashboardBuilder gray(Object obj) {
        head = false;
        text.gray(obj);
        return this;
    }

    public DashboardBuilder darkGray(Object obj) {
        head = false;
        text.darkGray(obj);
        return this;
    }

    public DashboardBuilder blue(Object obj) {
        head = false;
        text.blue(obj);
        return this;
    }

    public DashboardBuilder green(Object obj) {
        head = false;
        text.green(obj);
        return this;
    }

    public DashboardBuilder aqua(Object obj) {
        head = false;
        text.aqua(obj);
        return this;
    }

    public DashboardBuilder lightPurple(Object obj) {
        head = false;
        text.lightPurple(obj);
        return this;
    }

    public DashboardBuilder yellow(Object obj) {
        head = false;
        text.yellow(obj);
        return this;
    }

    public DashboardBuilder white(Object obj) {
        head = false;
        text.white(obj);
        return this;
    }

    public DashboardBuilder text(Object obj) {
        head = false;
        text.text(obj);
        return this;
    }

    public DashboardBuilder text(ChatColor color, Object obj) {
        text.text(color, obj);
        return this;
    }

    public DashboardBuilder br() {
        text.br();
        head = true;
        return this;
    }

    public DashboardBuilder space() {
        head = false;
        text.text(SPACE);
        return this;
    }

    public DashboardBuilder key(Object key) {
        if (!head) {
            space();
        }
        return green(key).gray(SEPARATOR).space();
    }

    public DashboardBuilder value(Object value) {
        return gold(value);
    }

    public CommandBuilder<DashboardBuilder> buttonRun(String name) {
        if (!head) {
            space();
        }
        head = false;
        text.lightPurple(BUTTON_PREFIX + name + BUTTON_SUFFIX).showing().gray(CLICK).create();
        return CommandBuilder.newBuilder(s -> {
            text.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s));
            return this;
        });
    }

    public CommandBuilder<DashboardBuilder> buttonSuggest(String name) {
        if (!head) {
            space();
        }
        head = false;
        text.lightPurple(BUTTON_PREFIX + name + BUTTON_SUFFIX).showing().gray(CLICK).create();
        return CommandBuilder.newBuilder(s -> {
            text.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s + SPACE));
            return this;
        });
    }

    public <T> DashboardBuilder appendIndexedList(List<? extends T> contents,
                                                  BiConsumer<DashboardBuilder, ? super T> formatter) {
        if (!head) {
            br();
        }
        for (int i = 0; i < contents.size(); i++) {
            text.gray(i).gray(SEPARATOR).text(SPACE);
            formatter.accept(this, contents.get(i));
            br();
        }
        return this;
    }

    public <T> DashboardBuilder appendList(Collection<? extends T> contents,
                                           BiConsumer<DashboardBuilder, ? super T> formatter) {
        if (!head) {
            br();
        }
        for (T element : contents) {
            formatter.accept(this, element);
            br();
        }
        return this;
    }

    public <K, V> DashboardBuilder appendMap(Map<? extends K, ? extends V> contents,
                                             BiConsumer<DashboardBuilder, ? super K> keyFormatter,
                                             BiConsumer<DashboardBuilder, ? super V> valueFormatter) {
        if (!head) {
            br();
        }
        for (Map.Entry<? extends K, ? extends V> entry : contents.entrySet()) {
            keyFormatter.accept(this, entry.getKey());
            text.gray(SEPARATOR).text(SPACE);
            valueFormatter.accept(this, entry.getValue());
            text.br();
        }
        return this;
    }

    public <K extends CharSequence, V> DashboardBuilder appendMap(Map<? extends K, ? extends V> contents,
                                                                  BiConsumer<DashboardBuilder, ? super V> valueFormatter) {
        return appendMap(contents, (t, k) -> t.green(k), valueFormatter);
    }

    public <K extends CharSequence, V> DashboardBuilder appendEnumMap(Map<? extends K, ? extends V> contents,
                                                                      BiConsumer<DashboardBuilder, ? super V> valueFormatter) {
        return appendMap(contents, (t, k) -> t.green(k.toString().toLowerCase()), valueFormatter);
    }

    public BaseComponent[] create() {
        if (!head) {
            br();
        }
        return text.gold(PREFIX).text(SPACE).gray(endline).create();
    }

    public void sendTo(CommandSender target) {
        Actions.message(target, ChatMessageType.CHAT, create());
    }

    public static DashboardBuilder newBuilder(Object name) {
        return new DashboardBuilder(name, null);
    }

    public static DashboardBuilder newBuilder(Object name, Object subtitle) {
        return new DashboardBuilder(name, subtitle);
    }

}
