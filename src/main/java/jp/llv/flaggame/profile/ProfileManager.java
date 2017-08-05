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
package jp.llv.flaggame.profile;

import jp.llv.flaggame.api.profile.ProfileAPI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.player.GamePlayer;
import jp.llv.flaggame.api.stage.Stage;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.events.GamePlayerUnloadEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import jp.llv.flaggame.util.OnelineBuilder;

/**
 *
 * @author SakuraServerDev
 */
public class ProfileManager implements Listener, ProfileAPI {

    private final FlagGameAPI api;
    private final Map<UUID, CachedPlayerProfile> playerProfiles = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, CachedStageProfile> stageProfiles = Collections.synchronizedMap(new HashMap<>());

    public ProfileManager(FlagGameAPI api) {
        this.api = api;
        api.getServer().getPluginManager().registerEvents(this, api.getPlugin());
        api.getServer().getOnlinePlayers().forEach(p -> getProfile(p.getUniqueId()));
    }

    @Override
    public CachedPlayerProfile getProfile(UUID uuid) {
        Objects.requireNonNull(uuid);
        CachedPlayerProfile profile = playerProfiles.get(uuid);
        if (profile == null) {
            playerProfiles.put(uuid, profile = new CachedPlayerProfile());
        }
        return profile;
    }

    @Override
    public CachedStageProfile getProfile(String name) {
        Objects.requireNonNull(name);
        CachedStageProfile profile = stageProfiles.get(name);
        if (profile == null) {
            stageProfiles.put(name, profile = new CachedStageProfile());
        }
        return profile;
    }

    @Override
    public void loadPlayerProfile(UUID uuid, boolean notifyLevelUp) {
        api.getDatabase().ifPresent(database -> {
            CachedPlayerProfile profile;
            int oldLevel;
            if (playerProfiles.containsKey(uuid)) {
                profile = playerProfiles.get(uuid);
                oldLevel = notifyLevelUp ? profile.getLevel().orElse(Integer.MAX_VALUE) : Integer.MAX_VALUE;
            } else {
                playerProfiles.put(uuid, profile = new CachedPlayerProfile());
                oldLevel = Integer.MAX_VALUE;
            }
            database.loadPlayerStat(uuid, result -> {
                profile.setStat(result.get().getKey(), result.get().getValue());
            }, result -> {
                try {
                    result.test();
                } catch (DatabaseException ex) {
                    api.getLogger().warn("Failed to load player stats", ex);
                }
            });
            database.loadPlayerExp(uuid, result -> {
                try {
                    Long value = result.get();
                    profile.setExp(value == null ? 0 : value);
                    int newLevel = profile.getLevel().orElse(Integer.MIN_VALUE);
                    Player player = api.getServer().getPlayer(uuid);
                    if (oldLevel < newLevel && player != null) {
                        OnelineBuilder.newBuilder().info("あなたのレベルが")
                                .value(oldLevel).info("から")
                                .value(newLevel).info("まで上がりました！ 次のレベルまでの必要経験値は")
                                .value(profile.getExpRequiredToLevelUp().getAsLong()).info("です！")
                                .sendTo(player);
                    }
                } catch (DatabaseException | NullPointerException ex) {
                    api.getLogger().warn("Failed to load player exp", ex);
                }
            });
            database.loadPlayerVibe(uuid, result -> {
                try {
                    Double value = result.get();
                    profile.setVibe(value == null ? 0 : value);
                } catch (DatabaseException | NullPointerException ex) {
                    api.getLogger().warn("Failed to load player vibe", ex);
                }
            });
        });
    }

    @Override
    public void loadPlayerProfiles(Iterable<? extends GamePlayer> players, boolean notifyLevelUp) {
        for (GamePlayer player : players) {
            loadPlayerProfile(player.getUUID(), notifyLevelUp);
        }
    }

    @Override
    public void loadStageProfile(String stage) {
        api.getDatabase().ifPresent(database -> {
            if (!stageProfiles.containsKey(stage)) {
                stageProfiles.put(stage, new CachedStageProfile());
            }
            CachedStageProfile profile = stageProfiles.get(stage);
            database.loadStageStat(stage, result -> {
                profile.setStat(result.get().getKey(), result.get().getValue());
            }, result -> {
                try {
                    result.test();
                } catch (DatabaseException ex) {
                    api.getLogger().warn("Failed to load stage stats", ex);
                }
            });
        });
    }

    @Override
    public void loadStageProfile(Stage stage) {
        loadStageProfile(stage.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void on(PlayerJoinEvent event) {
        loadPlayerProfile(event.getPlayer().getUniqueId(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void on(GamePlayerUnloadEvent event) {
        playerProfiles.remove(event.getPlayer().getUUID());
    }

}
