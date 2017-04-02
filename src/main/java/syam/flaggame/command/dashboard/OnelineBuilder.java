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
import org.bukkit.command.CommandSender;
import syam.flaggame.util.Actions;

/**
 * One line message builder.
 *
 * @author SakuraServerDev
 */
public class OnelineBuilder {
    
    private static final char QUOTE = '\'';

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
    
    public BaseComponent[] create() {
        if (inValue) {
            text.gray(QUOTE);
        }
        return text.create();
    }
    
    public void sendTo(CommandSender target) {
        Actions.sendPrefixedMessage(target, ChatMessageType.CHAT, create());
    }

    public static OnelineBuilder newBuilder() {
        return new OnelineBuilder();
    }

}
