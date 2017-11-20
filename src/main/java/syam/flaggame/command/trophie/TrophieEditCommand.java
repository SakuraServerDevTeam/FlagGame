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
package syam.flaggame.command.trophie;

import java.util.List;
import java.util.Objects;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.TrophieSetupSession;
import jp.llv.flaggame.api.trophie.Trophie;
import jp.llv.flaggame.util.FlagTabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 * @param <T> a type of trophie this command supports.
 */
public abstract class TrophieEditCommand<T extends Trophie> extends BaseCommand {

    private final Class<T> type;
    
    public TrophieEditCommand(FlagGameAPI api, int argLength, String usage, FlagTabCompleter completer, Class<T> type, String name, String... aliases) {
        super(
                api,
                true,
                argLength,
                usage,
                Perms.TROPHIE_EDIT,
                completer,
                name,
                aliases
        );
        this.type = Objects.requireNonNull(type);
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        GamePlayer gamePlayer = api.getPlayers().getPlayer(player);
        Trophie trophie = gamePlayer.getSetupSession(TrophieSetupSession.class)
                .orElseThrow(() -> new CommandException("&c先に編集するトロフィーを選択してください")).getReserved();
        if (!type.isInstance(trophie)) {
            throw new CommandException("&cあなたの選択しているトロフィーはこのコマンドによりサポートされていません");
        }
        this.execute(args, gamePlayer, player, type.cast(trophie));
    }
    
    protected abstract void execute(List<String> args, GamePlayer gamePlayer, Player player, T trophie);

}
