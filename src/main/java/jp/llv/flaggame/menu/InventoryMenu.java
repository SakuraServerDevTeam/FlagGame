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
package jp.llv.flaggame.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import jp.llv.flaggame.api.menu.Button;
import jp.llv.flaggame.api.menu.Menu;

/**
 *
 * @author toyblocks
 */
public final class InventoryMenu implements Menu {

    public static final int RAW_SIZE = 9;
    public static final int MAX_RAWS = 6;

    private final String title;
    private final List<Button> buttons;

    public InventoryMenu(String title, List<Button> buttons) {
        this.title = Objects.requireNonNull(title);
        if (buttons.size() % RAW_SIZE != 0) {
            IntStream.range(0, 9 - (buttons.size() % 9))
                    .forEach(i -> buttons.add(null));
        }
        this.buttons = new ArrayList<>(buttons);
        int raws = buttons.size() / RAW_SIZE;
        if (raws > MAX_RAWS) {
            throw new IllegalArgumentException("The number of buttons is not supported");
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Button getButton(int index) {
        return buttons.get(index);
    }

    @Override
    public int size() {
        return buttons.size();
    }

}
