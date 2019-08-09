package ua.ihromant.learning.agent;

import java.util.Scanner;

import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;

public class TicTacToe5x5Player extends ConsolePlayer<TTTAction> {
	public TicTacToe5x5Player(Scanner scan) {
		super(scan);
	}

	@Override
	protected void explanation(State<TTTAction> state) {
		System.out.println(state.toString());
		System.out.println("Enter your move, A-Y");
		System.out.println("ABCDE\nFGHIJ\nKLMNO\nPQRST\nUVWXY");
	}

	@Override
	protected TTTAction getAction(Scanner scan) {
		int next = scan.nextLine().charAt(0) - 'A';
		return new TTTAction(next);
	}
}
