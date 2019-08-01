package ua.ihromant.learning;

import ua.ihromant.learning.ai.AITemplate;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

public class GameBoard<A> {
	private final AITemplate<A> ai;
	private final Function<State<A>, A> agent;
	private final Supplier<State<A>> baseStateProducer;

	public GameBoard(AITemplate<A> ai, Function<State<A>, A> agent, Supplier<State<A>> baseStateProducer) {
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
				playFirst(scan);
			}
			if (decision.equals("2")) {
				playSecond(scan);
			}
		} while (decision.equals("1") || decision.equals("2"));
	}

	public void playFirst(Scanner scan) {
		State<A> state = baseStateProducer.get();
		while (!state.isTerminal()) {
			state = state.apply(agent.apply(state));
			if (state.isTerminal()) {
				break;
			}
			System.out.println(state.toString());
			state = ai.decision(state);
		}
		System.out.println(state);
		switch ((int) state.getUtility(Player.X)) {
			case 0:
				System.out.println("Draw!");
				break;
			case 1:
				System.out.println("You won!");
				break;
			case -1:
				System.out.println("Computer won!");
				break;
		}
	}

	public void playSecond(Scanner scan) {
		State<A> state = baseStateProducer.get();
		while (!state.isTerminal()) {
			state = ai.decision(state);
			if (state.isTerminal()) {
				break;
			}
			state = state.apply(agent.apply(state));
			System.out.println(state.toString());
		}
		System.out.println(state);
		switch ((int) state.getUtility(Player.O)) {
			case 0:
				System.out.println("Draw!");
				return;
			case 1:
				System.out.println("You won!");
				return;
			case -1:
				System.out.println("Computer won!");
		}
	}
}
