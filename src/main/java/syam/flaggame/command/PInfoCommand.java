/*
 * Copyright (c) 2015 Toyblocks All rights reserved.
 */
package syam.flaggame.command;

import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class PInfoCommand extends BaseCommand {

    public PInfoCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = true;
        name = "pinfo";
        argLength = 0;
        usage = " <- show your internal information";
    }

    @Override
    public void execute() throws CommandException {
        GamePlayer gp = this.plugin.getPlayers().getPlayer(player);
        gp.sendMessage("&2あなたは現在ゲーム&6"+gp.getEntry().map(e -> e.getName()+"&2("+e.getName()+")に参加しています。").orElse("&2に参加していません"));
    }

    @Override
    public boolean permission() {
        return Perms.PINFO.has(player);
    }
    
}
