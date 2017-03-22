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
package jp.llv.flaggame.rollback;

import java.nio.file.Path;
import jp.llv.flaggame.game.basic.objective.BannerSpawner;
import jp.llv.flaggame.game.basic.objective.Flag;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import syam.flaggame.game.Stage;
import syam.flaggame.util.Cuboid;

/**
 *
 * @author toyblocks
 */
public class LegacyRollbacker implements StructureRollbacker {

    @Override
    public void serialize(Stage stage, Cuboid area, Path path) {
    }

    @Override
    public void deserialize(Stage stage, Cuboid area, Path path) throws RollbackException {
        stage.getFlags().values().stream()
                .filter(f -> area.isIn(f.getLocation()))
                .forEach(Flag::rollback);
        stage.getBannerSpawners().values().stream()
                .filter(b -> area.isIn(b.getLocation()))
                .forEach(BannerSpawner::spawnBanner);
        
        for (Location loc : stage.getChests()) {
            if (!area.isIn(loc)) {
                continue;
            }
            Block toBlock = loc.getBlock();
            Block fromBlock = toBlock.getRelative(BlockFace.DOWN, 2);

            if (!(toBlock.getState() instanceof InventoryHolder)) {
                throw new RollbackException("Block is not InventoryHolder!Rollback skipping");
            }
            if (toBlock.getType() != fromBlock.getType()) {
                throw new RollbackException("BlockID unmatched!Rollback skipping..");
            }

            InventoryHolder toContainer;
            InventoryHolder fromContainer;
            try {
                toContainer = (InventoryHolder) toBlock.getState();
                fromContainer = (InventoryHolder) fromBlock.getState();
            } catch (ClassCastException ex) {
                throw new RollbackException("Container can't cast to InventoryHolder! Rollback skipping..");
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
                throw new RollbackException(ex);
            }
        }
    }
    
}
