package ua.ihromant.learning.agent;

import java.util.Scanner;
import java.util.function.Function;

import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;

public class TicTacToeSizedPlayer implements Function<State<TTTAction>, TTTAction> {
	private final Scanner scan;

	public TicTacToeSizedPlayer(Scanner scan) {
		this.scan = scan;
	}
	@Override
	public TTTAction apply(State<TTTAction> state) {
		System.out.println(state.toString());
		System.out.println("Enter your move, A-Y");
		System.out.println("ABCDE\nFGHIJ\nKLMNO\nPQRST\nUVWXY");
		int next = scan.nextLine().charAt(0) - 'A';
		return new TTTAction(next);
	}
}
