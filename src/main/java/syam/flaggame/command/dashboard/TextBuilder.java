/*
 * Copyright 2016 toyblocks.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package syam.flaggame.command.dashboard;

import java.util.Objects;
import java.util.function.Function;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 *
 * @author toyblocks
 * @param <R> callback
 */
public class TextBuilder<R> {

    private final Function<BaseComponent[], R> callback;
    private ComponentBuilder builder;

    private TextBuilder(Function<BaseComponent[], R> function) {
        Objects.requireNonNull(function);
        this.callback = function;
    }

    public TextBuilder<R> black(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.BLACK);
        return this;
    }

    public TextBuilder<R> darkBlue(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.DARK_BLUE);
        return this;
    }

    public TextBuilder<R> darkGreen(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.DARK_GREEN);
        return this;
    }

    public TextBuilder<R> darkAqua(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.DARK_AQUA);
        return this;
    }

    public TextBuilder<R> darkRed(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.DARK_RED);
        return this;
    }

    public TextBuilder<R> darkPurple(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.DARK_PURPLE);
        return this;
    }

    public TextBuilder<R> gold(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.GOLD);
        return this;
    }

    public TextBuilder<R> gray(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.GRAY);
        return this;
    }

    public TextBuilder<R> darkGray(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.DARK_GRAY);
        return this;
    }

    public TextBuilder<R> blue(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.BLUE);
        return this;
    }

    public TextBuilder<R> green(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.GREEN);
        return this;
    }

    public TextBuilder<R> aqua(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.AQUA);
        return this;
    }

    public TextBuilder<R> red(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.RED);
        return this;
    }

    public TextBuilder<R> lightPurple(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.LIGHT_PURPLE);
        return this;
    }

    public TextBuilder<R> yellow(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.YELLOW);
        return this;
    }

    public TextBuilder<R> white(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(ChatColor.WHITE);
        return this;
    }

    public TextBuilder<R> obfuscated() {
        this.builder.obfuscated(true);
        return this;
    }

    public TextBuilder<R> bold() {
        this.builder.bold(true);
        return this;
    }

    public TextBuilder<R> strikethrough() {
        this.builder.strikethrough(true);
        return this;
    }

    public TextBuilder<R> underlined() {
        this.builder.underlined(true);
        return this;
    }

    public TextBuilder<R> italic() {
        this.builder.italic(true);
        return this;
    }

    public TextBuilder<R> text(Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE));
        return this;
    }

    public TextBuilder<R> text(ChatColor color, Object obj) {
        this.builder = (this.builder == null ? new ComponentBuilder(Objects.toString(obj)) : this.builder.append(Objects.toString(obj), ComponentBuilder.FormatRetention.NONE)).color(color);
        return this;
    }

    public R create() {
        return this.callback.apply(this.builder == null ? new BaseComponent[0] : this.builder.create());
    }

    public TextBuilder<R> br() {
        this.builder = (this.builder == null ? new ComponentBuilder("\n") : this.builder.append("\n", ComponentBuilder.FormatRetention.NONE));
        return this;
    }

    public CommandBuilder<TextBuilder<R>> running(String command) {
        return CommandBuilder.newBuilder(s -> {
            builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s));
            return this;
        });
    }

    public CommandBuilder<TextBuilder<R>> suggesting(String command) {
        return CommandBuilder.newBuilder(s -> {
            builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s));
            return this;
        });
    }

    public TextBuilder<R> opening(String url) {
        builder.event(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        return this;
    }

    public TextBuilder<TextBuilder<R>> showing() {
        return TextBuilder.newBuilder(s -> {
            builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, s));
            return this;
        });
    }

    public TextBuilder<R> event(ClickEvent clickEvent) {
        builder.event(clickEvent);
        return this;
    }

    public TextBuilder<R> event(HoverEvent hoverEvent) {
        builder.event(hoverEvent);
        return this;
    }

    public static TextBuilder<BaseComponent[]> newBuilder() {
        return new TextBuilder<>(Function.identity());
    }

    public static <R> TextBuilder<R> newBuilder(Function<BaseComponent[], R> callback) {
        return new TextBuilder<>(callback);
    }

}
