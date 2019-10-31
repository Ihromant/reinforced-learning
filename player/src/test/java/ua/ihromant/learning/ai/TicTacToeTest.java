package ua.ihromant.learning.ai;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.agent.RandomAgent;
import ua.ihromant.learning.factory.AIZoo;
import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.state.GameResult;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeState;
import ua.ihromant.learning.state.TicTacToeState3x3;
import ua.ihromant.learning.util.WriterUtil;

public class TicTacToeTest {
	@Test
	public void testMinimaxX() {
		int tries = 1000;
		QLearningAI<TTTAction> ai = AIZoo.networkTTTAI(new TicTacToeState3x3(),
				Objects.requireNonNull(getClass().getClassLoader().getResource("test1000k.ai")).getFile());
		Map<Player, Agent<TTTAction>> players = Map.of(Player.X, new MinimaxAI<>(Player.X),
				Player.O, ai);
		Assertions.assertEquals(tries,
				IntStream.range(0, tries)
						.filter(i -> {
							System.out.println(i + " minimax test for player O tictactoe");
							List<HistoryItem<TTTAction>> history = Agent.play(players, new TicTacToeState3x3());
							if (history.get(history.size() - 1).getTo().getResult().getGameResult(Player.X) != GameResult.DRAW) {
								WriterUtil.writeHistory(history, ai.qTable);
								return false;
							}
							return true;
						}).count());
	}

	@Test
	public void testMinimaxO() {
		int tries = 1000;
		QLearningAI<TTTAction> ai = AIZoo.networkTTTAI(new TicTacToeState3x3(),
				Objects.requireNonNull(getClass().getClassLoader().getResource("test1000k.ai")).getFile());
		Map<Player, Agent<TTTAction>> players = Map.of(Player.X, ai,
				Player.O, new MinimaxAI<>(Player.O));
		Assertions.assertEquals(tries,
				IntStream.range(0, tries)
						.filter(i -> {
							System.out.println(i + " minimax test for player X tictactoe");
							List<HistoryItem<TTTAction>> history = Agent.play(players, new TicTacToeState3x3());
							if (history.get(history.size() - 1).getTo().getResult().getGameResult(Player.X) != GameResult.DRAW) {
								WriterUtil.writeHistory(history, ai.qTable);
								return false;
							}
							return true;
						}).count());
	}

	@Test
	public void testRandomX() {
		int tries = 1000;
		QLearningAI<TTTAction> ai = AIZoo.networkTTTAI(new TicTacToeState3x3(),
				Objects.requireNonNull(getClass().getClassLoader().getResource("test1500k.ai")).getFile());
		Map<Player, Agent<TTTAction>> players = Map.of(Player.X, new RandomAgent<>(),
				Player.O, ai);
		Assertions.assertEquals(tries,
				IntStream.range(0, tries)
						.filter(i -> {
							System.out.println(i + " random test for player O tictactoe");
							List<HistoryItem<TTTAction>> history = Agent.play(players, new TicTacToeState3x3());
							if (history.get(history.size() - 1).getTo().getResult().getGameResult(Player.X) == GameResult.WIN) {
								WriterUtil.writeHistory(history, ai.qTable);
								return false;
							}
							return true;
						}).count());
	}

	@Test
	public void testRandomO() {
		int tries = 1000;
		QLearningAI<TTTAction> ai = AIZoo.networkTTTAI(new TicTacToeState3x3(),
				Objects.requireNonNull(getClass().getClassLoader().getResource("test1500k.ai")).getFile());
		Map<Player, Agent<TTTAction>> players = Map.of(Player.X, ai,
				Player.O, new RandomAgent<>());
		Assertions.assertEquals(tries,
				IntStream.range(0, tries)
						.filter(i -> {
							System.out.println(i + " random test for player X tictactoe");
							List<HistoryItem<TTTAction>> history = Agent.play(players, new TicTacToeState3x3());
							if (history.get(history.size() - 1).getTo().getResult().getGameResult(Player.X) == GameResult.LOSE) {
								WriterUtil.writeHistory(history, ai.qTable);
								return false;
							}
							return true;
						}).count());
	}

	@Test
	public void testMinimaxXVsMap() {
		int tries = 1000;
		QLearningAI<TTTAction> ai = AIZoo.mapAI(
				Objects.requireNonNull(getClass().getClassLoader().getResource("testmap.ai")).getFile());
		Map<Player, Agent<TTTAction>> players = Map.of(Player.X, new MinimaxAI<>(Player.X),
				Player.O, ai);
		Assertions.assertEquals(tries,
				IntStream.range(0, tries)
						.filter(i -> {
							System.out.println(i + " minimax test for player X tictactoe");
							List<HistoryItem<TTTAction>> history = Agent.play(players, new TicTacToeState3x3());
							if (history.get(history.size() - 1).getTo().getResult().getGameResult(Player.X) != GameResult.DRAW) {
								WriterUtil.writeHistory(history, ai.qTable);
								return false;
							}
							return true;
						}).count());
	}

	@Test
	public void testMinimaxOVsMap() {
		int tries = 1000;
		QLearningAI<TTTAction> ai = AIZoo.mapAI(
				Objects.requireNonNull(getClass().getClassLoader().getResource("testmap.ai")).getFile());
		Map<Player, Agent<TTTAction>> players = Map.of(Player.O, new MinimaxAI<>(Player.O),
				Player.X, ai);
		Assertions.assertEquals(tries,
				IntStream.range(0, tries)
						.filter(i -> {
							System.out.println(i + " minimax test for player O tictactoe");
							List<HistoryItem<TTTAction>> history = Agent.play(players, new TicTacToeState3x3());
							if (history.get(history.size() - 1).getTo().getResult().getGameResult(Player.X) != GameResult.DRAW) {
								WriterUtil.writeHistory(history, ai.qTable);
								return false;
							}
							return true;
						}).count());
	}
}
