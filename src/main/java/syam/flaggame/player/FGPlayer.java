/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.player;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import syam.flaggame.FlagGame;
import syam.flaggame.game.Game;

public class FGPlayer {
    // Logger
    public static final Logger log = FlagGame.logger;
    private static final String logPrefix = FlagGame.logPrefix;
    private static final String msgPrefix = FlagGame.msgPrefix;

    // プレイヤーデータ
    private Player player;
    private final PlayerProfile profile;

    // 参加中のゲーム
    private Game game = null;

    /**
     * コンストラクタ
     * 
     * @param player
     */
    /*package*/ FGPlayer(final Player player) {
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
    
    /* getter / setter */
    public void setPlayingGame(Game game) {
        this.game = game;
    }

    public Game getPlayingGame() {
        return this.game;
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
}
