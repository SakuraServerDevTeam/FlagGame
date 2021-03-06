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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import syam.flaggame.permission.Perms;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.StageSetupSession;
import jp.llv.flaggame.api.stage.Stage;

/**
 *
 * @author SakuraServerDev
 */
public abstract class ObjectiveCommand extends BaseCommand {

    public ObjectiveCommand(FlagGameAPI api, int argLength, String usage, Perms permission, String name, String... aliases) {
        super(api, true, argLength + 1, "<type> " + usage, permission, name, aliases);
    }

    public ObjectiveCommand(FlagGameAPI api, int argLength, String usage, String name, String... aliases) {
        this(api, argLength, usage, null, name, aliases);
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer gplayer = api.getPlayers().getPlayer(player);
        Stage stage = gplayer.getSetupSession(StageSetupSession.class).map(StageSetupSession::getReserved).orElse(null);
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
