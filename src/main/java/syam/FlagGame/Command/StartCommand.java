package syam.FlagGame.Command;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import syam.FlagGame.Enum.Perms;
import syam.FlagGame.Game.Game;
import syam.FlagGame.Util.Actions;

public class StartCommand extends BaseCommand{
	public StartCommand(){
		bePlayer = false;
		name = "start";
		argLength = 1;
		usage = "<game> <- start game";
	}

	@Override
	public void execute() {
		// flagadmin ready - ゲームを開始準備中にする
		if (args.size() == 0){
			Actions.message(sender, null, "&cゲーム名を入力してください！ /fg start (name)");
			return;
		}

		Game game = plugin.getGame(args.get(0));
		if (game == null){
			Actions.message(sender, null, "&cゲーム'"+args.get(0)+"'が見つかりません");
			return;
		}

		if (!game.isReady()){
			Actions.message(sender, null, "&cゲーム'"+args.get(0)+"'は参加受付状態ではありません");
			return;
		}

		for (Set<String> teamSet : game.getPlayersMap().values()){
			if (teamSet.size() <= 0){
				Actions.message(sender, null, "&cプレイヤーが参加していないチームがあります");
				return;
			}
		}

		// start
		//game.start(sender);
		game.start_timer(sender);
	}

	@Override
	public boolean permission() {
		return Perms.START.has(sender);
	}
}