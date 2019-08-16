package ua.ihromant.learning.agent;

import java.util.Scanner;

import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;

public class TicTacToePlayer extends ConsolePlayer<TTTAction> {
	@Override
	protected void explanation(State<TTTAction> state) {
		System.out.println(state);
		System.out.println("Enter your move, 1-9");
		System.out.println("123\n456\n789");
	}

	@Override
	protected TTTAction getAction(Scanner scan) {
		int next = Integer.parseInt(scan.nextLine()) - 1;
		return new TTTAction(next);
	}
}
