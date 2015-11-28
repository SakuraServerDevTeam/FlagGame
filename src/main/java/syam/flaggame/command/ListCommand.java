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
public class ListCommand extends BaseCommand {

    public ListCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "list";
        argLength = 0;
        usage = " <- show a list of receptions";
    }
    
    @Override
    public void execute() throws CommandException {
        GamePlayer gPlayer = this.plugin.getPlayers().getPlayer(player);
        
        if (this.plugin.getReceptions().getReceptions().isEmpty()) {
            gPlayer.sendMessage("&c現在有効な参加受付はありません");
            return;
        }
        for (GameReception r : this.plugin.getReceptions()) {
            gPlayer.sendMessage("&"+getColorCodeOf(r.getState())+r.getName()+"&e("+r.getID()+")");
        }
    }
    
    private static char getColorCodeOf(GameReception.State state) {
        switch(state) {
            case READY:
                return '7';
            case OPENED:
                return 'a';
            case STARTED:
                return '6';
            case FINISHED:
                return 'b';
            case CLOSED:
                return 'c';
            default:
                return 'f';
        }
    }

    @Override
    public boolean permission() {
        return Perms.LIST.has(sender);
    }
    
}
