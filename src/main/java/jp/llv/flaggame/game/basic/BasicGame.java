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

import java.sql.SQLException;
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
import jp.llv.flaggame.util.MapUtils;
import jp.llv.flaggame.util.ConvertUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import syam.flaggame.FlagGame;
import syam.flaggame.database.RecordType;
import syam.flaggame.enums.TeamColor;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author Toyblocks
 */
public class BasicGame implements Game {

    private final FlagGame plugin;
    private final GameReception reception;
    private final Stage stage;
    private final Map<TeamColor, Team> teams;
    private final GameProfile profile = new GameProfile();

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
    public void startNow() throws CommandException {
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

        this.state = State.STARTED;
        start();
    }

    @Override
    public void startLater(long ms) throws CommandException {
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

        LongStream.of(10000L, 5000L, 4000L, 3000L, 2000L, 1000L)
                .forEach(r -> {
                    this.plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        GamePlayer.sendMessage(this.reception.getPlayers(),
                                "&2ゲーム'&6" + this.reception.getName() + "&2'が始まるまであと&6" + Actions.getTimeString(r) + "&2です!");
                    }, ConvertUtils.toTick(ms - r));
                });
        this.plugin.getServer().getScheduler().runTaskLater(plugin, this::start, ConvertUtils.toTick(ms));
    }

    private void start() {
        if (this.state == State.PREPARATION) {
            this.state = State.STARTED;
        }

        stage.initialize();

        this.expectedFinishAt = System.currentTimeMillis() + this.stage.getGameTime();

        GamePlayer.sendMessage(this.plugin.getPlayers(),
                "&2ゲーム'&6" + this.reception.getName() + "&2'が始まりました！",
                "&2開催ステージ: '&6" + this.stage.getName() + "&2' &f| &2制限時間: " + Actions.getTimeString(this.stage.getGameTime()),
                this.getTeams().stream()
                .map(t -> t.getColor().getRichName() + "&f" + t.getPlayers().size() + "人&r").collect(Collectors.joining(", "))
        );
        this.stage.getSpecSpawn().ifPresent(l -> {
            BaseComponent[] message = new ComponentBuilder("ここをクリック").bold(true).color(ChatColor.GOLD).bold(false)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("クリックして観戦します").color(ChatColor.GOLD).create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/flag join " + this.reception.getID()))
                    .append("して観戦することができます！").color(ChatColor.DARK_GREEN).create();
            GamePlayer.sendMessage(this.plugin.getPlayers(), message);
        });

        for (Team team : this.getTeams()) {
            Location teamSpawn = this.stage.getSpawn(team.getColor());
            for (GamePlayer player : team) {
                Player vp = player.getPlayer();
                if (!player.getPlayer().isOnline()) {
                    continue;
                }
                vp.teleport(teamSpawn, TeleportCause.PLUGIN);
                vp.setGameMode(GameMode.SURVIVAL);
                vp.setFlying(false);

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
                player.setTabName(team.getColor().getColor() + player.getName());
            }
        }

        this.stage.getStageArea().getPos1().getWorld().getEntities().stream()
                .filter(e -> e instanceof Item)
                .filter(e -> this.stage.getStageArea().isIn(e.getLocation()))
                .forEach(Entity::remove);

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
        LongStream.concat(lessThanAMinute, aMinute).map(t -> this.stage.getGameTime() - t)
                .map(ConvertUtils::toTick).forEach(t -> {
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
            this.onFinishing.poll().run();
        }

        Map<TeamColor, Double> points = new EnumMap<>(TeamColor.class);
        Map<TeamColor, Integer> flagPoints = new EnumMap<>(TeamColor.class);
        Map<TeamColor, Integer> kills = new EnumMap<>(TeamColor.class);
        List<String> msg = new ArrayList<>();

        Map<TeamColor, Map<Byte, Integer>> flagPointsMap = stage.checkFlag();
        for (TeamColor col : this.stage.getSpawns().keySet()) {
            int f = flagPointsMap.get(col).entrySet().stream().mapToInt(e -> e.getKey() * e.getValue()).sum();
            flagPoints.put(col, f);

            int k = (int) this.profile.kill.entrySet().stream().filter(e -> e.getKey().getTeam().get().getColor() == col)
                    .mapToDouble(e -> e.getValue()).sum();
            kills.put(col, k);

            double p = f;
            points.put(col, p);
            msg.add(col.getColor() + col.getTeamName() + "チーム得点: &6" + p + col.getColor() + "点&f(フラッグ: " + f + "点)");
        }

        TeamColor won = null;
        Map<Double, Set<TeamColor>> rPoints = MapUtils.rank(points, (d1, d2) -> Double.compare(d2, d1));
        Map.Entry<Double, Set<TeamColor>> first = rPoints.entrySet().iterator().next();
        if (first.getValue().size() == 1) {
            won = first.getValue().iterator().next();
        }

        Map.Entry<Double, Set<GamePlayer>> topKill
                = MapUtils.rank(this.profile.kill, (i1, i2) -> Double.compare(i2, i1)).entrySet().iterator().next();
        Map.Entry<Double, Set<GamePlayer>> topDeath
                = MapUtils.rank(this.profile.death, (i1, i2) -> Double.compare(i2, i1)).entrySet().iterator().next();
        Map.Entry<Double, Set<GamePlayer>> topCapture
                = MapUtils.rank(this.profile.capture, (i1, i2) -> Double.compare(i2, i1)).entrySet().iterator().next();

        GamePlayer.sendMessage(this.plugin.getPlayers(),
                "&2フラッグゲーム'&6" + this.stage.getName() + "&2'が終わりました!",
                String.join("&f, ", msg),
                won != null
                        ? won.getColor() + won.getTeamName() + "チームの勝利です!"
                        : "このゲームは引き分けです!",
                "&6トップキル: " + (topKill != null ? (topKill.getValue().stream().map(GamePlayer::getColoredName)
                        .collect(Collectors.joining("&f, ")) + "&f (&6" + topKill.getKey() + "Kills&f)") : "&fNone"),
                "&6トップデス: " + (topDeath != null ? (topDeath.getValue().stream().map(GamePlayer::getColoredName)
                        .collect(Collectors.joining("&f, ")) + "&f (&6" + topDeath.getKey() + "Deaths&f)") : "&fNone"),
                "&6トップキャプチャ: " + (topCapture != null ? (topCapture.getValue().stream().map(GamePlayer::getColoredName)
                        .collect(Collectors.joining("&f, ")) + "&f (&6" + topCapture.getKey() + "Points&f)") : "&fNone")
        );

        for (GamePlayer g : this.getReception().getPlayers()) {
            if (!g.getPlayer().isOnline()) {
                this.plugin.getDatabases().ifPresent(db -> {
                    try {
                        db.write(RecordType.EXIT, this.reception.getID(), g.getUUID());
                    } catch (SQLException ex) {
                    }
                });
            } else {
                g.getPlayer().teleport(this.stage.getSpawn(g.getTeam().get().getColor()));
                g.getPlayer().getInventory().clear();
                g.resetTabName();
                if (won == null) {
                    this.plugin.getDatabases().ifPresent(db -> {
                        try {
                            db.write(RecordType.DRAW, this.reception.getID(), g.getUUID());
                        } catch (SQLException ex) {
                        }
                    });
                } else if (g.getTeam().get().getColor() == won) {
                    this.plugin.getDatabases().ifPresent(db -> {
                        try {
                            db.write(RecordType.WIN, this.reception.getID(), g.getUUID());
                        } catch (SQLException ex) {
                        }
                    });
                } else {
                    this.plugin.getDatabases().ifPresent(db -> {
                        try {
                            db.write(RecordType.LOSE, this.reception.getID(), g.getUUID());
                        } catch (SQLException ex) {
                        }
                    });
                }
            }
        }

        this.plugin.getServer().getPluginManager()
                .callEvent(new jp.llv.flaggame.events.GameFinishedEvent(this));

        this.reception.close("The game finished");
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
            if (g.getPlayer().isOnline()) {
                g.getPlayer().teleport(this.stage.getSpawn(g.getTeam().get().getColor()));
                g.getPlayer().getInventory().clear();
                g.resetTabName();
            }
        }

        GamePlayer.sendMessage(this.reception.getPlayers(), "&2フラッグゲーム'&6" + this.stage.getName() + "&2'は強制終了されました: "
                + message);

        this.reception.close("The game finished");
    }

    public long getRemainTime() {
        return this.expectedFinishAt - System.currentTimeMillis();
    }

    private void notifyRemainTime() {
        GamePlayer.sendMessage(this.getReception().getPlayers(), "&aゲーム終了まであと " + Actions.getTimeString(getRemainTime()) + "です!");
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

    protected GameProfile getProfile() {
        return profile;
    }

}
