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
import java.util.Collection;
import java.util.List;
import org.bukkit.Location;
import jp.llv.flaggame.api.stage.objective.Flag;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.util.DashboardBuilder;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.stage.objective.BannerSlot;
import jp.llv.flaggame.api.stage.objective.BannerSpawner;
import jp.llv.flaggame.api.stage.objective.GameChest;
import jp.llv.flaggame.api.stage.objective.Nexus;
import jp.llv.flaggame.api.stage.objective.Spawn;
import jp.llv.flaggame.api.stage.objective.SpecSpawn;
import jp.llv.flaggame.api.stage.objective.SuperJump;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class ObjectiveListCommand extends ObjectiveCommand {

    public ObjectiveListCommand(FlagGameAPI api) {
        super(
                api,
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
            case SPAWN:
                listSpawn(player, stage, args);
                return;
            case SPEC_SPAWN:
                listSpecSpawn(player, stage, args);
                return;
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
            case SUPER_JUMP:
                listSuperJump(player, stage, args);
                return;
            default:
                throw new CommandException("&c不明なオブジェクティブです！");
        }
    }

    private void listSpawn(Player player, Stage stage, List<String> args) {
        Collection<Spawn> chests = stage.getObjectives(Spawn.class);
        DashboardBuilder.newBuilder("Spawns", chests.size())
                .appendList(chests, (d, obj) -> {
                    Location loc = obj.getLocation();
                    d.text(obj.getColor().getBungeeChatColor(), obj.getName())
                            .buttonTp("tp", player, loc)
                            .buttonRun("delete").append("objective delete")
                            .append(obj.getType())
                            .append(loc.getBlockX()).append(loc.getBlockY()).append(loc.getBlockZ()).create();
                }).buttonRun("enable manager").append("objective set spawn").create()
                .sendTo(player);
    }

    private void listSpecSpawn(Player player, Stage stage, List<String> args) {
        Collection<SpecSpawn> chests = stage.getObjectives(SpecSpawn.class);
        DashboardBuilder.newBuilder("SpecSpawns", chests.size())
                .appendList(chests, (d, obj) -> {
                    Location loc = obj.getLocation();
                    d.gold(obj.getName())
                            .buttonTp("tp", player, loc)
                            .buttonRun("delete").append("objective delete")
                            .append(obj.getType())
                            .append(loc.getBlockX()).append(loc.getBlockY()).append(loc.getBlockZ()).create();
                }).buttonRun("enable manager").append("objective set specspawn").create()
                .sendTo(player);
    }

    private void listFlag(Player player, Stage stage, List<String> args) {
        Collection<Flag> flags = stage.getObjectives(Flag.class);
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
        Collection<GameChest> chests = stage.getObjectives(GameChest.class);
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
        Collection<BannerSlot> slots = stage.getObjectives(BannerSlot.class);
        DashboardBuilder.newBuilder("Banner Slots", slots.size())
                .appendList(slots, (d, obj) -> {
                    Location loc = obj.getLocation();
                    d.key(obj.getName());
                    if (obj.getColor() == null) {
                        d.value("全チーム");
                    } else {
                        d.text(obj.getColor().getBungeeChatColor(), obj.getColor().getName());
                    }
                    d.buttonTp("tp", player, loc)
                            .buttonRun("delete").append("objective delete")
                            .append(obj.getType())
                            .append(loc.getBlockX()).append(loc.getBlockY()).append(loc.getBlockZ()).create();
                }).buttonSuggest("enable manager").append("objective set banner_slot").create()
                .sendTo(player);
    }

    private void listBannerSpawner(Player player, Stage stage, List<String> args) {
        Collection<BannerSpawner> spawners = stage.getObjectives(BannerSpawner.class);
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
        Collection<Nexus> nexuses = stage.getObjectives(Nexus.class);
        DashboardBuilder.newBuilder("Nexuses", nexuses.size())
                .appendList(nexuses, (d, obj) -> {
                    Location loc = obj.getLocation();
                    d.key(obj.getName())
                            .green(obj.getPoint()).green("ポイント").space();
                    if (obj.getColor() == null) {
                        d.white("全チーム");
                    } else {
                        d.text(obj.getColor().getBungeeChatColor(), obj.getColor().getName());
                    }
                    d.buttonTp("tp", player, loc)
                            .buttonRun("delete").append("objective delete")
                            .append(obj.getType())
                            .append(loc.getBlockX()).append(loc.getBlockY()).append(loc.getBlockZ()).create();
                }).buttonSuggest("enable manager").append("objective set nexus").create()
                .sendTo(player);
    }

    private void listSuperJump(Player player, Stage stage, List<String> args) {
        Collection<SuperJump> superjumps = stage.getObjectives(SuperJump.class);
        DashboardBuilder.newBuilder("SuperJumps", superjumps.size())
                .appendList(superjumps, (d, obj) -> {
                    Location loc = obj.getLocation();
                    d.key(obj.getName())
                            .green("半径").green(obj.getRange()).space()
                            .green("強さ").green(obj.getVelocity().length());
                    d.buttonTp("tp", player, loc)
                            .buttonRun("delete").append("objective delete")
                            .append(obj.getType())
                            .append(loc.getBlockX()).append(loc.getBlockY()).append(loc.getBlockZ()).create();
                }).buttonSuggest("create new").append("objective set super_jump").create()
                .sendTo(player);
    }

}
