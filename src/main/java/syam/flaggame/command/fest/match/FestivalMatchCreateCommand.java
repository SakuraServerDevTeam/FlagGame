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
package syam.flaggame.command.fest.match;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.reception.fest.FestivalMatch;
import jp.llv.flaggame.reception.fest.FestivalSchedule;
import jp.llv.flaggame.util.OnelineBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.FlagGameException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class FestivalMatchCreateCommand extends BaseCommand {

    public FestivalMatchCreateCommand(FlagGameAPI api) {
        super(
                api,
                true,
                2,
                "<round> <stage> <- create a festival match",
                Perms.FESTIVAL_MATCH_CREATE,
                "create"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        FestivalSchedule festival = api.getPlayers().getPlayer(player)
                .getSetupSession().map(s -> s.getSelected(FestivalSchedule.class))
                .orElseThrow(() -> new CommandException("&cフェスを選択してください！"));
        int index;
        try {
            index = Integer.parseInt(args.get(0)) - 1;
        } catch (NumberFormatException ex) {
            throw new CommandException("&c異常な数値フォーマットです！");
        }
        if (festival.getMatches().size() == index) {
            festival.addRound(index);
        } else if (index < 0 || festival.getMatches().size() < index) {
            throw new CommandException("&cそのラウンドは存在しません！");
        }
        Stage stage = api.getStages().getStage(args.get(1))
                .orElseThrow(() -> new CommandException("&cそのステージは存在しません！"));
        if (festival.getRound(index).containsKey(args.get(1))) {
            throw new CommandException("&cそのマッチは既に追加されています！");
        } else if (festival.getTeams().size() != stage.getSpawns().size()) {
            throw new CommandException("&cそのステージはチーム数が一致していません！");
        }
        FestivalMatch match = new FestivalMatch(args.get(1));
        festival.addMatch(index, match);
        OnelineBuilder.newBuilder()
                .info("フェス").value(festival.getName())
                .info("のラウンド").value(args.get(0))
                .info("にステージ").value(args.get(1))
                .info("のマッチを追加しました！")
                .sendTo(sender);
        for (Map.Entry<TeamColor, String> team : festival.getTeams().entrySet()) {
            if (stage.getSpawns().containsKey(team.getKey())) {
                match.setColorMap(team.getKey(), team.getKey());
                continue;
            }
            OnelineBuilder.newBuilder()
                    .warn("ステージ").value(args.get(1))
                    .warn("のチーム色はフェス設定と一致していません。")
                    .buttonSuggest("set")
                    .append("festival match team")
                    .append(team.getValue()).create()
                    .sendTo(sender);

        }
    }

    @Override
    protected List<String> complete(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        FestivalSchedule festival = api.getPlayers().getPlayer(player)
                .getSetupSession().map(s -> s.getSelected(FestivalSchedule.class))
                .orElseThrow(() -> new FlagGameException());
        if (args.isEmpty()) {
            throw new IllegalArgumentException();
        } else if (args.size() == 1) {
            return IntStream.rangeClosed(1, festival.getMatches().size())
                    .mapToObj(Integer::toString)
                    .filter(s -> s.startsWith(args.get(0)))
                    .collect(Collectors.toList());
        } else if (args.size() == 2) {
            return api.getStages().getStages().keySet().stream()
                    .filter(s -> s.startsWith(args.get(1)))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
