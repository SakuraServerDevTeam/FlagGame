/*
 * Copyright (C) 2017 toyblocks
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
package jp.llv.flaggame.api.stage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import jp.llv.flaggame.api.exception.InvalidOptionException;
import jp.llv.flaggame.util.OptionSet;

/**
 *
 * @author toyblocks
 */
public interface StageAPI extends Iterable<Stage> {

    /**
     * ステージとステージ名を紐付けでマッピングする
     *
     * @param stage ステージインスタンス
     */
    void addStage(Stage stage);

    /**
     * 実行可能なステージリストを返す
     *
     * @return {@code List<Stage>}
     */
    Collection<Stage> getAvailableStages();

    /**
     * 実行可能なステージからランダムで1つ抽出する
     *
     * @param filter filter
     * @return Stage
     * @throws jp.llv.flaggame.api.exception.InvalidOptionException if an option is invalid
     */
    Stage getRandomAvailableStage(OptionSet filter) throws InvalidOptionException;

    /**
     * ステージ名からステージを返す
     *
     * @param stageName a name of a stage to find
     * @return Game
     */
    Optional<Stage> getStage(String stageName);

    /**
     * 全ステージのマップを返す
     *
     * @return {@code HashMap<String, Stage>}
     */
    Map<String, Stage> getStages();

    /**
     * 指定したステージをマップから削除する
     *
     * @param stage 削除するステージ
     */
    void removeStage(Stage stage);

    /**
     * ステージマップをクリアする
     */
    void removeStages();

}
