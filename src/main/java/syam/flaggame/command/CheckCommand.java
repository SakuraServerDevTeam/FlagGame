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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.InventoryHolder;
import syam.flaggame.FlagGame;

import syam.flaggame.exception.CommandException;
import syam.flaggame.game.Stage;
import syam.flaggame.permission.Perms;
import syam.flaggame.util.Actions;

public class CheckCommand extends BaseCommand {

    public CheckCommand(FlagGame plugin) {
        super(plugin);
        bePlayer = false;
        name = "check";
        argLength = 1;
        usage = "<stage> <- check the setup status";
    }

    @Override
    public void execute() throws CommandException {
        Stage stage = plugin.getStages().getStage(args.get(0))
                .orElseThrow(() -> new CommandException("&cステージ'" + args.get(0) + "'が見つかりません"));

        if (stage.isReserved()) {
            throw new CommandException("&cステージ'" + args.get(0) + "'は既に使われています！");
        }

        // 設定状況をチェックする
        Actions.message(sender, msgPrefix + "&aステージ'" + args.get(0) + "'の設定をチェックします..");
        Actions.message(sender, "&a ===========================================");

        // flags
        String help = null;
        Boolean error = false;
        List<String> errorLoc = new ArrayList<>();

        // ステージエリア
        if (!stage.getAreas().hasStageArea()) {
            error = true;
            Actions.message(sender, msgPrefix + "&6[*]&bステージエリア: &c未設定");
            if (help == null) {
                help = "&6 * ステージエリアを設定してください！ *\n" + "&6 WorldEditでステージエリアを選択して、\n" + "&6 '&a/flag set stage&6'コマンドを実行してください";
            }
        } else {
            Actions.message(sender, msgPrefix + "&6[*]&bステージエリア: &6設定済み");
        }

        // チームスポーン
        if (stage.getSpawns().size() < 1) {
            error = true;
            Actions.message(sender, msgPrefix + "&6[*]&b各チームスポーン地点: &c未設定");
            if (help == null) {
                help = "&6 * 各チームのスポーン地点を設定してください！ *\n" + "&6 スポーン地点で'&a/flag set spawn <チーム名>&6'コマンドを実行してください";
            }
        } else {
            Actions.message(sender, msgPrefix + "&6[*]&b各チームスポーン地点: &6設定済み");
        }

        Actions.message(sender, msgPrefix + "&6   &bフラッグ: &6" + stage.getFlags().size() + "個");
        Actions.message(sender, msgPrefix + "&6   &bバナースポナー: &6" + stage.getBannerSpawners().size() + "個");
        Actions.message(sender, msgPrefix + "&6   &bバナースロット: &6" + stage.getBannerSlots().size() + "個");
        Actions.message(sender, msgPrefix + "&6   &bコア: &6" + stage.getNexuses().size() + "個");

        // チェスト
        if (stage.getChests().size() > 0) {
            // 全チェストをチェック
            for (Location loc : stage.getChests()) {
                Block toBlock = loc.getBlock();
                Block fromBlock = toBlock.getRelative(BlockFace.DOWN, 2);
                // インベントリインターフェースを持たないブロック
                if (!(toBlock.getState() instanceof InventoryHolder)) {
                    errorLoc.add("&d インベントリを持つブロックではありません: " + Actions.getBlockLocationString(toBlock.getLocation()));
                    continue;
                }
                // 2ブロック下とブロックIDが違う
                if (toBlock.getTypeId() != fromBlock.getTypeId()) {
                    errorLoc.add("&d 2つ下と同じブロックではありません: " + Actions.getBlockLocationString(toBlock.getLocation()));
                }
            }
            if (!errorLoc.isEmpty()) {
                Actions.message(sender, msgPrefix + "&6   &bチェスト: &c" + stage.getChests().size() + "個中 エラー " + errorLoc.size() + "個");
            } else {
                Actions.message(sender, msgPrefix + "&6   &bチェスト: &6" + stage.getChests().size() + "個 OK");
            }
        } else {
            Actions.message(sender, msgPrefix + "&6   &bチェスト: &6" + stage.getChests().size() + "個");
        }

        // 観戦者スポーン
        if (stage.getSpecSpawn().isPresent()) {
            Actions.message(sender, msgPrefix + "&6   &b観戦者スポーン地点: &6設定済み");
        } else {
            Actions.message(sender, msgPrefix + "&6   &b観戦者スポーン地点: &c未設定");
        }

        Actions.message(sender, "&a ===========================================");
        if (error) {
            Actions.message(sender, "&6 設定が完了していません。[*]の設定は必須項目です");
        } else {
            Actions.message(sender, "&a 必須項目は正しく設定されています");
            Actions.message(sender, "&6ステージ" + (stage.isAvailable() ? "&a有効" : "&c無効"));
        }

        if (help != null) {
            String[] ma = help.split("\n");
            for (String m : ma) {
                Actions.message(sender, m);
            }
        }

        if (!errorLoc.isEmpty()) {
            Actions.message(sender, "&6 チェストに以下のエラーがあります:");
            errorLoc.forEach(m -> Actions.message(sender, m));
        }

        Actions.message(sender, "&a ===========================================");
    }

    @Override
    public boolean permission() {
        return Perms.CHECK.has(sender);
    }
}
