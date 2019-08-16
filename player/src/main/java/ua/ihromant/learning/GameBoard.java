package ua.ihromant.learning;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

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

	public void playFirst() {
		State<A> state = baseStateProducer.get();
		while (!state.isTerminal()) {
			state = agent.decision(state);
			if (state.isTerminal()) {
				break;
			}
			System.out.println(state.toString());
			state = ai.decision(state);
		}
		System.out.println(state);
		switch (state.getUtility(Player.X)) {
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

	public void playSecond() {
		State<A> state = baseStateProducer.get();
		while (!state.isTerminal()) {
			state = ai.decision(state);
			if (state.isTerminal()) {
				break;
			}
			state = agent.decision(state);
			System.out.println(state.toString());
		}
		System.out.println(state);
		switch (state.getUtility(Player.O)) {
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
