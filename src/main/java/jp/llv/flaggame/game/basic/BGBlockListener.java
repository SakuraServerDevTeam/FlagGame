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

import java.util.Collection;
import java.util.Optional;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.stage.objective.BannerSlot;
import jp.llv.flaggame.api.stage.objective.BannerSpawner;
import jp.llv.flaggame.reception.Team;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import jp.llv.flaggame.reception.TeamColor;
import jp.llv.flaggame.api.stage.objective.Flag;
import jp.llv.flaggame.api.stage.objective.Nexus;
import jp.llv.flaggame.profile.record.BannerDeployRecord;
import jp.llv.flaggame.profile.record.FlagBreakRecord;
import jp.llv.flaggame.profile.record.FlagCaptureRecord;
import jp.llv.flaggame.profile.record.NexusBreakRecord;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import jp.llv.flaggame.api.player.GamePlayer;

/**
 *
 * @author Toyblocks
 */
public class BGBlockListener extends BGListener {

    private final FlagGameAPI api;
    private final Collection<GamePlayer> players;

    public BGBlockListener(FlagGameAPI api, BasicGame game) {
        super(game);
        this.api = api;
        this.players = game.getReception().getPlayers();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.api.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        Optional<Flag> of = this.game.getStage().getObjective(event.getBlock().getLocation(), Flag.class);
        Optional<Nexus> on = this.game.getStage().getObjective(event.getBlock().getLocation(), Nexus.class);
        Optional<BannerSpawner> ob = this.game.getStage().getObjective(event.getBlock().getLocation(), BannerSpawner.class);

        of.ifPresent(f -> this.breakFlag(gplayer, f, event));
        on.ifPresent(n -> this.breakNexus(gplayer, n, event));
        ob.ifPresent(b -> this.breakBannerSpawner(gplayer, b, event));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GamePlayer gplayer = this.api.getPlayers().getPlayer(player);
        if (!this.players.contains(gplayer)) {
            return;
        }

        Optional<Flag> of = this.game.getStage().getObjective(event.getBlock().getLocation(), Flag.class);
        Optional<BannerSlot> os = this.game.getStage().getObjective(event.getBlock().getLocation(), BannerSlot.class);

        of.ifPresent(f -> this.placeFlag(gplayer, f, event));
        os.ifPresent(s -> this.placeBanner(gplayer, s, event));
    }

    private void placeFlag(GamePlayer gplayer, Flag f, BlockPlaceEvent event) {
        Block b = event.getBlockPlaced();
        if (!Flag.isFlag(b.getType())) {//対象ブロックでない
            event.setCancelled(true);
            return;
        }

        Team placerTeam = gplayer.getTeam().get();
        @SuppressWarnings("deprecation")
        TeamColor placedTeamColor = TeamColor.getByColorData(b.getData());

        if (placedTeamColor != placerTeam.getType()) {
            gplayer.sendMessage(ChatMessageType.ACTION_BAR, "&c味方チーム以外のフラッグは設置できません!");
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);

        GamePlayer.sendMessage(placerTeam, ChatMessageType.ACTION_BAR,
                gplayer.getNickname()+ "&aが&6" + f.getTypeName() + "pフラッグ&aを獲得しました!");
        this.game.getTeams().stream().filter(team -> team != placerTeam)
                .forEach(team -> GamePlayer.sendMessage(team, ChatMessageType.ACTION_BAR,
                        gplayer.getNickname() + "&aに&6" + f.getTypeName() + "pフラッグ&aを獲得されました!"));
        game.getRecordStream().push(new FlagCaptureRecord(
                game.getID(),
                event.getPlayer().getUniqueId(),
                event.getBlock().getLocation(),
                f.getFlagPoint()
        ));

        if (api.getConfig().getUseFlagEffects()) {
            Location loc = b.getLocation();
            Firework effect = loc.getWorld().spawn(loc, Firework.class);
            FireworkMeta meta = effect.getFireworkMeta();
            meta.setPower(0);
            meta.addEffect(
                    FireworkEffect.builder()
                    .trail(true).flicker(false)
                    .withColor(placerTeam.getColor().getColor())
                    .with(FireworkEffect.Type.BURST)
                    .build()
            );
            effect.setFireworkMeta(meta);
            GamePlayer.playSound(placerTeam, Sound.BLOCK_NOTE_HARP);
        }
    }

    private void placeBanner(GamePlayer gplayer, BannerSlot s, BlockPlaceEvent event) {
        event.setCancelled(true);

        if (!game.getBannerHeld(gplayer).isPresent()) {
            gplayer.sendMessage(ChatMessageType.ACTION_BAR, "&cバナーを回収してから設置してください！");
            return;
        }
        HeldBanner banner = game.getBannerHeld(gplayer).get();
        if (s.getColor() != gplayer.getTeam().get().getType()) {
            gplayer.sendMessage(ChatMessageType.ACTION_BAR, "&c敵チームのスロットにバナーを設置することはできません！");
            return;
        }

        GamePlayer.sendMessage(gplayer.getTeam().get(), ChatMessageType.ACTION_BAR,
                gplayer.getNickname() + "&aが&6"
                + banner.getPoint() + "pバナー&aを設置しました！");
        GamePlayer.sendMessage(game.getPlayersNotIn(gplayer.getTeam().get()), ChatMessageType.ACTION_BAR,
                gplayer.getNickname() + "&aに&6"
                + banner.getPoint() + "pバナー&aを設置されました！");

        banner.destroy().forEach(BannerSpawner::spawnBanner);
        game.clearBannerHeld(gplayer);
        game.getRecordStream().push(new BannerDeployRecord(
                game.getID(),
                event.getPlayer().getUniqueId(),
                event.getBlock().getLocation(),
                banner.getPoint()
        ));
        if (api.getConfig().getUseFlagEffects()) {
            Location loc = s.getLocation();
            loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0, 10);
            loc.getWorld().playEffect(loc, Effect.SMOKE, 4, 2);
            GamePlayer.playSound(gplayer.getTeam().get(), Sound.BLOCK_ANVIL_PLACE);
        }
    }

    private void breakFlag(GamePlayer gplayer, Flag f, BlockBreakEvent event) {
        Team placerTeam = gplayer.getTeam().get();
        @SuppressWarnings("deprecation")
        TeamColor brokenTeamColor = TeamColor.getByColorData(event.getBlock().getData());
        if (placerTeam.getType() == brokenTeamColor) {
            gplayer.sendMessage(ChatMessageType.ACTION_BAR, "&c味方チームのフラッグは破壊できません!");
            event.setCancelled(true);
            return;
        }

        Team brokenTeam = game.getTeam(brokenTeamColor);
        event.setCancelled(false);

        GamePlayer.sendMessage(placerTeam, ChatMessageType.ACTION_BAR,
                gplayer.getNickname() + "&aが" + brokenTeamColor.getRichName() + "の&6" + f.getTypeName() + "pフラッグ&aを破壊しました!");
        this.game.getTeams().stream().filter(team -> team == brokenTeam)
                .forEach(team -> GamePlayer.sendMessage(team, ChatMessageType.ACTION_BAR,
                        gplayer.getNickname() + "&aに&6" + f.getTypeName() + "pフラッグ&aを破壊されました!"));
        game.getRecordStream().push(new FlagBreakRecord(
                game.getID(),
                event.getPlayer().getUniqueId(),
                event.getBlock().getLocation(),
                f.getFlagPoint()
        ));

        if (api.getConfig().getUseFlagEffects()) {
            Location loc = event.getBlock().getLocation();
            loc.getWorld().createExplosion(loc, 0F, false);
            GamePlayer.playSound(brokenTeam, Sound.ENTITY_BLAZE_HURT);
        }
    }

    private void breakNexus(GamePlayer gplayer, Nexus f, BlockBreakEvent event) {
        event.setCancelled(true);
        Team breaker = gplayer.getTeam().get();
        Team broken = null;
        if (f.getColor() != null) {
            broken = gplayer.getGame().get().getTeam(f.getColor());
        }

        if (breaker == broken) {
            gplayer.sendMessage(ChatMessageType.ACTION_BAR, "&c味方チームの目標は破壊できません!");
            return;
        }

        if (broken != null) {
            GamePlayer.sendMessage(breaker, ChatMessageType.ACTION_BAR,
                    gplayer.getNickname() + "&aが" + broken.getType().getRichName() + "の&6" + f.getPoint() + "p目標&aを破壊しました!");
            GamePlayer.sendMessage(broken, ChatMessageType.ACTION_BAR,
                    gplayer.getNickname() + "&aに" + f.getPoint() + "p目標&aを破壊されました!");
        } else {
            GamePlayer.sendMessage(game, ChatMessageType.ACTION_BAR,
                    gplayer.getNickname() + "&aが" + f.getPoint() + "p目標&aを破壊しました!");
        }
        game.getRecordStream().push(new NexusBreakRecord(
                game.getID(),
                event.getPlayer().getUniqueId(),
                event.getBlock().getLocation(),
                f.getPoint()
        ));

        if (api.getConfig().getUseFlagEffects()) {
            Location loc = event.getBlock().getLocation();
            loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0, 10);
            if (broken != null) {
                GamePlayer.playSound(broken, Sound.ENTITY_GUARDIAN_HURT);
            }
        }
    }

    private void breakBannerSpawner(GamePlayer gplayer, BannerSpawner s, BlockBreakEvent event) {
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
        HeldBanner banner = game.getBannerHeld(gplayer)
                .map(b -> b.append(s))
                .orElseGet(() -> {
                    HeldBanner b = new HeldBanner(s);
                    game.setBannerHeld(gplayer, b);
                    return b;
                });
        gplayer.getPlayer().getInventory().setHelmet(banner.getBanner(gplayer.getTeam().get().getColor()));
        GamePlayer.sendMessage(gplayer.getTeam().get(), ChatMessageType.ACTION_BAR,
                gplayer.getNickname() + "&aが&6"
                + s.getPoint() + "pバナー&aを回収しました！");
        GamePlayer.sendMessage(game.getPlayersNotIn(gplayer.getTeam().get()), ChatMessageType.ACTION_BAR,
                gplayer.getNickname() + "&aに&6"
                + s.getPoint() + "pバナー&aを回収されました！");
        if (api.getConfig().getUseFlagEffects()) {
            Location loc = event.getBlock().getLocation();
            loc.getWorld().createExplosion(loc, 0F, false);
            loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0, 10);
        }
    }

}
