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
package jp.llv.flaggame.rollback;

import java.util.function.Consumer;
import jp.llv.flaggame.game.basic.objective.BannerSpawner;
import jp.llv.flaggame.game.basic.objective.Flag;
import jp.llv.flaggame.rollback.SerializeTask.CompletedSerializeTask;
import jp.llv.flaggame.rollback.SerializeTask.FailedSerializeTask;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import syam.flaggame.FlagGame;
import syam.flaggame.game.Stage;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author SakuraServerDev
 */
public class ClassicStageData extends VoidStageData {

    @Override
    public SerializeTask load(FlagGame plugin, Stage stage, Cuboid area, Consumer<RollbackException> callback) {
        stage.getFlags().values().stream()
                .filter(f -> area.contains(f.getLocation()))
                .forEach(Flag::rollback);
        stage.getBannerSpawners().values().stream()
                .filter(b -> area.contains(b.getLocation()))
                .forEach(BannerSpawner::spawnBanner);

        for (Location loc : stage.getChests()) {
            if (!area.contains(loc)) {
                continue;
            }
            Block toBlock = loc.getBlock();
            Block fromBlock = toBlock.getRelative(BlockFace.DOWN, 2);

            if (!(toBlock.getState() instanceof InventoryHolder)) {
                return new FailedSerializeTask(callback,
                        new RollbackException("Block is not InventoryHolder!Rollback skipping")
                );
            }
            if (toBlock.getType() != fromBlock.getType()) {
                return new FailedSerializeTask(callback,
                        new RollbackException("BlockID unmatched!Rollback skipping..")
                );
            }

            InventoryHolder toContainer;
            InventoryHolder fromContainer;
            try {
                toContainer = (InventoryHolder) toBlock.getState();
                fromContainer = (InventoryHolder) fromBlock.getState();
            } catch (ClassCastException ex) {
                return new FailedSerializeTask(callback,
                        new RollbackException("Container can't cast to InventoryHolder! Rollback skipping..")
                );
            }

            try {
                ItemStack[] oldIs = fromContainer.getInventory().getContents().clone();
                ItemStack[] newIs = new ItemStack[oldIs.length];
                for (int i = 0; i < oldIs.length; i++) {
                    if (oldIs[i] == null) {
                        continue;
                    }
                    newIs[i] = new ItemStack(oldIs[i]);
                }
                toContainer.getInventory().setContents(newIs);
            } catch (Exception ex) {
                return new FailedSerializeTask(callback,
                        new RollbackException(ex)
                );
            }
        }
        return new CompletedSerializeTask(callback);
    }

    @Override
    public StageDataType getType() {
        return StageDataType.CLASSIC;
    }

}