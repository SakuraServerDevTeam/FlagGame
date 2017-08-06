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
package syam.flaggame.command.stage;

import java.util.List;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.exception.InvalidNameException;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.util.OnelineBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class StageTagAddCommand extends BaseCommand {

    public StageTagAddCommand(FlagGameAPI api) {
        super(
                api,
                false,
                2,
                "<stage> <tag> <- show stage stats",
                Perms.STAGE_TAG,
                "tag remove"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        Stage stage = api.getStages().getStage(args.get(0))
                .orElseThrow(() -> new CommandException("&cステージ'" + args.get(0) + "'が見つかりません！"));
        try {
            stage.addTag(args.get(1));
        } catch (InvalidNameException ex) {
            throw new CommandException("&c不正なタグ名です！", ex);
        }
        OnelineBuilder.newBuilder()
                .value(stage.getName()).info("にタグ")
                .value(args.get(1)).info("を追加しました！")
                .sendTo(sender);
    }

}
