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
package syam.flaggame.command.player;

import java.util.Arrays;
import java.util.List;
import jp.llv.flaggame.profile.PlayerProfile;
import jp.llv.flaggame.profile.record.RecordType;
import jp.llv.flaggame.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.util.DashboardBuilder;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;

/**
 *
 * @author SakuraServerDev
 */
public class PlayerStatsCommand extends BaseCommand {

    public PlayerStatsCommand(FlagGame plugin) {
        super(
                plugin,
                false,
                0,
                "[player] <- show specified player's stats",
                "stats"
        );
    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Player target = null;
        if (args.size() >= 1) {
            Perms.PLAYER_STATS_OTHER.requireTo(sender);
            target = plugin.getServer().getPlayer(args.get(0));
        } else if (player != null) {
            Perms.PLAYER_STATS_SELF.requireTo(sender);
            target = player;
        }
        if (target == null) {
            throw new CommandException("&cプレイヤーを指定してください！");
        }
        PlayerProfile profile = plugin.getProfiles().getProfile(target.getUniqueId());
        DashboardBuilder.newBuilder("Player Stats", target.getName())
                .appendList(Arrays.asList(StatCategory.values()), (d, stat) -> {
                    stat.appendTo(d, profile);
                }).sendTo(sender);
    }

    enum StatCategory {
        
        RECEPTION(Stat.ENTRY, Stat.LEAVE),
        RESULT(Stat.WIN, Stat.DRAW, Stat.LOSE),
        COMBAT(Stat.KILL, Stat.DEATH, Stat.KD),
        FLAG(Stat.FLAG_CAPTURE, Stat.FLAG_BREAK, Stat.FLAG_SCORE),
        NEXUS(Stat.NEXUS_BREAK),
        BANNER(Stat.BANNER_HOLD, Stat.BANNER_STEAL, Stat.BANNER_DEPLOY, Stat.BANNER_KEEP),
        RATE(Stat.RATE),
        ;
        
        private final Stat[] stats;

        private StatCategory(Stat ... stats) {
            this.stats = stats;
        }

        public void appendTo(DashboardBuilder dashboard, PlayerProfile profile) {
            for (Stat stat : stats) {
                stat.appendTo(dashboard, profile);
            }
        }
        
    }
    
    enum Stat {

        ENTRY(RecordType.ENTRY),
        LEAVE(RecordType.LEAVE),
        WIN(RecordType.WIN),
        LOSE(RecordType.LOSE),
        DRAW(RecordType.DRAW),
        KILL(RecordType.KILL),
        DEATH(RecordType.DEATH),
        KD(null) {
            @Override
            public void appendTo(DashboardBuilder dashboard, PlayerProfile profile) {
                dashboard.key("K/D")
                        .value(
                                profile.getStat(RecordType.KILL).flatMap(kill
                                -> profile.getStat(RecordType.DEATH)
                                .map(death
                                        -> Double.toString(kill.getCount() / (double) death.getCount())
                                )).orElse("N/A")
                        );
            }
        },
        FLAG_CAPTURE(RecordType.FLAG_CAPTURE),
        FLAG_BREAK(RecordType.FLAG_BREAK),
        FLAG_SCORE(RecordType.FLAG_SCORE),
        NEXUS_BREAK(RecordType.NEXUS_BREAK),
        BANNER_HOLD(RecordType.BANNER_HOLD),
        BANNER_STEAL(RecordType.BANNER_STEAL),
        BANNER_DEPLOY(RecordType.BANNER_DEPLOY),
        BANNER_KEEP(RecordType.BANNER_KEEP),
        RATE(RecordType.DRAW),
        ;

        private final RecordType record;

        private Stat(RecordType record) {
            this.record = record;
        }

        public void appendTo(DashboardBuilder dashboard, PlayerProfile profile) {
            dashboard.key(StringUtil.capitalize(name()))
                    .value(profile.getStat(record).map(e -> Integer.toString(e.getCount())).orElse("N/A"));
        }

    }

}
