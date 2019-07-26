package ua.ihromant.learning;

import java.util.Scanner;

import ua.ihromant.learning.ai.AITemplate;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.TicTacToeState;

public class GameBoard {
	private final AITemplate ai;

	public GameBoard(AITemplate ai) {
		this.ai = ai;
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
		TicTacToeState state = new TicTacToeState();
		while (!state.isTerminal()) {
			System.out.println("Enter your move, 1-9");
			System.out.println("123\n456\n789");
			int next = Integer.parseInt(scan.nextLine()) - 1;
			state = new TicTacToeState(state, next, Player.X);
			System.out.println(state.toString());
			if (state.isTerminal()) {
				break;
			}
			state = (TicTacToeState) ai.decision(state).getTo();
			System.out.println(state.toString());
		}
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
		TicTacToeState state = new TicTacToeState();
		while (!state.isTerminal()) {
			state = (TicTacToeState) ai.decision(state).getTo();
			System.out.println(state.toString());
			if (state.isTerminal()) {
				break;
			}
			System.out.println("Enter your move, 1-9");
			System.out.println("123\n456\n789");
			int next = Integer.parseInt(scan.nextLine()) - 1;
			state = new TicTacToeState(state, next, Player.O);
			System.out.println(state.toString());
		}
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
