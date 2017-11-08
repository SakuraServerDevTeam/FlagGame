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
package syam.flaggame.command.kit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.api.exception.CommandException;
import jp.llv.flaggame.api.exception.FlagGameException;
import jp.llv.flaggame.api.kit.Kit;
import jp.llv.flaggame.database.DatabaseException;
import jp.llv.flaggame.events.kit.KitCreateEvent;
import jp.llv.flaggame.util.OnelineBuilder;
import jp.llv.nbt.StructureLibAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.permission.Perms;

/**
 *
 * @author toyblocks
 */
public class KitCreateCommand extends BaseCommand {

    public KitCreateCommand(FlagGameAPI api) {
        super(
                api,
                true,
                1,
                "<name> <- create a kit from your state",
                Perms.KIT_CREATE,
                "create"
        );
    }

    @Override
    protected void execute(List<String> args, CommandSender sender, Player player) throws FlagGameException {
        StructureLibAPI lib = StructureLibAPI.Version.getDetectedVersion(sender);
        if (!Kit.NAME_REGEX.matcher(args.get(0)).matches()) {
            throw new CommandException("&cキット名'" + args.get(0) + "'は使えません！");
        } else if (api.getKits().getKit(args.get(0)).isPresent()) {
            throw new CommandException("&cキット'" + args.get(0) + "'は既に存在しています！");
        }

        Map<String, Integer> effects = new HashMap<>();
        player.getActivePotionEffects().forEach(effect
                -> effects.put(effect.getType().getName(), effect.getAmplifier())
        );
        Kit kit = new Kit(
                args.get(0),
                lib.serialize(player.getInventory().getItemInMainHand()).toTag(),
                lib.serialize(player.getInventory()).toTag(),
                lib.serialize(player.getEnderChest()).toTag(),
                effects
        );

        KitCreateEvent event = new KitCreateEvent(player, kit);
        api.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        api.getKits().addKit(kit);
        OnelineBuilder.newBuilder()
                .info("新規キット").value(kit.getName())
                .info("を登録しました！").sendTo(player);

        api.getDatabase()
                .orElseThrow(() -> new CommandException("&cデータベースへの接続に失敗しました！"))
                .saveKit(kit, result -> {
                    try {
                        result.test();
                        OnelineBuilder.newBuilder()
                                .info("新規キット").value(kit.getName())
                                .info("を保存しました！").sendTo(player);
                    } catch (DatabaseException ex) {
                        OnelineBuilder.newBuilder()
                                .warn("新規キット").value(kit.getName())
                                .warn("の保存に失敗しました！").sendTo(player);
                        api.getLogger().warn("Failed to save a new kit", ex);
                    }
                });
    }

}
