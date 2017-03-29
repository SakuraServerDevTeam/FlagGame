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
package syam.flaggame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.rollback.RollbackException;
import org.bukkit.World;
import syam.flaggame.FlagGame;

/**
 * StageManager (StageManager.java)
 *
 * @author syam(syamn)
 */
public class StageManager implements Iterable<Stage> {

    private final FlagGame plugin;
    private final HashMap<String, Stage> stages = new HashMap<>();

    public StageManager(FlagGame plugin) {
        this.plugin = plugin;
    }

    /**
     * 全ステージのマップを返す
     *
     * @return {@code HashMap<String, Stage>}
     */
    public Map<String, Stage> getStages() {
        return Collections.unmodifiableMap(this.stages);
    }

    /**
     * ステージとステージ名を紐付けでマッピングする
     *
     * @param stageName ステージ名
     * @param stage ステージインスタンス
     */
    public void addStage(String stageName, Stage stage) {
        stages.put(stageName, stage);
    }

    /**
     * 指定したステージをマップから削除する
     *
     * @param stageName 削除するステージ名
     */
    public void removeStage(String stageName) {
        stages.remove(stageName);
    }

    /**
     * ステージマップをクリアする
     */
    public void removeStages() {
        stages.clear();
    }

    /**
     * 実行可能なステージリストを返す
     *
     * @return {@code List<Stage>}
     */
    public ArrayList<Stage> getAvailableStages() {
        ArrayList<Stage> ret = new ArrayList<>();

        for (Stage stage : stages.values()) {
            if (stage.isAvailable() && !stage.isReserved()) {
                ret.add(stage);
            }
        }

        return ret;
    }

    /**
     * 実行可能なステージからランダムで1つ抽出する
     *
     * @return Stage
     */
    public Stage getRandomAvailableStage() {
        Random rnd = new Random();
        ArrayList<Stage> availables = getAvailableStages();

        if (availables.size() <= 0) {
            return null;
        }

        return availables.get(rnd.nextInt(availables.size()));
    }

    /**
     * ステージ名からステージを返す
     *
     * @param stageName
     * @return Game
     */
    public Optional<Stage> getStage(String stageName) {
        return Optional.ofNullable(stages.get(stageName));
    }

    @Override
    public Iterator<Stage> iterator() {
        return this.getStages().values().iterator();
    }

    public void saveStages() throws DatabaseException {
        if (!plugin.getDatabases().isPresent()) {
            throw new DatabaseException("Not connected");
        }
        World gameWorld = plugin.getServer().getWorld(plugin.getConfigs().getGameWorld());
        for (Stage stage : stages.values()) {
            for (String area : stage.getAreas().getAreas()) {
                AreaInfo info = stage.getAreas().getAreaInfo(area);
                for (AreaInfo.RollbackData rollback : info.getRollbacks().values()) {
                    try {
                        rollback.setData(rollback.getTarget().write(plugin, gameWorld));
                    } catch (RollbackException ex) {
                        throw new DatabaseException(ex);
                    }
                }
            }
        }
        plugin.getDatabases().get().saveStages(stages.values());
    }

    public void loadStages() throws DatabaseException {
        if (!plugin.getDatabases().isPresent()) {
            throw new DatabaseException("Not connected");
        }
        stages.clear();
        World gameWorld = plugin.getServer().getWorld(plugin.getConfigs().getGameWorld());
        for (Stage stage : plugin.getDatabases().get().loadStages()) {
            stages.put(stage.getName(), stage);
            for (String area : stage.getAreas().getAreas()) {
                AreaInfo info = stage.getAreas().getAreaInfo(area);
                for (AreaInfo.RollbackData rollback : info.getRollbacks().values()) {
                    try {
                        rollback.getTarget().read(plugin, gameWorld, rollback.getData());
                    } catch (RollbackException ex) {
                        throw new DatabaseException(ex);
                    }
                }
            }
        }
    }

}
