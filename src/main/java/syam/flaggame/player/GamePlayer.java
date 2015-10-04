/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.player;

import java.util.UUID;
import java.util.logging.Logger;
import jp.llv.flaggame.reception.GameReception;

import org.bukkit.entity.Player;

import syam.flaggame.FlagGame;
import syam.flaggame.game.Game;
import syam.flaggame.game.Team;

public class GamePlayer {
    // Logger
    public static final Logger log = FlagGame.logger;
    private static final String logPrefix = FlagGame.logPrefix;
    private static final String msgPrefix = FlagGame.msgPrefix;

    // プレイヤーデータ
    private Player player;
    private final PlayerProfile profile;

    private GameReception reception;
    private Team team;
    
    /**
     * コンストラクタ
     * 
     * @param player
     */
    /*package*/ GamePlayer(final Player player) {
        if (player == null) {
            throw new NullPointerException();
        }
        this.player = player;
        this.profile = new PlayerProfile(player.getUniqueId(), true);
        this.profile.setPlayerName(player.getName());
    }

    public String getName() {
        return this.player.getName();
    }
    
    public UUID getUUID() {
        return this.player.getUniqueId();
    }

    public Player getPlayer() {
        return this.player;
    }

    /*package*/ void setPlayer(Player player) {
        this.player = player;
    }

    public PlayerProfile getProfile() {
        return this.profile;
    }
    
    /*package*/ void setEntry(GameReception reception) {
        this.reception = reception;
    }
    
    public GameReception getEntry() {
        return this.reception;
    }
    
    public Game getGame() {
        GameReception entry = this.getEntry();
        return entry != null ? entry.getGame() : null;
    }
    
    /*package*/ void setTeam(Team team) {
        this.team = team;
    }
    
    public Team getTeam() {
        return this.team;
    }
    
}
