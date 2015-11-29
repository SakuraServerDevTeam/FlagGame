/*
 * Copyright (c) 2015 Toyblocks All rights reserved.
 */
package syam.flaggame.command;

import jp.llv.flaggame.reception.GameReception;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class CloseCommand extends BaseCommand {

    public CloseCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "close";
        argLength = 1;
        usage = "<reception> [reason] <- close the reception";
    }

    @Override
    public void execute() throws CommandException {
        GameReception reception = this.plugin.getReceptions().getReception(this.args.get(0))
                .orElseThrow(() -> new CommandException("&c受付'" + args.get(0) + "'が見つかりません！"));
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        
        if (reception.getState()==GameReception.State.CLOSED) {
            throw new CommandException("&cその受付は既に破棄されています!");
        }
        reception.close(args.size() < 2 ? sender.getName()+"Closed":args.get(1));
        gPlayer.sendMessage("&a成功しました!");
    }

    @Override
    public boolean permission() {
        return Perms.CLOSE.has(sender);
    }
    
}
