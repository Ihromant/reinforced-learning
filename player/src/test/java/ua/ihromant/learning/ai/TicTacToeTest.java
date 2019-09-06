package ua.ihromant.learning.ai;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.GamePlayer;
import ua.ihromant.learning.factory.AIZoo;
import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.state.GameResult;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState3x3;
import ua.ihromant.learning.util.WriterUtil;

public class TicTacToeTest {
	@Test
	public void testFirst() {
		QLearningAI<TTTAction> ai = AIZoo.networkTTTAI(9,
				getClass().getClassLoader().getResource("test1500k.ai").getFile());
		Map<Player, Agent<TTTAction>> players = Map.of(Player.X, new MinimaxAI<>(Player.X),
				Player.O, ai);
		IntStream.range(0, 1000)
				.forEach(i -> {
					System.out.println(i + " minimax test for player O tictactoe");
					List<HistoryItem<TTTAction>> history = new GamePlayer<>(players, new TicTacToeState3x3()).play();
					if (history.get(history.size() - 1).getTo().getUtility(Player.X) != GameResult.DRAW) {
						WriterUtil.writeHistory(history, ai.qTable);
					}
				});
	}

	@Test
	public void testSecond() {
		QLearningAI<TTTAction> ai = AIZoo.networkTTTAI(9,
				getClass().getClassLoader().getResource("test1500k.ai").getFile());
		Map<Player, Agent<TTTAction>> players = Map.of(Player.X, ai,
				Player.O, new MinimaxAI<>(Player.O));
		IntStream.range(0, 1000)
				.forEach(i -> {
					System.out.println(i + " minimax test for player X tictactoe");
					List<HistoryItem<TTTAction>> history = new GamePlayer<>(players, new TicTacToeState3x3()).play();
					if (history.get(history.size() - 1).getTo().getUtility(Player.X) != GameResult.DRAW) {
						WriterUtil.writeHistory(history, ai.qTable);
					}
				});
	}
}
