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
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.exception.NotRegisteredException;
import jp.llv.flaggame.api.player.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.events.TrophyCreateEvent;
import jp.llv.flaggame.util.FlagTabCompleter;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;
import jp.llv.flaggame.api.trophy.Trophy;
import jp.llv.flaggame.api.player.TrophySetupSession;

/**
 *
 * @author toyblocks
 */
public class TrophyCreateCommand extends BaseCommand {

    public TrophyCreateCommand(FlagGameAPI api) {
        super(api,
                true,
                2,
                "<type> <name> <- create a trophy",
                Perms.TROPHY_CREATE,
                FlagTabCompleter.builder()
                        .forArg(0).suggestList((a, s, r) -> a.getRegistry().getTrophies())
                        .create(),
                "create"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        if (!Trophy.NAME_REGEX.matcher(args.get(1)).matches()) {
            throw new CommandException("&cこのトロフィー名は使用できません！");
        } else if (this.api.getTrophies().getTrophy(args.get(1)).isPresent()) {
            throw new CommandException("&cそのトロフィーは既に存在します！");
        }

        Trophy trophy;
        try {
            trophy = api.getRegistry().getTrophy(args.get(0)).apply(args.get(1));
        } catch (NotRegisteredException ex) {
            throw new CommandException("&cそのトロフィータイプはサポートされていません", ex);
        }
        TrophyCreateEvent event = new TrophyCreateEvent(player, trophy);
        api.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        // 新規登録
        this.api.getTrophies().addTrophy(trophy);

        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(trophy, TrophySetupSession.class);
        Actions.message(player, "&a新規トロフィー'" + trophy.getName() + "'を登録して選択しました！");
    }

}
