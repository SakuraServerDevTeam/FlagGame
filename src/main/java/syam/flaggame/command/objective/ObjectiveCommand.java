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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author SakuraServerDev
 */
public abstract class ObjectiveCommand extends BaseCommand {

    public ObjectiveCommand(FlagGame plugin, int argLength, String usage, Perms permission, String name, String... aliases) {
        super(plugin, true, argLength + 1, "<type> " + usage, permission, name, aliases);
    }

    public ObjectiveCommand(FlagGame plugin, int argLength, String usage, String name, String... aliases) {
        this(plugin, argLength, usage, null, name, aliases);
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer gplayer = plugin.getPlayers().getPlayer(player);
        Stage stage = gplayer.getSetupSession().map(s -> s.getSelected(Stage.class)).orElse(null);
        if (stage == null) {
            throw new CommandException("&cあなたはステージを選択していません！");
        }
        ObjectiveType type;
        try {
            type = ObjectiveType.of(args.get(0));
        } catch (IllegalArgumentException ex) {
            String types = Arrays.stream(ObjectiveType.values())
                    .map(p -> p.toString().toLowerCase())
                    .collect(Collectors.joining("/"));
            throw new CommandException("&cそのオブジェクティブはサポートされていません！\n&c" + types, ex);
        }
        args.remove(0);
        execute(args, player, stage, type);
    }

    public abstract void execute(List<String> args, Player player, Stage stage, ObjectiveType type) throws FlagGameException;

}
