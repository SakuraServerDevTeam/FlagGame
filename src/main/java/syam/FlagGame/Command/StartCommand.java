package syam.FlagGame.Command;

import java.util.Set;

import syam.FlagGame.Game.Game;
import syam.FlagGame.Game.Stage;
import syam.FlagGame.Manager.GameManager;
import syam.FlagGame.Manager.StageManager;
import syam.FlagGame.Permission.Perms;
import syam.FlagGame.Util.Actions;

public class StartCommand extends BaseCommand{
	public StartCommand(){
		bePlayer = false;
		name = "start";
		argLength = 1;
		usage = "<stage> <- start game";
	}

	@Override
	public void execute() {
		if (args.size() == 0){
			Actions.message(sender, null, "&cステージ名を入力してください！");
			return;
		}

		Game game = null;
		if (args.get(0).equalsIgnoreCase("random")){
			// ランダムステージ
			game = GameManager.getRandomGame();
			if (game == null || !game.isReady()){
				Actions.message(sender, null, "&c参加受付状態のランダムステージはありません！");
				return;
			}
		}else{
			game = GameManager.getGame(args.get(0));
		}

		if (game == null || !game.isReady()){
			// そのステージが本当にあるかチェック
			if (StageManager.getStage(args.get(0)) == null){
				Actions.message(sender, null, "&cステージ'"+args.get(0)+"'が見つかりません");
			}else{
				Actions.message(sender, null, "&cステージ'"+args.get(0)+"'は参加受付状態ではありません");
			}
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
