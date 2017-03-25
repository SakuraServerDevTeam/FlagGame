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
package syam.flaggame.command.area;

import syam.flaggame.FlagGame;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author toyblocks
 */
public abstract class AreaCommand extends BaseCommand{
    
    public AreaCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = true;
    }

    @Override
    public void execute() throws CommandException {
        GamePlayer gp = plugin.getPlayers().getPlayer(this.player);
        if (!gp.getSetupSession().isPresent()) {
            throw new CommandException("&c先に編集するゲームを選択してください");
        }
        this.execute(gp.getSetupSession().get().getSelectedStage());
    }
    
    public abstract void execute(Stage stage) throws CommandException;
    
}
