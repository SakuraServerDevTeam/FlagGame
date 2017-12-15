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
import jp.llv.flaggame.api.menu.Button;
import jp.llv.flaggame.api.menu.Menu;
import jp.llv.flaggame.api.menu.PageableMenu;
import org.bukkit.Material;

/**
 * A menu builder which supports pagenation.
 *
 * @author toyblocks
 */
public class PageableInventoryMenu implements PageableMenu {

    public static final int RAW_SIZE = InventoryMenu.RAW_SIZE;
    public static final int MAX_RAWS = InventoryMenu.MAX_RAWS - 1;
    public static final int MAX_SINGLE_PAGE_SIZE = InventoryMenu.RAW_SIZE * InventoryMenu.MAX_RAWS;
    public static final int MAX_PAGE_SIZE = RAW_SIZE * MAX_RAWS;
    public static final int BACK_BUTTON_INDEX = 45;
    public static final int CLOSE_BUTTON_INDEX = 49;
    public static final int NEXT_BUTTON_INDEX = 53;
    private static final Button CLOSE_BUTTON = new Button(Material.BARRIER, "CLOSE", (p, c) -> true, false);
    private static final String BACK_BUTTON_NAME = "PREV";
    private static final String NEXT_BUTTON_NAME = "NEXT";

    private final MenuManager manager;
    private final String title;
    private final List<Button> buttons;

    public PageableInventoryMenu(MenuManager manager, String title, List<Button> buttons) {
        this.manager = Objects.requireNonNull(manager);
        this.title = Objects.requireNonNull(title);
        this.buttons = new ArrayList<>(buttons);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Menu getPage(int index) {
        if (isPagenationAvailable()) {
            int low = index * MAX_PAGE_SIZE;
            int high = Math.min(low + MAX_PAGE_SIZE, buttons.size());
            List<Button> page = new ArrayList<>(buttons.subList(low, high));
            page.set(CLOSE_BUTTON_INDEX, CLOSE_BUTTON);
            if (index > 0) {
                page.set(BACK_BUTTON_INDEX, new Button(Material.STRUCTURE_VOID, BACK_BUTTON_NAME, (player, c) -> {
                    manager.openMenu(player, this.getPage(index - 1));
                    return false;
                }, false));
            }
            if (index < pageSize() - 1) {
                page.set(NEXT_BUTTON_INDEX, new Button(Material.STRUCTURE_VOID, NEXT_BUTTON_NAME, (player, c) -> {
                    manager.openMenu(player, this.getPage(index + 1));
                    return false;
                }, false));
            }
            return new InventoryMenu(
                    title + " (" + (index + 1) + "/" + pageSize() + ")",
                    buttons
            );
        } else {
            return this;
        }
    }

    public boolean isPagenationAvailable() {
        return size() > MAX_SINGLE_PAGE_SIZE;
    }

    @Override
    public Button getButton(int index) {
        return buttons.get(index);
    }

    @Override
    public int size() {
        return buttons.size();
    }

    @Override
    public int pageSize() {
        if (isPagenationAvailable()) {
            return (int) Math.ceil(((double) buttons.size()) / RAW_SIZE);
        } else {
            return 1;
        }
    }

}
