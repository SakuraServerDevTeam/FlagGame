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

/**
 * StageManager (StageManager.java)
 *
 * @author syam(syamn)
 */
public class StageManager implements Iterable<Stage> {

    private final Map<String, Stage> stages = Collections.synchronizedMap(new HashMap<>());

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
     * @param stage ステージインスタンス
     */
    public void addStage(Stage stage) {
        stages.put(stage.getName(), stage);
    }

    /**
     * 指定したステージをマップから削除する
     *
     * @param stage 削除するステージ
     */
    public void removeStage(Stage stage) {
        stages.remove(stage.getName());
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

}
