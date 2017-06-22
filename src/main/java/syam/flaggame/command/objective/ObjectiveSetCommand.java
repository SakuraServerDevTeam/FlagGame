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
package syam.flaggame.command.objective;

import jp.llv.flaggame.api.stage.objective.ObjectiveType;
import java.util.List;
import jp.llv.flaggame.reception.TeamColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.exception.ReservedException;
import syam.flaggame.permission.Perms;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.stage.Stage;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class ObjectiveSetCommand extends ObjectiveCommand {

    public ObjectiveSetCommand(FlagGameAPI api) {
        super(
                api,
                0,
                "[option...] <- enable objective locate tool",
                Perms.OBJECTIVE_SET,
                "set"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage, ObjectiveType type) throws FlagGameException {
        switch (type) {
            case FLAG:
                setFlag(player, stage, args);
                return;
            case CHEST:
                setChest(player, stage, args);
                return;
            case BANNER_SLOT:
                setBannerSlot(player, stage, args);
                return;
            case BANNER_SPAWNER:
                setBannerSpawner(player, stage, args);
                return;
            case NEXUS:
                setNexus(player, stage, args);
                return;
            default:
                throw new CommandException("&c不明なオブジェクティブです！");
        }
    }

    private void setFlag(Player player, Stage game, List<String> args) throws CommandException, ReservedException {
        // 引数チェック
        if (args.size() < 1) {
            throw new CommandException("&c引数が足りません！フラッグの得点を指定してください！");
        }

        // フラッグタイプチェック
        double type;
        try {
            type = Double.parseDouble(args.get(0));
        } catch (NumberFormatException ex) {
            throw new CommandException("フラッグの得点を正しく指定してください!", ex);
        }

        // マネージャーセット
        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(game).setSetting(ObjectiveType.FLAG).setSelectedPoint(type);
        String tool = Material.getMaterial(api.getConfig().getToolID()).name();
        Actions.message(player, "&aフラッグ管理モードを開始しました。選択ツール: " + tool);
    }

    private void setNexus(Player player, Stage stage, List<String> args) throws CommandException, ReservedException {
        if (args.size() < 1) {
            throw new CommandException("&c引数が足りません！目標の得点を正しく指定してください!");
        }

        double point;
        try {
            point = Double.parseDouble(args.get(0));
        } catch (NumberFormatException ex) {
            throw new CommandException("目標の得点を正しく指定してください!", ex);
        }

        TeamColor color;
        if (args.size() < 2) {
            color = null;
        } else {
            try {
                color = TeamColor.of(args.get(1));
            } catch (IllegalArgumentException ex) {
                throw new CommandException("目標のチーム色を正しく指定してください!", ex);
            }
        }

        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(stage).setSetting(ObjectiveType.NEXUS)
                .setSelectedPoint(point)
                .setSelectedColor(color);
        String tool = Material.getMaterial(api.getConfig().getToolID()).name();
        Actions.message(player, "&a目標管理モードを開始しました。選択ツール: " + tool);
    }

    private void setBannerSpawner(Player player, Stage stage, List<String> args) throws CommandException, ReservedException {
        if (args.size() < 2) {
            throw new CommandException("&c引数が足りません! バナーの得点と耐久度を正しく指定してください!");
        }

        byte point, hp;
        try {
            point = Byte.parseByte(args.get(0));
            hp = Byte.parseByte(args.get(1));
        } catch (NumberFormatException ex) {
            throw new CommandException("&c数値フォーマットが異常です!", ex);
        }

        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(stage).setSetting(ObjectiveType.BANNER_SPAWNER)
                .setSelectedPoint(point)
                .setHp(hp);
        String tool = Material.getMaterial(api.getConfig().getToolID()).name();
        Actions.message(player, "&aバナースポナー管理モードを開始しました。選択ツール: " + tool);
    }

    private void setBannerSlot(Player player, Stage stage, List<String> args) throws CommandException, ReservedException {
        TeamColor color;
        if (args.size() < 1) {
            color = null;
        } else {
            try {
                color = TeamColor.of(args.get(0));
            } catch (IllegalArgumentException ex) {
                throw new CommandException("目標のチーム色を正しく指定してください!", ex);
            }
        }

        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(stage).setSetting(ObjectiveType.BANNER_SLOT)
                .setSelectedColor(color);
        String tool = Material.getMaterial(api.getConfig().getToolID()).name();
        Actions.message(player, "&aスロット管理モードを開始しました。選択ツール: " + tool);
    }

    private void setChest(Player player, Stage game, List<String> args) throws ReservedException {
        // マネージャーセット
        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(game).setSetting(ObjectiveType.CHEST);
        String tool = Material.getMaterial(api.getConfig().getToolID()).name();
        Actions.message(player, "&aチェスト管理モードを開始しました。選択ツール: " + tool);
    }

}
