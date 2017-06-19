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
package syam.flaggame.command.stage;

import java.util.Arrays;
import java.util.List;
import jp.llv.flaggame.profile.CachedStageProfile;
import jp.llv.flaggame.api.profile.RecordType;
import jp.llv.flaggame.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import jp.llv.flaggame.api.FlagGameAPI;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.util.DashboardBuilder;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.stage.Stage;
import syam.flaggame.permission.Perms;
import jp.llv.flaggame.api.player.GamePlayer;

/**
 *
 * @author SakuraServerDev
 */
public class StageStatsCommand extends BaseCommand {

    public StageStatsCommand(FlagGameAPI api) {
        super(
                api,
                false,
                1,
                "[stage] <- show stage stats",
                Perms.STAGE_STATS,
                "stats"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Stage stage = null;
        if (args.size() >= 1) {
            stage = api.getStages().getStage(args.get(0)).orElse(null);
        } else if (player != null) {
            GamePlayer gplayer = api.getPlayers().getPlayer(player);
            stage = gplayer.getSetupSession().map(s -> s.getSelected(Stage.class)).orElse(null);
        }
        if (stage == null) {
            throw new CommandException("&cステージを指定してください！");
        }
        CachedStageProfile profile = api.getProfiles().getProfile(stage.getName());
        int gameCount = profile.getStat(RecordType.GAME_START).map(e -> e.getCount())
                .orElseThrow(() -> new CommandException("&cそのステージは開催記録がありません！"));
        DashboardBuilder.newBuilder("Stage Stats", stage.getName())
                .appendList(Arrays.asList(StatCategory.values()), (d, stat) -> {
                    stat.appendTo(d, profile, gameCount);
                }).sendTo(sender);
    }

    enum StatCategory {

        RECEPTION(Stat.START),
        COMBAT(Stat.KILL, Stat.DEATH),
        FLAG(Stat.FLAG_CAPTURE, Stat.FLAG_BREAK, Stat.FLAG_SCORE),
        NEXUS(Stat.NEXUS_BREAK),
        BANNER_1(Stat.BANNER_HOLD, Stat.BANNER_STEAL),
        BANNER_2(Stat.BANNER_DEPLOY, Stat.BANNER_KEEP),
        RATE(Stat.RATE),;

        private final Stat[] stats;

        private StatCategory(Stat... stats) {
            this.stats = stats;
        }

        public void appendTo(DashboardBuilder dashboard, CachedStageProfile profile, int gameCount) {
            for (Stat stat : stats) {
                stat.appendTo(dashboard, profile, gameCount);
            }
        }

    }

    enum Stat {

        START(RecordType.GAME_START),
        KILL(RecordType.KILL),
        DEATH(RecordType.DEATH),
        FLAG_CAPTURE(RecordType.FLAG_CAPTURE),
        FLAG_BREAK(RecordType.FLAG_BREAK),
        FLAG_SCORE(RecordType.FLAG_SCORE),
        NEXUS_BREAK(RecordType.NEXUS_BREAK),
        BANNER_HOLD(RecordType.BANNER_HOLD),
        BANNER_STEAL(RecordType.BANNER_STEAL),
        BANNER_DEPLOY(RecordType.BANNER_DEPLOY),
        BANNER_KEEP(RecordType.BANNER_KEEP),
        RATE(null) {
            @Override
            public void appendTo(DashboardBuilder dashboard, CachedStageProfile profile, int gameCount) {
                dashboard.key(StringUtil.capitalize(name()))
                        .value(profile.getStat(RecordType.RATE).map(e -> Double.toString(e.getAverage())).orElse("N/A"));
            }
        },;

        private final RecordType record;

        private Stat(RecordType record) {
            this.record = record;
        }

        public void appendTo(DashboardBuilder dashboard, CachedStageProfile profile, int gameCount) {
            double count = profile.getStat(record).map(e -> (double) e.getCount()).orElse(Double.NaN);
            dashboard.key(StringUtil.capitalize(name()))
                    .value(Double.isNaN(count) ? "N/A" : count).space()
                    .gray('(').gray(Double.isNaN(count) ? "N/A" : count / gameCount).gray(')');
        }

    }

}
