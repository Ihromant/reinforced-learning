package ua.ihromant.learning;

import java.util.Scanner;

import ua.ihromant.learning.ai.AITemplate;
import ua.ihromant.learning.ai.QLearningTemplate;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.TicTacToeStateSized;

public class SizedMain {
	// private static AITemplate template = new MinimaxTemplate(Player.O);
	private static AITemplate template = new QLearningTemplate(new TicTacToeStateSized(), 1000000);
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		TicTacToeStateSized state = new TicTacToeStateSized();
		while (!state.isTerminal()) {
			state = (TicTacToeStateSized) template.decision(state).getTo();
			System.out.println(state.toString());
			if (state.isTerminal()) {
				break;
			}
			System.out.println("Enter your move, A-Y");
			System.out.println("ABCDE\nFGHIJ\nKLMNO\nPQRST\nUVWXY");
			int next = scan.nextLine().charAt(0) - 'A';
			state = new TicTacToeStateSized(state, next, Player.O);
			System.out.println(state.toString());
		}
		switch ((int) state.getUtility(Player.X)) {
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