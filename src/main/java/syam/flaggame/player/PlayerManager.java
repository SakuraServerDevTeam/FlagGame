/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import syam.flaggame.FlagGame;

public class PlayerManager {

    // Logger

    public static final Logger log = FlagGame.logger;

    private static final Map<UUID, GamePlayer> players = new HashMap<>();

    /**
     * プレイヤーを追加します
     *
     * @param player 追加するプレイヤー
     * @return プレイヤーオブジェクト {@link GamePlayer}
     */
    public static GamePlayer addPlayer(Player player) {
        GamePlayer fgPlayer = players.get(player.getUniqueId());

        if (fgPlayer != null) {
            // プレイヤーオブジェクトは接続ごとに違うものなので再設定する
            fgPlayer.setPlayer(player);
        } else {
            // 新規プレイヤー
            fgPlayer = new GamePlayer(player);
            players.put(player.getUniqueId(), fgPlayer);
        }

        return fgPlayer;
    }

    /**
     * 全プレイヤーデータを保存する
     */
    public static void saveAll() {
        players.values().stream()
                .map(GamePlayer::getProfile)
                .forEach(PlayerProfile::save);
    }

    /**
     * プレイヤーを取得する
     *
     * @param playerName 取得対象のプレイヤー名
     * @return プレイヤー {@link GamePlayer}
     */
    public static GamePlayer getPlayer(UUID playerName) {
        return players.get(playerName);
    }
    
    public static GamePlayer getPlayer(String name) {
        return players.values().stream()
                .filter(p -> p.getName().toLowerCase().startsWith(name.toLowerCase()))
                .findAny().orElse(null);
    }

    /**
     * プレイヤーを取得する
     *
     * @param player 取得対象のプレイヤー
     * @return プレイヤー {@link GamePlayer}
     */
    public static GamePlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    /*
     * プレイヤーのプロフィールを取得する
     * 
     * @param player
     *            取得対象のプレイヤー名
     * @return プレイヤープロフィール {@link PlayerProfile}
     */
    public static PlayerProfile getProfile(UUID uuid) {
        GamePlayer fgPlayer = players.get(uuid);

        return (fgPlayer != null) ? fgPlayer.getProfile() : null;
    }

    /**
     * プレイヤーのプロフィールを取得する
     *
     * @param player 取得対象のプレイヤー
     * @return プレイヤープロフィール {@link PlayerProfile}
     */
    public static PlayerProfile getProfile(OfflinePlayer player) {
        return getProfile(player.getUniqueId());
    }
    
    public static void update() {
        players.clear();
        Bukkit.getServer().getOnlinePlayers().stream().forEach(PlayerManager::addPlayer);
    }
    
}
