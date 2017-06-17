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
package syam.flaggame.command;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import jp.llv.flaggame.api.FlagGameAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import syam.flaggame.exception.CommandException;
import syam.flaggame.permission.Perms;

public class TpCommand extends BaseCommand {

    public TpCommand(FlagGameAPI api) {
        super(
                api,
                false,
                4,
                "<player> <x> <y> <z> [<yaw> <pitch>] [world] [<vx> <vy> <vz>] <- tp the player",
                Perms.TP,
                "tp"
        );

    }

    @Override
    public void execute(List<String> args, CommandSender sender, Player player) throws CommandException {
        Player target = api.getServer().getPlayer(args.get(0));
        if (target == null) {
            throw new CommandException("&cプレイヤーが見つかりませんでした！");
        }
        double x, y, z;
        try {
            x = Double.parseDouble(args.get(1));
            y = Double.parseDouble(args.get(2));
            z = Double.parseDouble(args.get(3));
        } catch (NumberFormatException ex) {
            throw new CommandException("&c無効な数値です！", ex);
        }
        float yaw, pitch;
        if (args.size() < 6) {
            yaw = target.getLocation().getYaw();
            pitch = target.getLocation().getPitch();
        } else {
            try {
                yaw = Float.parseFloat(args.get(4));
                pitch = Float.parseFloat(args.get(5));
            } catch (NumberFormatException ex) {
                throw new CommandException("&c無効な数値です！", ex);
            }
        }
        World world;
        if (args.size() < 7) {
            world = target.getWorld();
        } else {
            world = api.getServer().getWorld(args.get(6));
            if (world == null) {
                throw new CommandException("&c無効なワールドです！");
            }
        }
        double vx, vy, vz;
        if (args.size() < 10) {
            vx = target.getVelocity().getX();
            vy = target.getVelocity().getY();
            vz = target.getVelocity().getZ();
        } else {
            try {
                vx = Double.parseDouble(args.get(7));
                vy = Double.parseDouble(args.get(8));
                vz = Double.parseDouble(args.get(9));
            } catch (NumberFormatException ex) {
                throw new CommandException("&c無効な数値です！", ex);
            }
        }
        Location loc = new Location(world, x, y, z, yaw, pitch);
        Vector velocity = new Vector(vx, vy, vz);
        target.teleport(loc);
        target.setVelocity(velocity);
        if (sender == target) {
            sendMessage(sender, "&aテレポートしました！");
        } else {
            sendMessage(sender, "&aプレイヤー'&6" + target.getName() + "&a'をテレポートしました！");
        }
    }
}
