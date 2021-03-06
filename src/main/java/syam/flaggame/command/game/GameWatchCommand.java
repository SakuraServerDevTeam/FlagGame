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
package syam.flaggame.command.game;

import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import jp.llv.flaggame.api.game.Game;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import jp.llv.flaggame.api.FlagGameAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import jp.llv.flaggame.api.exception.CommandException;

import syam.flaggame.permission.Perms;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.api.stage.objective.SpecSpawn;
import syam.flaggame.util.Actions;

public class GameWatchCommand extends BaseCommand {

    public GameWatchCommand(FlagGameAPI api) {
        super(
                api,
                true,
                0,
                "[stage] <- watch the game",
                Perms.GAME_WATCH,
                "watch"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Stage stage;
        GamePlayer gPlayer = this.api.getPlayers().getPlayer(player);

        if (args.size() >= 1) {
            stage = api.getStages().getStage(args.get(0))
                    .orElseThrow(() -> new CommandException("&cステージ'" + args.get(0) + "'が見つかりません"));
        } // 引数がなければ自動補完
        else {
            Collection<Game> startingGames = this.api.getGames().getGames(Game.State.STARTED);
            if (gPlayer.getEntry().map(r -> r.getState().toGameState() != Game.State.FINISHED).orElse(false) && gPlayer.getStage().isPresent()) {
                stage = gPlayer.getStage().get();
            } else if (startingGames.isEmpty()) {
                Actions.message(player, "&c現在、始まっているゲームはありません！");
                return;
            } else if (startingGames.size() >= 2) {
                Actions.message(player, "&c複数のゲームが始まっています！観戦するステージを指定してください！");
                return;
            } else {
                stage = startingGames.iterator().next().getStage();
            }
        }

        List<SpecSpawn> specSpawns = stage.getObjectives(SpecSpawn.class);
        if (specSpawns.isEmpty()) {
            throw new CommandException("&cステージ'" + stage.getName() + "'は観戦者のスポーン地点が設定されていません");
        }
        Location specSpawn = specSpawns.get(new Random().nextInt(specSpawns.size())).getLocation();

        if (gPlayer.getGame().isPresent() && gPlayer.getGame().get().getState() == Game.State.STARTED) {
            Actions.message(player, "&cあなたはゲームに参加しているため移動できません！");
            return;
        }

        // テレポート
        if (!player.getWorld().equals(specSpawn.getWorld())) {
            gPlayer.setTpBackLocation(player.getLocation());
        }
        player.teleport(specSpawn, TeleportCause.PLUGIN);
        Actions.message(player, "&aステージ'" + stage.getName() + "'の観戦者スポーン地点へ移動しました！");
        Actions.message(player, "&2 '&6/flag leave&2' コマンドで元の地点へ戻ることができます！");
    }
}
