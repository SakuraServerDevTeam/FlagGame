/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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
package syam.flaggame.game;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.llv.flaggame.util.ConfigUtils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import syam.flaggame.FlagGame;

public class StageFileManager {

    // Logger

    public static final Logger log = FlagGame.logger;
    private static final String logPrefix = FlagGame.logPrefix;

    private final FlagGame plugin;

    public StageFileManager(final FlagGame plugin) {
        this.plugin = plugin;
    }

    /* ステージデータ保存/読み出し */
    public void saveStages() {
        String fileDir = plugin.getDataFolder() + System.getProperty("file.separator")
                + "stageData" + System.getProperty("file.separator");
        FileConfiguration confFile;
        for (Stage stage : this.plugin.getStages().getStages().values()) {
            confFile = new YamlConfiguration();

            File file = new File(fileDir + stage.getName() + ".yml");

            ConfigUtils.writeStage(confFile, "stage", stage);

            try {
                confFile.save(file);
            } catch (IOException ex) {
                log.warning(logPrefix + "Couldn't write Stage data!");
                ex.printStackTrace();
            }
        }
    }

    public void loadStages() {
        FileConfiguration confFile = new YamlConfiguration();
        String fileDir = plugin.getDataFolder() + System.getProperty("file.separator") + "stageData";

        File dir = new File(fileDir);
        File[] files = dir.listFiles();

        // ステージデータクリア
        plugin.getStages().removeStages();

        // ファイルなし
        if (files == null || files.length == 0) {
            return;
        }

        // 取得データ
        for (File file : files) {
            try {
                confFile.load(file);
                Stage stage = ConfigUtils.readStage(confFile, "stage");
                this.plugin.getStages().addStage(stage.getName(), stage);
                log.log(Level.INFO, logPrefix + "Loaded Game: {0} ({1})", new Object[]{file.getName(), stage.getName()});

            } catch (IOException | InvalidConfigurationException | NullPointerException ex) {
                log.log(Level.WARNING, "Failed to load a stage: " + file.getName(), ex);
            }
        }
    }
}
