package ua.ihromant.learning.agent;

import java.util.Scanner;

import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;

public class TicTacToe5x6Player extends ConsolePlayer<TTTAction> {
	public TicTacToe5x6Player(Scanner scan) {
		super(scan);
	}

	@Override
	protected void explanation(State<TTTAction> state) {
		System.out.println(state.toString());
		System.out.println("Enter your move, 0-29");
	}

	@Override
	protected TTTAction getAction(Scanner scan) {
		int next = Integer.parseInt(scan.nextLine());
		return new TTTAction(next);
	}
}
