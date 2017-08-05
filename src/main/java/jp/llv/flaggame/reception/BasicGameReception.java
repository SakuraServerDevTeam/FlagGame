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
package jp.llv.flaggame.reception;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.reception.Reception;
import jp.llv.flaggame.api.reception.Teaming;
import jp.llv.flaggame.api.session.Reservable;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.api.game.Game;
import jp.llv.flaggame.game.basic.BasicGame;
import jp.llv.flaggame.profile.GameRecordStream;
import jp.llv.flaggame.profile.RecordStream;
import jp.llv.flaggame.profile.record.PlayerEntryRecord;
import jp.llv.flaggame.profile.record.PlayerLeaveRecord;
import jp.llv.flaggame.profile.record.PlayerTeamRecord;
import jp.llv.flaggame.profile.record.PlayerWinRecord;
import jp.llv.flaggame.profile.record.ReceptionCloseRecord;
import jp.llv.flaggame.profile.record.ReceptionOpenRecord;
import jp.llv.flaggame.api.stage.rollback.SerializeTask;
import jp.llv.flaggame.util.ConvertUtils;
import jp.llv.flaggame.util.OptionSet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.exception.InvalidOptionException;
import syam.flaggame.permission.Perms;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.game.basic.BGRecordStream;
import syam.flaggame.util.Actions;

/**
 * Reception for teaming in a single game.
 *
 * @author toyblocks
 */
public class BasicGameReception implements Reception {

    private final FlagGameAPI api;
    private final UUID id;

    private State state = State.READY;
    private RecordStream records;
    private final Set<GamePlayer> teamJoinRecorded = new HashSet<>();
    private Teaming teaming;
    private BasicGame game;

    private Stage stage;
    private Reservable.Reservation<Stage> reservation;
    private SerializeTask initialTask;

    public BasicGameReception(FlagGameAPI api, UUID id) {
        this.api = api;
        this.id = id;
    }

    @Override
    public void open(OptionSet options) throws FlagGameException {
        if (this.getState() != State.READY) {
            throw new CommandException("&cこの募集は既に開始されました!");
        }

        try {
            this.stage = api.getStages().getRandomAvailableStage(options);
        } catch (InvalidOptionException ex) {
            throw new CommandException("&c無効なオプションが設定されています！", ex);
        }

        TeamType[] teams = stage.getSpawns().keySet().toArray(new TeamColor[stage.getSpawns().size()]);
        if (options.isPresent("t")) {
            teaming = api.getRegistry().getTeaming(options.getString("t")).apply(api, teams);
        } else {
            teaming = api.getRegistry().getDefaultTeaming().apply(api, teams);
        }

        this.reservation = stage.reserve(this);

        initialTask = stage.getInitialTask(api.getPlugin(), ex -> {
            api.getLogger().warn("Failed to initialize the stage", ex);
        });
        String etr = Actions.getTimeString(ConvertUtils.toMiliseconds(initialTask.getEstimatedTickRemaining()));
        GamePlayer.sendMessage(api.getPlayers(), "&a'&6" + stage.getName() + "&a'をロードしています...");
        GamePlayer.sendMessage(api.getPlayers(), "&aこれにはおよそ" + etr + "間かかる予定です");
        initialTask.start(api.getPlugin());

        // 賞金系メッセージ
        String entryFeeMsg = Actions.formatMoney(stage.getEntryFee());
        String awardMsg = Actions.formatMoney(stage.getPrize());
        if (stage.getEntryFee() <= 0) {
            entryFeeMsg = "&7FREE!";
        }
        if (stage.getPrize() <= 0) {
            awardMsg = "&7なし";
        }

        this.state = State.OPENED;
        this.records = new BGRecordStream(api, game, new GameRecordStream(id));
        this.records.push(new ReceptionOpenRecord(this.id));
        GamePlayer.sendMessage(api.getPlayers(), "&2フラッグゲーム'&6" + this.getName() + "&2'の参加受付が開始されました！");
        GamePlayer.sendMessage(api.getPlayers(), "&2 参加料:&6 " + entryFeeMsg + "&2   賞金:&6 " + awardMsg);
        BaseComponent[] message = new ComponentBuilder("ここをクリック").bold(true).color(ChatColor.GOLD).bold(false)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("クリックして参加申し込みします").color(ChatColor.GOLD).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/flag game join " + this.getID()))
                .append("して参加してください！").color(ChatColor.DARK_GREEN).create();
        GamePlayer.sendMessage(api.getPlayers(), message);
    }

    @Override
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

        this.state = State.CLOSED;
        api.getReceptions().remove(this);
        api.getServer().getPluginManager()
                .callEvent(new jp.llv.flaggame.events.ReceptionClosedEvent(reason, this));
        this.records.push(new ReceptionCloseRecord(this.id));
        if (!api.getDatabase().isPresent()) {
            api.getLogger().warn("Failed to save records");
        } else {
            api.getDatabase().get().saveReocrds(records, result -> {
                try {
                    result.test();
                    api.getProfiles().loadPlayerProfiles(this, true);
                    api.getProfiles().loadStageProfile(stage);
                } catch (DatabaseException ex) {
                    api.getLogger().warn("Failed to save records", ex);
                }
            });
        }
        this.records = null;
    }

    @Override
    public void join(GamePlayer player, OptionSet options) throws FlagGameException {
        if (this.getState() != State.OPENED) {
            throw new CommandException("&cこの募集は既に終了しました!");
        }

        double cost = stage.getEntryFee();
        // 参加料チェック
        if (cost > 0.0 && !Actions.checkMoney(player.getUUID(), cost)) {
            throw new CommandException("&c参加するためには参加料 " + Actions.formatMoney(cost) + " が必要です！");
        } else if (cost > 0.0 && Actions.takeMoney(player.getUUID(), cost)) {
            player.sendMessage("&c参加料として " + Actions.formatMoney(cost) + " を支払いました！");
        }


        long level = api.getProfiles().getProfile(player.getUUID()).getLevel().orElse(0);
        int count = teaming.size() + 1;
        Optional<TeamType> type = teaming.join(player);
        
        player.join(this, options);
        getRecordStream().push(new PlayerEntryRecord(id, player.getPlayer()));
        if (type.isPresent()) {
            GamePlayer.sendMessage(api.getPlayers(),
                    type.get().toColor().getChatColor() + player.getName() + "&7(Lv" + level + ")&aが'&6"
                    + this.getName() + "&a'で開催予定のゲームに参加しました(&6" + count + "人目&a)"
            );
            getRecordStream().push(new PlayerTeamRecord(id, player.getPlayer(), type.get().toColor()));
            teamJoinRecorded.add(player);
        } else {
            GamePlayer.sendMessage(api.getPlayers(),
                    player.getName() + "&7(Lv" + level + ")&aが'&6"
                    + this.getName() + "&a'で開催予定のゲームに参加しました(&6" + count + "人目&a)"
            );
        }
    }

    @Override
    public void leave(GamePlayer player) {
        switch (getState()) {
            case READY:
            case STARTING:
            case STARTED:
            case CLOSED:
                throw new IllegalStateException();
            case OPENED:
                teaming.leave(player);
                break;
            case FINISHED:
        }
        teaming.leave(player);
        player.leave(this);
        if (getState().toGameState() != Game.State.FINISHED) {
            getRecordStream().push(new PlayerLeaveRecord(id, player.getPlayer()));
            GamePlayer.sendMessage(api.getPlayers(), player.getColoredName() + "&aが'" + this.getName() + "'で開催予定のゲームへのエントリーを取り消しました");
        }
    }

    @Override
    public void start(OptionSet options) throws FlagGameException {
        if (this.getState() != State.OPENED) {
            throw new IllegalStateException();
        }

        if (initialTask != null && !initialTask.isFinished()) {
            throw new CommandException("&cステージの初期化が完了していません！");
        }

        //Build teams
        Set<Team> teams = new HashSet<>();
        for (Map.Entry<TeamType, ? extends Collection<GamePlayer>> e : this.teaming.build().entrySet()) {
            teams.add(new Team(this, e.getKey(), e.getValue()));
            for (GamePlayer player : e.getValue()) {
                if (!teamJoinRecorded.contains(player)) {
                    getRecordStream().push(new PlayerTeamRecord(id, player.getPlayer(), e.getKey().toColor()));
                }
            }
        }
        //start game
        try {
            this.game = new BasicGame(api, this, this.stage, teams);
            this.game.startLater(10000L);
        } catch (CommandException ex) {
            this.game = null;
            throw ex;
        }

        // successfully started
        setState(State.STARTING);
        GamePlayer.sendMessage(api.getPlayers(), "&2フラッグゲーム'&6" + this.getName() + "&2'の参加受付が終了しました！");
    }

    @Override
    public void stop(String reason) throws IllegalStateException {
        if (this.getState() == State.STARTED) {
            this.game.stopForcibly(reason);
            this.state = State.FINISHED;
        }
        if (reservation != null) {
            reservation.release();
            reservation = null;
        }

        this.records.stream(PlayerWinRecord.class)
                .map(PlayerWinRecord::getPlayer)
                .forEach(uuid -> {
                    GamePlayer player = api.getPlayers().getPlayer(uuid);
                    if (player == null || stage.getPrize() <= 0.0) {
                        return;
                    }
                    if (Actions.addMoney(uuid, stage.getPrize())) {
                        player.sendMessage("&a[+]おめでとうございます！賞金として" + Actions.formatMoney(stage.getPrize()) + "を得ました！");
                    } else {
                        player.sendMessage("&c報酬受け取りにエラーが発生しました。管理人までご連絡ください。");
                    }
                });
        if (!api.getDatabase().isPresent()) {
            return;
        }
        api.getDatabase().get().saveReocrds(records, result -> {
            try {
                result.test();
                api.getProfiles().loadPlayerProfiles(this, true);
                api.getProfiles().loadStageProfile(stage);
            } catch (DatabaseException ex) {
                api.getLogger().warn("Failed to save records", ex);
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

    @Override
    public Optional<? extends Game> getGame(GamePlayer player) {
        return Optional.ofNullable(game);
    }

    @Override
    public Collection<? extends Game> getGames() {
        return game == null ? Collections.emptySet() : Collections.singleton(game);
    }

    @Override
    public UUID getID() {
        return id;
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
            throw new IllegalStateException("This reception is already " + this.state);
        }
        this.state = state;
    }

    @Override
    public Collection<GamePlayer> getPlayers() {
        return teaming == null ? Collections.emptySet() : teaming.getPlayers();
    }

    @Override
    public RecordStream getRecordStream() {
        return records;
    }

    @Override
    public String getName() {
        return stage == null ? id.toString() : stage.getName();
    }

}
