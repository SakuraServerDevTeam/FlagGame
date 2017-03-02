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
package jp.llv.flaggame.reception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import syam.flaggame.FlagGame;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.exception.CommandException;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author toyblocks
 */
public class EvenRequiredRealtimeTeamingReception extends RealtimeTeamingReception {

    private Set<GamePlayer> waiting = new HashSet<>();

    public EvenRequiredRealtimeTeamingReception(FlagGame plugin, UUID id, List<String> args) {
        super(plugin, id, args);
    }

    @Override
    public void join(GamePlayer player, List<String> args) throws CommandException {
        if (this.getState() != State.OPENED) {
            throw new CommandException("&cこの募集は既に終了しました!");
        }

        //人数でチームをマッピング
        Map<Integer, List<TeamColor>> m = new HashMap<>();
        for (Map.Entry<TeamColor, Set<GamePlayer>> e : this.players.entrySet()) {
            if (!m.containsKey(e.getValue().size())) {
                m.put(e.getValue().size(), new ArrayList<>());
            }
            m.get(e.getValue().size()).add(e.getKey());
        }
        int min = m.keySet().stream().mapToInt(i -> i).min()
                .orElseThrow(() -> new CommandException("&c参加可能チームがありません!"));

        this.waiting.add(player);
        if (waiting.size() == super.stage.getTeams().size()) {
            for (GamePlayer p : this.waiting) {
                List<TeamColor> can = m.get(min);
                TeamColor color = can.get((int) (Math.random() * can.size()));
                this.players.get(color).add(p);
                p.join(this, args);
                int count = this.players.entrySet().stream().map(Map.Entry::getValue).mapToInt(Set::size).sum();
                GamePlayer.sendMessage(this.plugin.getPlayers(), color.getColor() + p.getName() + "&aが'&6"
                        + this.getName() + "&a'で開催予定のゲームに参加しました(&6" + count + "人目&a)");
            }
            this.waiting.clear();
        } else {
            int remain = super.stage.getTeams().size() - waiting.size();
            GamePlayer.sendMessage(this.plugin.getPlayers(), player.getName() + "&aが'&6"
                    + this.getName() + "&a'で開催予定のゲームにエントリーしました(&6あと" + remain + "人で参加&a)");
        }
    }

}
