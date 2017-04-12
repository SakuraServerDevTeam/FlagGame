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
package jp.llv.flaggame.reception;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.game.Game;
import jp.llv.flaggame.profile.GameRecordStream;
import jp.llv.flaggame.profile.RecordStream;
import jp.llv.flaggame.profile.record.PlayerWinRecord;
import jp.llv.flaggame.profile.record.ReceptionCloseRecord;
import jp.llv.flaggame.profile.record.ReceptionOpenRecord;
import jp.llv.flaggame.rollback.SerializeTask;
import jp.llv.flaggame.util.ConvertUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.CommandException;
import syam.flaggame.exception.StageReservedException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.player.GamePlayer;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 * @param <G> game type this reception manage
 */
public abstract class SingleGameReception<G extends Game> implements GameReception {

    protected final FlagGame plugin;
    protected final UUID id;
    private State state = State.READY;

    private RecordStream records;

    protected G game;
    protected final Stage stage;
    private Stage.Reservation stageReservation;
    private SerializeTask initialTask;

    public SingleGameReception(FlagGame plugin, UUID id, List<String> args) {
        this.plugin = plugin;
        this.id = id;
        if (args.size() < 1) {
            throw new IllegalArgumentException("The first argument must be a stage name");
        }
        this.stage = plugin.getStages().getStage(args.get(0))
                .orElseThrow(() -> new IllegalArgumentException("No such stage"));
        this.records = new GameRecordStream(id);
    }

    @Override
    public UUID getID() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.stage.getName();
    }

    @Override
    public Optional<? extends Game> getGame(GamePlayer player) {
        return Optional.ofNullable(game);
    }

    @Override
    public Collection<? extends Game> getGames() {
        return game == null ? Collections.emptySet() : Collections.singleton(game);
    }

    @Override
    public State getState() {
        //まずゲームと状態を同期
        if (this.state == State.STARTING
            && this.game.getState() != Game.State.PREPARATION) {
            this.state = State.STARTED;
        }
        if (this.state == State.STARTED && this.game.getState() == Game.State.FINISHED) {
            this.state = State.FINISHED;
        }
        return this.state;
    }

    protected void setState(State state) {
        if (this.state.ordinal() >= state.ordinal()) {
            throw new IllegalStateException("This reception is already state; " + this.state);
        }
        this.state = state;
    }

    @Override
    public Iterator<GamePlayer> iterator() {
        return this.getPlayers().iterator();
    }

    @Override
    public RecordStream getRecordStream() {
        return this.records;
    }

    protected boolean isStageInitialized() {
        return initialTask != null && initialTask.isFinished();
    }

    @Override
    public void open(List<String> args) throws CommandException {
        if (this.getState() != State.READY) {
            throw new CommandException("&cこの募集は既に開始されました!");
        }

        try {
            this.stage.validate();
        } catch (NullPointerException ex) {
            throw new CommandException("&cそのステージは設定が無効です!");
        }

        try {
            this.stageReservation = stage.reserve(this);
        } catch (StageReservedException ex) {
            throw new CommandException("&cそのステージは既に使用中です!", ex);
        }

        initialTask = stage.getInitialTask(plugin, ex -> {
            plugin.getLogger().log(Level.WARNING, "Failed to initialize the stage", ex);
        });
        String etr = Actions.getTimeString(ConvertUtils.toMiliseconds(initialTask.getEstimatedTickRemaining()));
        GamePlayer.sendMessage(plugin.getPlayers(), "&a'&6" + stage.getName() + "&a'をロードしています...");
        GamePlayer.sendMessage(plugin.getPlayers(), "&aこれにはおよそ" + etr + "間かかる予定です");
        initialTask.start(plugin);

        // 賞金系メッセージ
        String entryFeeMsg = String.valueOf(stage.getEntryFee()) + "Coin";
        String awardMsg = String.valueOf(stage.getPrize()) + "Coin";
        if (stage.getEntryFee() <= 0) {
            entryFeeMsg = "&7FREE!";
        }
        if (stage.getPrize() <= 0) {
            awardMsg = "&7なし";
        }

        this.state = State.OPENED;
        this.records.push(new ReceptionOpenRecord(this.id));
        GamePlayer.sendMessage(this.plugin.getPlayers(), "&2フラッグゲーム'&6" + this.getName() + "&2'の参加受付が開始されました！");
        GamePlayer.sendMessage(this.plugin.getPlayers(), "&2 参加料:&6 " + entryFeeMsg + "&2   賞金:&6 " + awardMsg);
        BaseComponent[] message = new ComponentBuilder("ここをクリック").bold(true).color(ChatColor.GOLD).bold(false)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("クリックして参加申し込みします").color(ChatColor.GOLD).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/flag game join " + this.getID()))
                .append("して参加してください！").color(ChatColor.DARK_GREEN).create();
        GamePlayer.sendMessage(this.plugin.getPlayers(), message);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void close(String reason) {
        if (getState().toGameState() != Game.State.FINISHED) {
            this.stop(reason);
        }

        if (initialTask != null) {
            initialTask.cancel();
        }

        for (GamePlayer p : getPlayers()) {
            p.getTeam().ifPresent(t -> t.remove(p));
            p.leave(this);
        }

        this.plugin.getReceptions().remove(this);
        this.state = State.CLOSED;
        this.plugin.getServer().getPluginManager()
                .callEvent(new jp.llv.flaggame.events.ReceptionClosedEvent(reason, this));
        this.records.push(new ReceptionCloseRecord(this.id));
        plugin.getDatabases().get().saveReocrds(records, result -> {
            try {
                result.test();
                plugin.getProfiles().loadPlayerProfiles(this, true);
                plugin.getProfiles().loadStageProfile(stage);
            } catch (DatabaseException ex) {
                plugin.getLogger().log(Level.WARNING, "Failed to save records", ex);
            }
        });
        this.records = null;
    }

    @Override
    public abstract void leave(GamePlayer player);

    @Override
    public abstract void join(GamePlayer player, List<String> args) throws CommandException;

    @Override
    public abstract void start(List<String> args) throws CommandException;

    @Override
    public void stop(String reason) throws IllegalStateException {
        if (this.getState() == State.STARTED) {
            this.game.stopForcibly(reason);
            this.state = State.FINISHED;
        }
        if (stageReservation != null) {
            stageReservation.release();
            stageReservation = null;
        }

        this.records.stream(PlayerWinRecord.class)
                .map(PlayerWinRecord::getPlayer)
                .forEach(uuid -> {
                    GamePlayer player = plugin.getPlayers().getPlayer(uuid);
                    if (player == null || stage.getPrize() <= 0.0) {
                        return;
                    }
                    if (Actions.addMoney(uuid, stage.getPrize())) {
                        player.sendMessage("&a[+]おめでとうございます！賞金として" + Actions.formatMoney(stage.getPrize()) + "Coinを得ました！");
                    } else {
                        player.sendMessage("&c報酬受け取りにエラーが発生しました。管理人までご連絡ください。");
                    }
                });
        if (!plugin.getDatabases().isPresent()) {
            return;
        }
        plugin.getDatabases().get().saveReocrds(records, result -> {
            try {
                result.test();
                plugin.getProfiles().loadPlayerProfiles(this, true);
                plugin.getProfiles().loadStageProfile(stage);
            } catch (DatabaseException ex) {
                plugin.getLogger().log(Level.WARNING, "Failed to save records", ex);
            }
        });
        records = new GameRecordStream(id);

        String rateCommand = "/flaggame:flag stage rate ";
        BaseComponent[] rateMessage = new ComponentBuilder("クリックでステージの評価にご協力ください: ").color(ChatColor.GOLD)
                .append("❤").color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, rateCommand + 0))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("1.根本的問題がある").color(ChatColor.RED).create()))
                .append("❤").color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, rateCommand + 1))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("2.多くの問題がある").color(ChatColor.RED).create()))
                .append("❤").color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, rateCommand + 2))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("3.改善の余地がある").color(ChatColor.GOLD).create()))
                .append("❤").color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, rateCommand + 3))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("4.個人的に楽しめた").color(ChatColor.GOLD).create()))
                .append("❤").color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, rateCommand + 4))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("5.チームで楽しめた").color(ChatColor.GREEN).create()))
                .append("❤").color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, rateCommand + 5))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("6.全員が楽しめた").color(ChatColor.GREEN).create()))
                .create();
        GamePlayer.sendMessage(
                getPlayers().stream().filter(g -> g.isOnline())
                .filter(g -> Perms.STAGE_RATE.has(g.getPlayer()))
                .collect(Collectors.toSet()),
                rateMessage
        );
    }

}
