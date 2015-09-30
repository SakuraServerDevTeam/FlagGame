package syam.flaggame.database;

import syam.flaggame.FlagGame;
import syam.flaggame.manager.StageManager;
import syam.flaggame.player.PlayerManager;

public class MySQLReconnect implements Runnable {
    private final FlagGame plugin;

    public MySQLReconnect(final FlagGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!Database.isConnected()) {
            Database.connect();
            if (Database.isConnected()) {
                // プレイヤープロファイルを更新
                PlayerManager.saveAll();
                PlayerManager.clearAll();

                plugin.getServer().getOnlinePlayers().stream().forEach(PlayerManager::addPlayer);

                // ゲームステージプロファイルを保存
                StageManager.saveAll();
            }
        }
    }
}
