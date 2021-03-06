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
import jp.llv.flaggame.api.exception.InvalidNameException;
import jp.llv.flaggame.api.kit.Kit;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.player.NickPosition;
import jp.llv.flaggame.api.trophy.Trophy;
import jp.llv.flaggame.util.FlagTabCompleter;
import jp.llv.flaggame.util.OnelineBuilder;
import jp.llv.flaggame.util.Parser;
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 */
public class TrophyKitAddCommand extends TrophyEditCommand<Trophy> {

    public TrophyKitAddCommand(FlagGameAPI api) {
        super(
                api,
                1,
                "<kit> <- add selected trophy's reward kit",
                FlagTabCompleter.builder()
                        .forArg(0).suggestList((a, s, p) -> a.getKits().getKits().keySet())
                        .create(),
                Trophy.class,
                "kit add"
        );
    }

    @Override
    protected void execute(List<String> args, GamePlayer gamePlayer, Player player, Trophy trophy) throws CommandException {
        Kit kit = api.getKits().getKit(args.get(0))
                .orElseThrow(() -> new CommandException("&cキット'" + args.get(0) + "'が見つかりません"));
        trophy.addRewardKit(kit.getName());
        OnelineBuilder.newBuilder()
                .info("トロフィー").value(trophy.getName())
                .info("の報酬Kitに").value(kit.getName())
                .info("が追加されました！")
                .sendTo(player);
    }

}
