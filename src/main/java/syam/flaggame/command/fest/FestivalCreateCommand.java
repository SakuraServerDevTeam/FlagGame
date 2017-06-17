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
package syam.flaggame.command.fest;

import java.util.List;
import jp.llv.flaggame.events.FestivalCreateEvent;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import jp.llv.flaggame.util.OnelineBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalCreateCommand extends BaseCommand {

    public FestivalCreateCommand(FlagGameAPI api) {
        super(
                api,
                true,
                1,
                "<festival> <- create a festival",
                Perms.FESTIVAL_CREATE,
                "create"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        if (!FestivalSchedule.NAME_REGEX.matcher(args.get(0)).matches()) {
            throw new CommandException("&cこのフェス名は使用できません！");
        } else if (api.getFestivals().getFestival(args.get(0)).isPresent()) {
            throw new CommandException("&cそのフェス名は既に存在します！");
        }

        FestivalSchedule festival = new FestivalSchedule(args.get(0));
        FestivalCreateEvent event = new FestivalCreateEvent(festival, sender);
        api.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        api.getFestivals().addFestival(festival);

        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);
        gPlayer.createSetupSession(festival);
        OnelineBuilder.newBuilder()
                .info("新規フェス").value(festival.getName())
                .info("を選択して登録しました！")
                .buttonRun("dashboard").append("festival dashboard").create()
                .sendTo(sender);
    }

}
