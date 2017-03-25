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
package jp.llv.flaggame.game.basic;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import jp.llv.flaggame.events.GameStartEvent;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.game.HitpointTask;
import jp.llv.flaggame.game.basic.objective.HeldBanner;
import jp.llv.flaggame.game.protection.StageProtectionListener;
import jp.llv.flaggame.profile.DeviationBasedExpCalcurator;
import jp.llv.flaggame.profile.record.FlagCaptureRecord;
import jp.llv.flaggame.profile.record.FlagScoreRecord;
import jp.llv.flaggame.profile.record.GameStartRecord;
import jp.llv.flaggame.profile.record.PlayerKillRecord;
import jp.llv.flaggame.profile.record.PlayerRecord;
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
import org.bukkit.Sound;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import syam.flaggame.FlagGame;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.util.StreamUtil;
import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;
import jp.llv.flaggame.profile.ExpCalcurator;
import jp.llv.flaggame.profile.record.BannerKeepRecord;
import jp.llv.flaggame.rollback.RollbackException;
import syam.flaggame.game.AreaInfo;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author Toyblocks
 */
public class BasicGame implements Game {

    private final FlagGame plugin;
    private final GameReception reception;
    private final Stage stage;
    private final Map<TeamColor, Team> teams;
    private final Map<GamePlayer, HeldBanner> heldBanners = new HashMap<>();

    private final Queue<Runnable> onFinishing = new LinkedList<>();
    private final Set<BossBar> bossbars = new HashSet<>();

    private State state = State.PREPARATION;
    private long expectedFinishAt = 0;

    private final BGRecordStream records;
    private final ExpCalcurator expCalcurator = new DeviationBasedExpCalcurator();

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
        this.records = new BGRecordStream(plugin, this);
    }

    public BasicGame(FlagGame plugin, GameReception reception, Stage stage, Team... teams) {
        this(plugin, reception, stage, Arrays.asList(teams));
    }

    public UUID getID() {
        return this.reception.getID();
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
        if (this.state != State.PREPARATION) {
            throw new IllegalStateException();
        }
        this.state = State.STARTED;
        this.getRecordStream().push(new GameStartRecord(this.getID(), this.stage.getName()));

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
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/flag watch " + this.stage.getName()))
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

        // stage rollback
        this.stage.getAreas().getStageArea().getPos1().getWorld().getEntities().stream()
                .filter(e -> e instanceof Item)
                .filter(e -> this.stage.getAreas().getStageArea().contains(e.getLocation()))
                .forEach(Entity::remove);
        for (String areaID : stage.getAreas().getAreas()) {
            Cuboid area = stage.getAreas().getArea(areaID);
            AreaInfo info = stage.getAreas().getAreaInfo(areaID);
            for (AreaInfo.RollbackData rollback : info.getRollbacks().values()) {
                if (rollback.getTiming() == 0) {
                    try {
                        rollback.getTarget().deserialize(stage, area, rollback.getData());
                    } catch (RollbackException ex) {
                        plugin.getLogger().log(Level.WARNING, "Failed to rollback", ex);
                        this.stopForcibly("Failed to rollback");
                        return;
                    }
                } else {
                    BukkitTask rollbackTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        try {
                            rollback.getTarget().deserialize(stage, area, rollback.getData());
                        } catch (RollbackException ex) {
                            plugin.getLogger().log(Level.WARNING, "Failed to rollback", ex);
                            this.stopForcibly("Failed to rollback");
                        }
                    }, rollback.getTiming());
                    onFinishing.offer(rollbackTask::cancel);
                }
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

        StageProtectionListener protectionListener = new StageProtectionListener(plugin, this);
        this.plugin.getServer().getPluginManager().registerEvents(protectionListener, this.plugin);
        this.onFinishing.offer(protectionListener::unregister);

        this.getTeams().stream().forEach(team -> {
            String title = "\u00A76FlagGame ~\u00A7b" + this.stage.getName() + "\u00A76";
            BossBar bossbar = this.plugin.getServer().createBossBar(title, team.getColor().getBarColor(), BarStyle.SEGMENTED_20, new BarFlag[0]);
            this.bossbars.add(bossbar);
            for (GamePlayer player : team) {
                bossbar.addPlayer(player.getPlayer());
            }
            bossbar.setVisible(true);
        });

        BukkitTask stopTask = this.plugin.getServer().getScheduler()
                .runTaskLater(plugin, this::stop, ConvertUtils.toTick(this.stage.getGameTime()));
        this.onFinishing.offer(stopTask::cancel);

        BukkitTask updateTask = this.plugin.getServer().getScheduler()
                .runTaskTimer(plugin, this::updateRemainTime, 20L, 20L);
        this.onFinishing.offer(updateTask::cancel);

        BukkitTask hitpointTask = new HitpointTask(this).runTaskTimer(plugin, 10L, 10L);
        this.onFinishing.offer(hitpointTask::cancel);

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

        GamePlayer.sendTitle(this, "&6試合開始", stage.getGuide(), 0, 60, 20);
    }

    private void stop() {
        if (this.state != State.STARTED) {
            throw new IllegalStateException();
        }
        this.state = State.FINISHED;
        while (!this.onFinishing.isEmpty()) {
            this.onFinishing.poll().run();
        }

        // calcurate flag score
        this.stage.getFlags().entrySet().stream()
                .filter(e -> e.getValue().getOwner() != null)
                .map(e -> new FlagScoreRecord(
                        this.getID(),
                        this.getRecordStream().stream(FlagCaptureRecord.class).filter(StreamUtil.isLocatedIn(e.getKey())).reduce(StreamUtil.toLastElement()).get().getPlayer(),
                        e.getValue().getLocation(),
                        e.getValue().getFlagPoint()
                )).forEach(this.getRecordStream()::push);
        // calcurate banner score
        if (stage.getBannerSlots().isEmpty()) {
            heldBanners.entrySet().stream().forEach(e -> getRecordStream().push(
                    new BannerKeepRecord(getID(), e.getKey().getPlayer(), e.getValue().getPoint())));
        }

        // game point (a player)
        Map<GamePlayer, Double> points = this.getRecordStream().groupingBy(
                PlayerRecord.class, r -> getPlayer(r.getPlayer()),
                Collectors.summingDouble(t -> t.getGamePoint())
        );
        OptionalDouble maxPoint = points.entrySet().stream().mapToDouble(Map.Entry::getValue).max();
        Set<GamePlayer> maxPointers = MapUtils.getKeyByValue(points, maxPoint.orElse(Double.NaN));
        // game point (a team)
        Map<TeamColor, Double> teamPoints = MapUtils.remap(points,
                p -> p.getTeam().get().getColor(),
                Collectors.summingDouble(d -> d)
        );
        OptionalDouble maxTeamPoint = teamPoints.entrySet().stream().mapToDouble(Map.Entry::getValue).max();
        Set<TeamColor> winnerTeams = MapUtils.getKeyByValue(teamPoints, maxTeamPoint.orElse(Double.NaN));
        Set<GamePlayer> winnerPlayers = winnerTeams.stream()
                .flatMap(c -> getTeam(c).getPlayers().stream())
                .collect(Collectors.toSet());
        // condition point
        Map<GamePlayer, Double> conditions = this.getRecordStream().groupingBy(
                PlayerRecord.class, r -> getPlayer(r.getPlayer()),
                Collectors.summingDouble(t -> t.getExp(plugin.getConfigs()))
        ).entrySet().stream()
                .collect(StreamUtil.deviation(Map.Entry::getValue, (e, v) -> MapUtils.tuple(e.getKey(), v)))
                .collect(StreamUtil.toMap());
        // experience point
        Map<GamePlayer, Double> exps = conditions.entrySet().stream()
                .map(expCalcurator.calcurate(winnerPlayers, this.stage.getGameTime(), this.reception.size()))
                .collect(StreamUtil.toMap());
        OptionalDouble maxExp = exps.entrySet().stream().mapToDouble(Map.Entry::getValue).max();
        // kill point
        Map<GamePlayer, Long> kills = this.getRecordStream().groupingBy(
                PlayerKillRecord.class, r -> getPlayer(r.getPlayer()),
                Collectors.counting()
        );
        OptionalLong maxKills = kills.entrySet().stream().mapToLong(Map.Entry::getValue).max();
        Set<GamePlayer> maxKillers = MapUtils.getKeyByValue(kills, maxKills.orElse(Long.MIN_VALUE));
        // capture point
        Map<GamePlayer, Double> captures = this.getRecordStream().groupingBy(
                FlagCaptureRecord.class, r -> getPlayer(r.getPlayer()),
                Collectors.summingDouble(t -> t.getExp(plugin.getConfigs()))
        );
        OptionalDouble maxCaptures = captures.entrySet().stream().mapToDouble(Map.Entry::getValue).max();
        Set<GamePlayer> maxCapturers = MapUtils.getKeyByValue(captures, maxCaptures.orElse(Double.NaN));

        GamePlayer.sendMessage(this.plugin.getPlayers(), "&2フラッグゲーム'&6" + this.stage.getName() + "&2'が終わりました!");
        GamePlayer.sendMessage(this.plugin.getPlayers(), teamPoints.entrySet().stream()
                .map(e -> e.getKey().getRichName() + "得点: &6" + e.getValue() + e.getKey().getColor() + "点")
                .collect(Collectors.joining(", "))
        );
        if (winnerTeams.isEmpty()) {
            GamePlayer.sendMessage(this.plugin.getPlayers(), "このゲームは引き分けです!");
        } else {
            GamePlayer.sendMessage(this.plugin.getPlayers(), winnerTeams.stream()
                    .map(TeamColor::getRichName)
                    .collect(Collectors.joining(", ")) + "の勝利です！"
            );
        }

        if (!maxKillers.isEmpty()) {
            GamePlayer.sendMessage(this.plugin.getPlayers(),
                    "&6戦闘狂: "
                    + maxKillers.stream().map(GamePlayer::getColoredName).collect(Collectors.joining(", "))
                    + "(&6" + maxKills.getAsLong() + "kills&f)"
            );
        }
        if (!maxCapturers.isEmpty()) {
            GamePlayer.sendMessage(this.plugin.getPlayers(),
                    "&6戦略家: "
                    + maxCapturers.stream().map(GamePlayer::getColoredName).collect(Collectors.joining(", "))
                    + "(&6" + maxCaptures.getAsDouble() + "exp&f)"
            );
        }
        if (!maxPointers.isEmpty()) {
            GamePlayer.sendMessage(this.plugin.getPlayers(),
                    "&6稼ぎ頭: "
                    + maxPointers.stream().map(GamePlayer::getColoredName).collect(Collectors.joining(", "))
                    + "(&6" + maxPoint.getAsDouble() + "points&f)"
            );
        }

        points.entrySet().forEach(e -> e.getKey().sendMessage("&aあなたの獲得得点(β): &6" + e.getValue()));
        exps.entrySet().forEach(e -> e.getKey().sendMessage("&aあなたの経験値(β): &6" + e.getValue()));

        this.bossbars.stream().forEach(BossBar::removeAll);

        this.plugin.getServer().getPluginManager()
                .callEvent(new jp.llv.flaggame.events.GameFinishedEvent(this));

        for (GamePlayer g : this.reception.getPlayers()) {
            if (g.getPlayer().isOnline()) {
                g.getPlayer().teleport(this.stage.getSpawn(g.getTeam().get().getColor()));
                g.getPlayer().getInventory().clear();
                g.resetTabName();
            }
        }
        
        String author = "".equals(stage.getAuthor()) ? "" : "presented by " + stage.getAuthor();
        GamePlayer.sendTitle(this, "&6試合終了", author, 0, 60, 20);
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
        GamePlayer.playSound(this.getReception().getPlayers(), Sound.BLOCK_NOTE_PLING);
    }

    private void updateRemainTime() {
        double progress = ((double) this.getRemainTime()) / this.stage.getGameTime();
        this.bossbars.forEach(b -> b.setProgress(progress));
    }

    @Override
    public Collection<Team> getTeams() {
        return this.teams.values();
    }

    @Override
    public Team getTeam(TeamColor color) {
        return this.teams.get(color);
    }

    public void setBannerHeld(GamePlayer player, HeldBanner banner) {
        if (this.heldBanners.containsKey(player)) {
            throw new IllegalStateException("Held banner duplication");
        }
        this.heldBanners.put(player, banner);
    }

    public Optional<HeldBanner> getBannerHeld(GamePlayer player) {
        if (this.heldBanners.containsKey(player)) {
            return Optional.of(heldBanners.get(player));
        } else {
            return Optional.empty();
        }
    }

    public void clearBannerHeld(GamePlayer player) {
        this.heldBanners.remove(player);
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

    public BGRecordStream getRecordStream() {
        return this.records;
    }

    private GamePlayer getPlayer(UUID uuid) {
        return this.plugin.getPlayers().getPlayer(uuid);
    }

}
