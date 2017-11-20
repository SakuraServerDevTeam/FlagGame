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
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.exception.NotRegisteredException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.TrophieSetupSession;
import jp.llv.flaggame.api.trophie.Trophie;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.events.TrophieCreateEvent;
import jp.llv.flaggame.util.FlagTabCompleter;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

/**
 *
 * @author toyblocks
 */
public class TrophieCreateCommand extends BaseCommand {

    public TrophieCreateCommand(FlagGameAPI api) {
        super(
                api,
                true,
                2,
                "<type> <name> <- create a trophie",
                Perms.TROPHIE_CREATE,
                FlagTabCompleter.builder()
                        .forArg(0).suggestList((a, s, r) -> a.getRegistry().getTrophies())
                        .create(),
                "create"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        if (!Trophie.NAME_REGEX.matcher(args.get(1)).matches()) {
            throw new CommandException("&cこのトロフィー名は使用できません！");
        } else if (this.api.getTrophies().getTrophie(args.get(1)).isPresent()) {
            throw new CommandException("&cそのトロフィーは既に存在します！");
        }

        Trophie trophie;
        try {
            trophie = api.getRegistry().getTrophie(args.get(0)).apply(args.get(1));
        } catch (NotRegisteredException ex) {
            throw new CommandException("&cそのトロフィータイプはサポートされていません", ex);
        }
        TrophieCreateEvent event = new TrophieCreateEvent(player, trophie);
        api.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        // 新規登録
        this.api.getTrophies().addTrophie(trophie);

        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(trophie, TrophieSetupSession.class);
        Actions.message(player, "&a新規ステージ'" + trophie.getName() + "'を登録して選択しました！");
    }

}
