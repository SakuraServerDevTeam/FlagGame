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
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.profile.RecordType;
import jp.llv.flaggame.trophy.NashornTrophy;
import jp.llv.flaggame.trophy.RecordTrophy;
import jp.llv.flaggame.util.FlagTabCompleter;
import jp.llv.flaggame.util.OnelineBuilder;
import jp.llv.flaggame.util.Parser;
import jp.llv.flaggame.util.StringUtil;
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 */
public class TrophyRecordCommand extends TrophyEditCommand<RecordTrophy> {

    public TrophyRecordCommand(FlagGameAPI api) {
        super(
                api,
                0,
                "[recort type] <- set a record type",
                FlagTabCompleter.builder()
                        .forArg(0).suggestEnum(RecordType.class)
                        .create(),
                RecordTrophy.class,
                "record target"
        );
    }

    @Override
    protected void execute(List<String> args, GamePlayer gamePlayer, Player player, RecordTrophy trophy) throws FlagGameException {
        if (args.isEmpty()) {
            OnelineBuilder.newBuilder()
                    .info("トロフィー").value(trophy.getName())
                    .info("の対象レコードは").value(StringUtil.capitalize(trophy.getTarget().toString()))
                    .info("です！")
                    .sendTo(player);
        } else {
            RecordType target = Parser.asEnum(args.get(0), RecordType.class);
            OnelineBuilder.newBuilder()
                    .info("トロフィー").value(trophy.getName())
                    .info("の対象レコードを").value(StringUtil.capitalize(trophy.getTarget().toString()))
                    .info("に変更しました！")
                    .sendTo(player);
        }
    }

}
