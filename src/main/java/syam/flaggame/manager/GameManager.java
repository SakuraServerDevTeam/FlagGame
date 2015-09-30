package syam.flaggame.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import syam.flaggame.FlagGame;
import syam.flaggame.game.Game;

public class GameManager {
    // Logger
    public static final Logger log = FlagGame.logger;
    private static final String logPrefix = FlagGame.logPrefix;
    private static final String msgPrefix = FlagGame.msgPrefix;

    private final FlagGame plugin;

    public GameManager(final FlagGame plugin) {
        this.plugin = plugin;
    }

    private static HashMap<String, Game> games = new HashMap<>();
    private static Game waitingRandomGame = null;

    /**
     * ゲームマップを返す
     * 
     * @return HashMap<String, Game>
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
                .filter(Game::isReady)
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
                .filter(Game::isStarting)
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
