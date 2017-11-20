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
package syam.flaggame.command.trophy;

import java.util.List;
import java.util.Objects;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.util.FlagTabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;
import jp.llv.flaggame.api.trophy.Trophy;
import jp.llv.flaggame.api.player.TrophySetupSession;

/**
 *
 * @author toyblocks
 * @param <T> a type of trophy this command supports.
 */
public abstract class TrophyEditCommand<T extends Trophy> extends BaseCommand {

    private final Class<T> type;
    
    public TrophyEditCommand(FlagGameAPI api, int argLength, String usage, FlagTabCompleter completer, Class<T> type, String name, String... aliases) {
        super(api,
                true,
                argLength,
                usage,
                Perms.TROPHY_EDIT,
                completer,
                name,
                aliases
        );
        this.type = Objects.requireNonNull(type);
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer gamePlayer = api.getPlayers().getPlayer(player);
        Trophy trophy = gamePlayer.getSetupSession(TrophySetupSession.class)
                .orElseThrow(() -> new CommandException("&c先に編集するトロフィーを選択してください")).getReserved();
        if (!type.isInstance(trophy)) {
            throw new CommandException("&cあなたの選択しているトロフィーはこのコマンドによりサポートされていません");
        }
        this.execute(args, gamePlayer, player, type.cast(trophy));
    }
    
    protected abstract void execute(List<String> args, GamePlayer gamePlayer, Player player, T trophy);

}
