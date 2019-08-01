package ua.ihromant.learning;

import ua.ihromant.learning.ai.AITemplate;
import ua.ihromant.learning.ai.QLearningTemplate;
import ua.ihromant.learning.ai.qtable.MapQTable;
import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.NimLineState;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

import java.util.Scanner;

public class NimMain {
	// private static AITemplate template = new MinimaxTemplate(Player.O);
	private static AITemplate<NimAction> template = new QLearningTemplate<>(new NimLineState(new int[] {1, 3, 5, 7}),
			new MapQTable<>(0.3), 100, 100);
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		State<NimAction> state = new NimLineState(new int[] {1, 3, 5, 7});
		while (!state.isTerminal()) {
			state = template.decision(state);
			System.out.println(state.toString());
			if (state.isTerminal()) {
				break;
			}
			System.out.println("Enter your move, firstly number to take, then index (starting from 0)");
			int toTake = Integer.parseInt(scan.nextLine());
			state = state.apply(new NimAction(Integer.parseInt(scan.nextLine()), toTake));
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