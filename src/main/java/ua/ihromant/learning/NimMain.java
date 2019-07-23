package ua.ihromant.learning;

import java.util.Scanner;

import ua.ihromant.learning.ai.AITemplate;
import ua.ihromant.learning.ai.QLearningTemplate;
import ua.ihromant.learning.state.NimLineState;
import ua.ihromant.learning.state.Player;

public class NimMain {
	// private static AITemplate template = new MinimaxTemplate(Player.O);
	private static AITemplate template = new QLearningTemplate(new NimLineState(new int[] {1, 3, 5, 7}), 100, 100);
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		NimLineState state = new NimLineState(new int[] {1, 3, 5, 7});
		while (!state.isTerminal()) {
			state = (NimLineState) template.decision(state).getTo();
			System.out.println(state.toString());
			if (state.isTerminal()) {
				break;
			}
			System.out.println("Enter your move, firstly number to take, then index (starting from 0)");
			int toTake = Integer.parseInt(scan.nextLine());
			state = new NimLineState(state,
					Integer.parseInt(scan.nextLine()),
					toTake);
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