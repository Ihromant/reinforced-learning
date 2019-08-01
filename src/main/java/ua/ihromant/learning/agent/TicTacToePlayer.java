package ua.ihromant.learning.agent;

import java.util.Scanner;
import java.util.function.Function;

import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;

public class TicTacToePlayer implements Function<State<TTTAction>, TTTAction> {
	private final Scanner scan;

	public TicTacToePlayer(Scanner scan) {
		this.scan = scan;
	}

	@Override
	public TTTAction apply(State<TTTAction> state) {
		System.out.println(state);
		System.out.println("Enter your move, 1-9");
		System.out.println("123\n456\n789");
		int next = Integer.parseInt(scan.nextLine()) - 1;
		return new TTTAction(next);
	}
}
