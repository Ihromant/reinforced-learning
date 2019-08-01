package ua.ihromant.learning;

import ua.ihromant.learning.ai.AITemplate;
import ua.ihromant.learning.ai.QLearningTemplate;
import ua.ihromant.learning.ai.qtable.MapQTable;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.state.TTTAction;
import ua.ihromant.learning.state.TicTacToeStateSized;

import java.util.Scanner;

public class SizedMain {
	// private static AITemplate template = new MinimaxTemplate(Player.O);
	private static AITemplate<TTTAction> template = new QLearningTemplate<>(new TicTacToeStateSized(),
			new MapQTable<>(0.3), 100, 10000);
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		State<TTTAction> state = new TicTacToeStateSized();
		while (!state.isTerminal()) {
			state = template.decision(state);
			System.out.println(state.toString());
			if (state.isTerminal()) {
				break;
			}
			System.out.println("Enter your move, A-Y");
			System.out.println("ABCDE\nFGHIJ\nKLMNO\nPQRST\nUVWXY");
			int next = scan.nextLine().charAt(0) - 'A';
			state = state.apply(new TTTAction(Player.O, next));
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