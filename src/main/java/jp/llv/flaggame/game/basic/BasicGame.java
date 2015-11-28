/* 
 * Copyright (C) 2015 Toyblocks, SakuraServerDev
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
package jp.llv.flaggame.game.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import jp.llv.flaggame.events.GameStartEvent;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.reception.GameReception;
import jp.llv.flaggame.reception.Team;
import jp.llv.flaggame.util.IntMap;
import jp.llv.flaggame.util.MapUtils;
import jp.llv.flaggame.util.ConvertUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import syam.flaggame.FlagGame;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class BasicGame implements Game {

    private final FlagGame plugin;
    private final GameReception reception;
    private final Stage stage;
    private final Map<TeamColor, Team> teams;

    private final IntMap<GamePlayer> personalKillCount = new IntMap<>();
    private final IntMap<TeamColor> killCount = new IntMap<>();
    private final IntMap<GamePlayer> personalDeathCount = new IntMap<>();
    private final IntMap<TeamColor> deathCount = new IntMap<>();

    private final Queue<Runnable> onFinishing = new LinkedList<>();

    private State state = State.PREPARATION;
    private long expectedFinishAt = 0;

    private Team wonTeam = null;

    public BasicGame(FlagGame plugin, GameReception reception, Stage stage, Collection<Team> ts) {
        if (plugin == null || reception == null) {
            throw new NullPointerException();
        }
        if (stage.getReception().orElseThrow(() -> {
            return new IllegalStateException("The stage is not reserved");
        }) != reception) {
            throw new IllegalStateException("The stage is not reserved by the reception");
        }
        this.plugin = plugin;
        this.reception = reception;
        this.stage = stage;
        this.teams = new EnumMap<>(TeamColor.class);
        ts.forEach(t -> {
            this.teams.put(t.getColor(), t);
        });
    }

    public BasicGame(FlagGame plugin, GameReception reception, Stage stage, Team... teams) {
        this(plugin, reception, stage, Arrays.asList(teams));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void start() throws CommandException {
        if (this.state != State.PREPARATION) {
            throw new CommandException(new IllegalStateException());
        }

        GameStartEvent event = new GameStartEvent(this);
        this.plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            throw new CommandException("&cCancelled by event-api");
        }

        if (this.getTeams().stream().map(Team::getPlayers).anyMatch(Collection::isEmpty)) {
            throw new CommandException("&cany team is empty");
        }

        try {
            this.stage.validate();
        } catch (NullPointerException ex) {
            throw new CommandException("&cthe stage is not ready", ex);
        }

        stage.initialize();

        this.state = State.STARTED;
        this.expectedFinishAt = System.currentTimeMillis() + this.stage.getGameTime();

        this.stage.getProfile().addPlayed();

        GamePlayer.sendMessage(this.reception.getPlayers(),
                "&2ゲーム'&6" + this.reception.getName() + "&2'が始まりました！",
                "&2開催ステージ: '&6" + this.stage.getName() + "&2' &f| &2制限時間: " + ConvertUtils.format(this.stage.getGameTime()),
                this.getTeams().stream()
                .map(t -> t.getColor().getRichName() + "&f" + t.getPlayers().size() + "人&r").collect(Collectors.joining(", "))
        );
        this.stage.getSpecSpawn().ifPresent(l -> {
            GamePlayer.sendMessage(this.reception.getPlayers(),
                    "&2 '&6/f watch " + this.reception.getID() + "&2' コマンドで観戦することができます！"
            );
        });

        for (Team team : this.getTeams()) {
            Location teamSpawn = this.stage.getSpawn(team.getColor());
            for (GamePlayer player : team) {
                player.getProfile().addPlayed();

                Player vp = player.getPlayer();
                if (!player.getPlayer().isOnline()) {
                    continue;
                }
                vp.teleport(teamSpawn, TeleportCause.PLUGIN);
                vp.setGameMode(GameMode.SURVIVAL);

                //インベントリ操作
                vp.getInventory().clear();
                vp.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short) 0, team.getColor().getBlockData()));
                vp.getInventory().setChestplate(null);
                vp.getInventory().setLeggings(null);
                vp.getInventory().setBoots(null);

                //ステータス値
                vp.setHealth(20D);
                vp.setFoodLevel(20);
                vp.setSaturation(20);
                vp.setLevel(0);
                vp.setExp(0f);

                //ステータス効果
                Arrays.stream(PotionEffectType.values())
                        .filter(Objects::nonNull).filter(vp::hasPotionEffect).forEach(vp::removePotionEffect);

                //プレイヤーリストへ色適用
                player.setTabName(team.getColor() + player.getName());
            }
        }

        //各種処理系開始
        BGListener playerListener = new BGPlayerListener(plugin, this);
        this.plugin.getServer().getPluginManager().registerEvents(playerListener, this.plugin);
        this.onFinishing.offer(playerListener::unregister);

        BGListener blockListener = new BGBlockListener(plugin, this);
        this.plugin.getServer().getPluginManager().registerEvents(blockListener, this.plugin);
        this.onFinishing.offer(blockListener::unregister);

        BGListener inventoryListener = new BGInventoryListener(plugin, this);
        this.plugin.getServer().getPluginManager().registerEvents(inventoryListener, this.plugin);
        this.onFinishing.offer(inventoryListener::unregister);

        BGListener entityListener = new BGEntityListener(plugin, this);
        this.plugin.getServer().getPluginManager().registerEvents(entityListener, this.plugin);
        this.onFinishing.offer(entityListener::unregister);

        BukkitTask stopTask = this.plugin.getServer().getScheduler()
                .runTaskLater(plugin, this::stop, ConvertUtils.toTick(this.stage.getGameTime()));
        this.onFinishing.offer(stopTask::cancel);

        LongStream lessThanAMinute = LongStream//1~10秒,30秒
                .of(1000L, 2000L, 3000L, 4000L, 5000L, 6000L, 7000L, 8000L, 9000L, 10000L, 30000L)
                .filter(t -> t <= this.stage.getGameTime());
        LongStream aMinute = LongStream.rangeClosed(1, this.stage.getGameTime())
                .filter(t -> t % 60000L == 0);
        LongStream.concat(lessThanAMinute, aMinute).map(ConvertUtils::toTick).forEach(t -> {
            BukkitTask task = this.plugin.getServer().getScheduler()
                    .runTaskLater(plugin, this::notifyRemainTime, t);
            this.onFinishing.offer(task::cancel);
        });
    }

    private void stop() {
        if (this.state != State.STARTED) {
            throw new IllegalStateException();
        }

        this.state = State.FINISHED;

        while (!this.onFinishing.isEmpty()) {
            this.onFinishing.peek().run();
        }

        Map<TeamColor, Double> points = new EnumMap<>(TeamColor.class);
        Map<TeamColor, Integer> flagPoints = new EnumMap<>(TeamColor.class);
        Map<TeamColor, Integer> kills = new EnumMap<>(TeamColor.class);
        List<String> msg = new ArrayList<>();

        Map<TeamColor, Map<Byte, Integer>> flagPointsMap = stage.checkFlag();
        for (TeamColor col : flagPoints.keySet()) {
            int f = flagPointsMap.get(col).entrySet().stream().mapToInt(e -> e.getKey() * e.getValue()).sum();
            flagPoints.put(col, f);

            int k = this.getKillCount(col);
            kills.put(col, k);

            double p = f;
            points.put(col, Double.valueOf(f));
            msg.add(col.getColor() + col.getTeamName() + "チーム得点: &6" + p + col.getColor() + "点&f(フラッグ: " + f + "点)");
        }

        TeamColor won = null;
        Map<Double, Set<TeamColor>> rPoints = MapUtils.rank(points, (d1, d2) -> Double.compare(d2, d1));
        Map.Entry<Double, Set<TeamColor>> first = rPoints.entrySet().iterator().next();
        if (first.getValue().size() == 1) {
            won = first.getValue().iterator().next();
        }

        Map.Entry<Integer, Set<GamePlayer>> topKill
                = MapUtils.rank(this.personalKillCount, (i1, i2) -> Integer.compare(i2, i1)).entrySet().iterator().next();

        GamePlayer.sendMessage(this.plugin.getPlayers(),
                "&2フラッグゲーム'&6" + this.stage.getName() + "&2'が終わりました!",
                String.join("&f, ", msg),
                won != null
                        ? won.getColor() + won.getTeamName() + "チームの勝利です!"
                        : "このゲームは引き分けです!",
                "&6トップキル: " + topKill.getValue().stream().map(GamePlayer::getColoredName)
                .collect(Collectors.joining("&f, ")) + "&f (&6" + topKill.getKey() + "Kills&f)"
        );

        for (GamePlayer g : this.getReception().getPlayers()) {
            if (!g.getPlayer().isOnline()) {
                g.getProfile().addExited();
            } else {
                g.getPlayer().teleport(this.stage.getSpawn(g.getTeam().get().getColor()));
                g.getPlayer().getInventory().clear();
                g.resetTabName();
                if (won == null) {
                    return;
                } else if (g.getTeam().get().getColor() == won) {
                    g.getProfile().addWonGame();
                } else {
                    g.getProfile().addLostGame();
                }
            }
        }

        this.plugin.getServer().getPluginManager()
                .callEvent(new jp.llv.flaggame.events.GameFinishedEvent(this));
    }

    @Override
    public void stopForcibly(String message) {
        if (this.state != State.STARTED) {
            throw new IllegalStateException();
        }

        this.state = State.FINISHED;

        while (!this.onFinishing.isEmpty()) {
            this.onFinishing.peek().run();
        }

        for (GamePlayer g : this.reception.getPlayers()) {
            g.getProfile().decreasePlayed();
            if (g.getPlayer().isOnline()) {
                g.getPlayer().teleport(this.stage.getSpawn(g.getTeam().get().getColor()));
                g.getPlayer().getInventory().clear();
                g.resetTabName();
            }
        }

        GamePlayer.sendMessage(this.reception.getPlayers(), "&2フラッグゲーム'&6" + this.stage.getName() + "&2'は強制終了されました: "
                + message);
    }

    public long getRemainTime() {
        return this.expectedFinishAt - System.currentTimeMillis();
    }

    private void notifyRemainTime() {
        GamePlayer.sendMessage(this.getReception().getPlayers(), "&aゲーム終了まであと " + ConvertUtils.format(this.getRemainTime()) + "秒です!");
    }

    /*package*/ void addKillCount(GamePlayer player) {
        this.killCount.increase(player.getTeam().get().getColor());
        this.personalKillCount.increase(player);
    }

    public int getKillCount(TeamColor color) {
        return this.killCount.getOrZero(color);
    }

    public int getKillCount(GamePlayer color) {
        return this.personalKillCount.getOrZero(color);
    }

    /*package*/ void addDeathCount(GamePlayer player) {
        this.deathCount.increase(player.getTeam().get().getColor());
        this.personalDeathCount.increase(player);
    }

    public int getDeathCount(TeamColor color) {
        return this.deathCount.getOrZero(color);
    }

    public int getDeathCount(GamePlayer color) {
        return this.personalDeathCount.getOrZero(color);
    }

    @Override
    public Collection<Team> getTeams() {
        return this.teams.values();
    }

    @Override
    public Team getTeam(TeamColor color) {
        return this.teams.get(color);
    }

    @Override
    public GameReception getReception() {
        return this.reception;
    }

    @Override
    public Stage getStage() {
        return this.stage;
    }

    @Override
    public State getState() {
        return this.state;
    }

    public Team getWonTeam() {
        return this.wonTeam;
    }

}
