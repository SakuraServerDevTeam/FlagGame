/* 
 * Copyright (C) 2015 Syamn, SakruaServerDev.
 * All rights reserved.
 */
package syam.flaggame.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import syam.flaggame.FlagGame;
import syam.flaggame.game.Game;

public class GameManager {
    // Logger
    public static final Logger log = FlagGame.logger;
    
    public GameManager(final FlagGame plugin) {
    }

    private static HashMap<String, Game> games = new HashMap<>();
    private static Game waitingRandomGame = null;

    /**
     * ゲームマップを返す
     * 
     * @return {@code HashMap<String, Game>}
     */
    public static HashMap<String, Game> getGames() {
        return games;
    }

    /**
     * ゲームを追加する
     * 
     * @param stageName
     *            ステージ名
     * @param game
     *            ゲームインスタンス
     */
    public static void addGame(String stageName, Game game) {
        games.put(stageName, game);
    }

    /**
     * ゲームを削除する
     * 
     * @param stageName
     *            ステージ名
     */
    public static void removeGame(String stageName) {
        games.remove(stageName);
    }

    /**
     * ステージ名からゲームを返す
     * 
     * @param gameName
     * @return Game
     */
    public static Game getGame(String gameName) {
        return games.get(gameName);
    }

    /**
     * 受付中のゲームリストを返す
     * 
     * @return {@code List<Stage>}
     */
    public static ArrayList<Game> getReadyingGames() {
        ArrayList<Game> ret = new ArrayList<>();

        games.values().stream()
                .filter(g -> g.getState() == Game.State.ENTRY)
                .forEach(ret::add);

        return ret;
    }

    /**
     * 受付中のゲームリストを返す
     * 
     * @return {@code List<Stage>}
     */
    public static ArrayList<Game> getStartingGames() {
        ArrayList<Game> ret = new ArrayList<>();

        games.values().stream()
                .filter(g -> g.getState() == Game.State.STARTED)
                .forEach(ret::add);

        return ret;
    }

    /* *********** */
    public static void setRandomGame(Game game) {
        waitingRandomGame = game;
    }

    public static Game getRandomGame() {
        return waitingRandomGame;
    }
}
