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

import syam.flaggame.game.objective.ObjectiveType;
import java.util.Collection;
import java.util.List;
import org.bukkit.Location;
import syam.flaggame.game.objective.Flag;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import jp.llv.flaggame.util.DashboardBuilder;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.game.objective.BannerSlot;
import syam.flaggame.game.objective.BannerSpawner;
import syam.flaggame.game.objective.GameChest;
import syam.flaggame.game.objective.Nexus;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class ObjectiveListCommand extends ObjectiveCommand {

    public ObjectiveListCommand(FlagGame plugin) {
        super(
                plugin,
                0,
                "<- show objective list",
                Perms.OBJECTIVE_LIST,
                "list",
                "l"
        );
    }

    @Override
    public void execute(List<String> args, Player player, Stage stage, ObjectiveType type) throws CommandException {
        switch (type) {
            case FLAG:
                listFlag(player, stage, args);
                return;
            case CHEST:
                listChest(player, stage, args);
                return;
            case BANNER_SLOT:
                listBannerSlot(player, stage, args);
                return;
            case BANNER_SPAWNER:
                listBannerSpawner(player, stage, args);
                return;
            case NEXUS:
                listNexus(player, stage, args);
                return;
            default:
                throw new CommandException("&c不明なオブジェクティブです！");
        }
    }

    private void listFlag(Player player, Stage stage, List<String> args) {
        Collection<Flag> flags = stage.getObjectives(Flag.class).values();
        DashboardBuilder.newBuilder("Flags", flags.size())
                .appendList(flags, (d, obj) -> {
                    Location loc = obj.getLocation();
                    d.key(obj.getName())
                            .green(obj.getFlagPoint()).green("ポイント").space()
                            .green(obj.isProducing() ? "producing" : "non-producing")
                            .buttonTp("tp", player, loc)
                            .buttonRun("delete").append("objective delete")
                            .append(obj.getType())
                            .append(loc.getBlockX()).append(loc.getBlockY()).append(loc.getBlockZ()).create();
                }).buttonSuggest("enable manager").append("objective set flag").create()
                .sendTo(player);
    }

    private void listChest(Player player, Stage stage, List<String> args) {
        Collection<GameChest> chests = stage.getObjectives(GameChest.class).values();
        DashboardBuilder.newBuilder("Containers", chests.size())
                .appendList(chests, (d, obj) -> {
                    Location loc = obj.getLocation();
                    d.gold(obj.getName())
                            .buttonTp("tp", player, loc)
                            .buttonRun("delete").append("objective delete")
                            .append(obj.getType())
                            .append(loc.getBlockX()).append(loc.getBlockY()).append(loc.getBlockZ()).create();
                }).buttonRun("enable manager").append("objective set chest").create()
                .sendTo(player);
    }

    private void listBannerSlot(Player player, Stage stage, List<String> args) {
        Collection<BannerSlot> slots = stage.getObjectives(BannerSlot.class).values();
        DashboardBuilder.newBuilder("Banner Slots", slots.size())
                .appendList(slots, (d, obj) -> {
                    Location loc = obj.getLocation();
                    d.key(obj.getName());
                    if (obj.getColor() == null) {
                        d.value("全チーム");
                    } else {
                        d.text(obj.getColor().getBungeeChatColor(), obj.getColor().getTeamName());
                    }
                    d.buttonTp("tp", player, loc)
                            .buttonRun("delete").append("objective delete")
                            .append(obj.getType())
                            .append(loc.getBlockX()).append(loc.getBlockY()).append(loc.getBlockZ()).create();
                }).buttonSuggest("enable manager").append("objective set banner_slot").create()
                .sendTo(player);
    }

    private void listBannerSpawner(Player player, Stage stage, List<String> args) {
        Collection<BannerSpawner> spawners = stage.getObjectives(BannerSpawner.class).values();
        DashboardBuilder.newBuilder("Banner Spawners", spawners.size())
                .appendList(spawners, (d, obj) -> {
                    Location loc = obj.getLocation();
                    d.key(obj.getName())
                            .green(obj.getPoint()).green("ポイント").space()
                            .green("HP").green(obj.getHp()).space()
                            .green(obj.isProducing() ? "producing" : "non-producing")
                            .buttonTp("tp", player, loc)
                            .buttonRun("delete").append("objective delete")
                            .append(obj.getType())
                            .append(loc.getBlockX()).append(loc.getBlockY()).append(loc.getBlockZ()).create();
                }).buttonSuggest("enable manager").append("objective set banner_spawner").create()
                .sendTo(player);
    }

    private void listNexus(Player player, Stage stage, List<String> args) {
        Collection<Nexus> nexuses = stage.getObjectives(Nexus.class).values();
        DashboardBuilder.newBuilder("Nexuses", nexuses.size())
                .appendList(nexuses, (d, obj) -> {
                    Location loc = obj.getLocation();
                    d.key(obj.getName())
                            .green(obj.getPoint()).green("ポイント").space();
                    if (obj.getColor() == null) {
                        d.white("全チーム");
                    } else {
                        d.text(obj.getColor().getBungeeChatColor(), obj.getColor().getTeamName());
                    }
                    d.buttonTp("tp", player, loc)
                            .buttonRun("delete").append("objective delete")
                            .append(obj.getType())
                            .append(loc.getBlockX()).append(loc.getBlockY()).append(loc.getBlockZ()).create();
                }).buttonSuggest("enable manager").append("objective set nexus").create()
                .sendTo(player);
    }

}
