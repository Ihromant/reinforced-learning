package ua.ihromant.learning;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.util.WriterUtil;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

public class GameBoard<A> {
	private final Agent<A> ai;
	private final Agent<A> agent;
	private final Supplier<State<A>> baseStateProducer;

	public GameBoard(Agent<A> ai, Agent<A> agent, Supplier<State<A>> baseStateProducer) {
		this.ai = ai;
		this.agent = agent;
		this.baseStateProducer = baseStateProducer;
	}

	public void play() {
		Scanner scan = new Scanner(System.in);
		String decision;
		do {
			System.out.println("Write whether you move first or second. Other values will exit the game");
			decision = scan.nextLine();
			if (decision.equals("1")) {
				playFirst();
			}
			if (decision.equals("2")) {
				playSecond();
			}
		} while (decision.equals("1") || decision.equals("2"));
	}

	private void playFirst() {
		List<HistoryItem<A>> history = Agent.play(Map.of(Player.X, agent, Player.O, ai), baseStateProducer.get());
		WriterUtil.writeHistory(history);
		switch (history.get(history.size() - 1).getTo().getResult().getGameResult(Player.X)) {
			case DRAW:
				System.out.println("Draw!");
				break;
			case WIN:
				System.out.println("You won!");
				break;
			case LOSE:
				System.out.println("Computer won!");
				break;
		}
	}

	private void playSecond() {
		List<HistoryItem<A>> history = Agent.play(Map.of(Player.X, ai, Player.O, agent), baseStateProducer.get());
		WriterUtil.writeHistory(history);
		switch (history.get(history.size() - 1).getTo().getResult().getGameResult(Player.O)) {
			case DRAW:
				System.out.println("Draw!");
				return;
			case WIN:
				System.out.println("You won!");
				return;
			case LOSE:
				System.out.println("Computer won!");
		}
	}
}
