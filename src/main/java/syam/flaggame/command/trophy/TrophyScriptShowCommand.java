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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.trophy.NashornTrophy;
import jp.llv.flaggame.util.FlagTabCompleter;
import jp.llv.flaggame.util.OnelineBuilder;
import jp.llv.flaggame.util.Parser;
import org.bukkit.entity.Player;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class TrophyScriptShowCommand extends TrophyEditCommand<NashornTrophy> {

    public TrophyScriptShowCommand(FlagGameAPI api) {
        super(
                api,
                0,
                "<- show trophy trigger script",
                FlagTabCompleter.empty(),
                NashornTrophy.class,
                "script show",
                "scs"
        );
    }

    @Override
    protected void execute(List<String> args, GamePlayer gamePlayer, Player player, NashornTrophy trophy) throws FlagGameException {
        Perms.SCRIPT.requireTo(player);
        List<String> lines = new ArrayList<>(Arrays.asList(trophy.getScript().split(" ")));
        int line = Parser.asInt(1, args.get(0), lines.size() + 1) - 1;

        if (args.size() == 1) {
            if (line == lines.size()) {//delete a new line (error)
                throw new CommandException("&c新規ラインを削除することはできません！");
            } else {//delete a line
                String deleted = lines.remove(line);
                OnelineBuilder.newBuilder()
                        .value(line + 1).info("行目のスクリプト")
                        .value(deleted).info("を削除しました！")
                        .sendTo(player);
            }
        } else {
            String script = String.join(" ", args.subList(1, args.size()));
            if (line == lines.size()) {//create
                lines.add(script);
                OnelineBuilder.newBuilder()
                        .info("スクリプト")
                        .value(script).info("を最終行に追加しました！")
                        .sendTo(player);
            } else {//update
                lines.set(line, script);
                OnelineBuilder.newBuilder()
                        .value(line + 1).info("行目のスクリプトを")
                        .value(script).info("に更新しました！")
                        .sendTo(player);
            }
        }

    }

}
