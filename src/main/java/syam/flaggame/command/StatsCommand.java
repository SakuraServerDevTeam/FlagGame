/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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
package syam.flaggame.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import syam.flaggame.FlagGame;

import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.player.PlayerManager;
import syam.flaggame.player.PlayerProfile;
import syam.flaggame.util.Actions;

public class StatsCommand extends BaseCommand {

    public StatsCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "stats";
        argLength = 0;
        usage = "[player] <- show game stats";
    }

    @Override
    public void execute() throws CommandException {
        PlayerProfile prof = null;
        boolean other = false;

        // 自分の情報表示
        if (args.size() <= 0) {
            // check console
            if (!(sender instanceof Player)) {
                throw new CommandException("&c情報を表示するユーザ名を入力してください");
            }

            // check permission
            if (!Perms.STATS_SELF.has(sender)) {
                throw new CommandException("&cあなたはこのコマンドを使う権限がありません");
            }

            prof = this.plugin.getPlayers().getProfile(player);
        } // 他人の情報表示
        else {
            other = true;

            // check permission
            if (!Perms.STATS_OTHER.has(sender)) {
                throw new CommandException("&cあなたは他人の情報を見る権限がありません");
            }

            prof = this.plugin.getPlayers().getProfile(args.get(0));
        }

        // check null
        if (prof == null) {
            throw new CommandException("&cプレイヤー情報が正しく読み込めませんでした");
        }

        // メッセージ送信
        for (String line : buildStrings(prof, other)) {
            Actions.message(sender, line);
        }
    }

    private List<String> buildStrings(PlayerProfile prof, boolean other) {
        List<String> l = new ArrayList<>();
        l.clear();

        // ヘッダー
        l.add("&a[FlagGame] プレイヤー情報");
        if (other) {
            l.add("&aプレイヤー: &6" + prof.getName());
        }

        // 一般 *************************************************
        l.add("&6-=== 一般 ===-");
        l.add("&eゲーム参加: &a" + prof.getPlayed() + " 回");
        l.add("&2 途中退場: " + (prof.getExited() == 0 ? "a" : "c") + prof.getExited() + " 回");

        // 結果 *************************************************
        l.add("&6-=== ゲーム勝敗 ===-");
        l.add("&e Win: &a" + prof.getWonGame() + " 回");
        l.add("&eLose: &a" + prof.getLostGame() + " 回");
        l.add("&eDraw: &a" + prof.getDrewGame() + " 回");
        l.add("&e勝率: " + prof.getFormattedWinningRate()+" %");

        // フラッグ *************************************************
        l.add("&6-=== フラッグ ===-");
        l.add("&e 設置: &a" + prof.getPlacedFlag() + " フラッグ");
        l.add("&e 破壊: &a" + prof.getBrokenFlag() + " フラッグ");

        // 戦闘 *************************************************
        l.add("&6-=== 戦闘 ===-");
        l.add("&e Kill: &a" + prof.getKill() + " 回");
        l.add("&eDeath: &a" + prof.getDeath() + " 回");
        l.add("&e  K/D: " + prof.getFormattedKD()); // kd

        return l;
    }

    @Override
    public boolean permission() {
        return (Perms.STATS_SELF.has(sender) || Perms.STATS_OTHER.has(sender));
    }
}
