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
package jp.llv.flaggame.stage;

import java.security.SecureRandom;
import jp.llv.flaggame.api.stage.StageAPI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.util.OptionSet;
import jp.llv.flaggame.api.exception.InvalidOptionException;
import jp.llv.flaggame.api.profile.RecordType;
import jp.llv.flaggame.api.profile.StatEntry;
import jp.llv.flaggame.api.stage.Stage;

/**
 * StageManager (StageManager.java)
 *
 * @author syam(syamn)
 */
public class StageManager implements StageAPI {

    private final FlagGameAPI api;
    private final Map<String, Stage> stages = Collections.synchronizedMap(new HashMap<>());
    private final Random random = new SecureRandom();

    public StageManager(FlagGameAPI api) {
        this.api = api;
    }

    /**
     * 全ステージのマップを返す
     *
     * @return {@code HashMap<String, Stage>}
     */
    @Override
    public Map<String, Stage> getStages() {
        return Collections.unmodifiableMap(this.stages);
    }

    /**
     * ステージとステージ名を紐付けでマッピングする
     *
     * @param stage ステージインスタンス
     */
    @Override
    public void addStage(Stage stage) {
        stages.put(stage.getName(), stage);
    }

    /**
     * 指定したステージをマップから削除する
     *
     * @param stage 削除するステージ
     */
    @Override
    public void removeStage(Stage stage) {
        stages.remove(stage.getName());
    }

    /**
     * ステージマップをクリアする
     */
    @Override
    public void removeStages() {
        stages.clear();
    }

    /**
     * 実行可能なステージリストを返す
     *
     * @return {@code List<Stage>}
     */
    @Override
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
     * @param filter filters to
     * @return Stage
     * @throws jp.llv.flaggame.api.exception.InvalidOptionException if a option is invalid
     */
    @Override
    public Optional<Stage> getRandomAvailableStage(OptionSet filter) throws InvalidOptionException {
        ArrayList<Stage> availables = getAvailableStages();

        if (filter.isPresent("S")) {
            String name = filter.getString("S");
            availables.removeIf(s -> !s.getName().equals(name));
        }
        if (filter.isPresent("s")) {
            String name = filter.getString("s").toLowerCase();
            availables.removeIf(s -> !s.getName().toLowerCase().startsWith(name));
        }
        if (filter.isPresent("r")) {
            double minRate =filter.getDouble("r");
            availables.removeIf(stage -> minRate <= api.getProfiles()
                    .getProfile(stage.getName()).getStat(RecordType.RATE)
                    .map(StatEntry::getAverage).orElse(2.5)
            );
        }
        if (filter.isPresent("R")) {
            double maxRate =filter.getDouble("r");
            availables.removeIf(stage -> api.getProfiles()
                    .getProfile(stage.getName()).getStat(RecordType.RATE)
                    .map(StatEntry::getAverage).orElse(2.5) <= maxRate
            );
        }
        if (filter.isPresent("t")) {
            String tag = filter.getString("t");
            availables.removeIf(s -> !s.getTags().contains(tag));
        }
        if (filter.isPresent("t!")) {
            String tag = filter.getString("t!");
            availables.removeIf(s -> s.getTags().contains(tag));
        }

        if (availables.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(availables.get(random.nextInt(availables.size())));
    }

    /**
     * ステージ名からステージを返す
     *
     * @param stageName a name of a stage to find
     * @return Game
     */
    @Override
    public Optional<Stage> getStage(String stageName) {
        return Optional.ofNullable(stages.get(stageName));
    }

    @Override
    public Iterator<Stage> iterator() {
        return this.getStages().values().iterator();
    }

}
