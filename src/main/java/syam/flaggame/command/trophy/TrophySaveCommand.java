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
import jp.llv.flaggame.api.player.TrophySetupSession;
import jp.llv.flaggame.api.trophy.Trophy;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.util.FlagTabCompleter;
import jp.llv.flaggame.util.OnelineBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class TrophySaveCommand extends BaseCommand {

    public TrophySaveCommand(FlagGameAPI api) {
        super(
                api,
                false,
                0,
                "[trophy] <- save a specified or selected trophy",
                Perms.STAGE_SAVE,
                FlagTabCompleter.builder()
                        .forArg(0).suggestList((a, s, p) -> a.getTrophies().getNormalTrophies())
                        .create(),
                "save"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        Trophy trophy;
        if (args.isEmpty()) {
            trophy = api.getPlayers().getPlayer(player).getSetupSession(TrophySetupSession.class)
                    .orElseThrow(() -> new CommandException("&cあなたはトロフィーを選択していません！"))
                    .getReserved();
        } else {
            trophy = api.getTrophies().getTrophy(args.get(0))
                    .orElseThrow(() -> new CommandException("&cトロフィー'" + args.get(0) + "'が見つかりません！"));
        }
        api.getDatabase()
                .orElseThrow(() -> new CommandException("&cデータベースへの接続に失敗しました！"))
                .saveTrophy(trophy, result -> {
                    try {
                        result.test();
                        OnelineBuilder.newBuilder()
                                .info("トロフィー").value(trophy.getName()).info("の保存に成功しました！")
                                .sendTo(player);
                    } catch(DatabaseException ex) {
                        api.getLogger().warn("Failed to save a trophy", ex);
                        OnelineBuilder.newBuilder()
                                .warn("トロフィー").value(trophy.getName()).warn("の保存に失敗しました！")
                                .sendTo(player);
                    }
                });
    }

}
