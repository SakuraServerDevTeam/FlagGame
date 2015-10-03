/* 
 * Copyright (c) 2015 SakuraServerDev All rights reserved.
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

    private static final Map<UUID, FGPlayer> players = new HashMap<>();

    /**
     * プレイヤーを追加します
     *
     * @param player 追加するプレイヤー
     * @return プレイヤーオブジェクト {@link FGPlayer}
     */
    public static FGPlayer addPlayer(Player player) {
        FGPlayer fgPlayer = players.get(player.getUniqueId());

        if (fgPlayer != null) {
            // プレイヤーオブジェクトは接続ごとに違うものなので再設定する
            fgPlayer.setPlayer(player);
        } else {
            // 新規プレイヤー
            fgPlayer = new FGPlayer(player);
            players.put(player.getUniqueId(), fgPlayer);
        }

        return fgPlayer;
    }

    /**
     * 指定したプレイヤーをマップから削除します
     *
     * @param playerName 削除するプレイヤー名
     */
    public static void remove(UUID playerName) {
        players.remove(playerName);
    }

    /**
     * 全プレイヤーデータを保存する
     */
    public static void saveAll() {
        players.values().stream()
                .map(FGPlayer::getProfile)
                .forEach(PlayerProfile::save);
    }

    /**
     * プレイヤーを取得する
     *
     * @param playerName 取得対象のプレイヤー名
     * @return プレイヤー {@link FGPlayer}
     */
    public static FGPlayer getPlayer(UUID playerName) {
        return players.get(playerName);
    }
    
    public static FGPlayer getPlayer(String name) {
        return players.values().stream()
                .filter(p -> p.getName().toLowerCase().startsWith(name.toLowerCase()))
                .findAny().orElse(null);
    }

    /**
     * プレイヤーを取得する
     *
     * @param player 取得対象のプレイヤー
     * @return プレイヤー {@link FGPlayer}
     */
    public static FGPlayer getPlayer(Player player) {
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
        FGPlayer fgPlayer = players.get(uuid);

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

    /* getter / setter */
    public static Map<UUID, FGPlayer> getPlayers() {
        return Collections.unmodifiableMap(players);
    }
    
    public static void update() {
        players.clear();
        Bukkit.getServer().getOnlinePlayers().stream().forEach(PlayerManager::addPlayer);
    }
    
}
