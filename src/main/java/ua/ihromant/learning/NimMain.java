package ua.ihromant.learning;

import java.util.Arrays;
import java.util.Scanner;

public class NimMain {
	// private static AITemplate template = new MinimaxTemplate(Player.O);
	private static AITemplate template = new QLearningTemplate(new NimState(new int[] {1, 3, 5, 7}), 100000);
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		NimState state = new NimState(new int[] {1, 3, 5, 7});
		while (!state.isTerminal()) {
			state = (NimState) template.decision(state).getTo();
			System.out.println(state.toString());
			if (state.isTerminal()) {
				break;
			}
			System.out.println("Enter your move, firstly number to take, then indices");
			int toTake = Integer.parseInt(scan.nextLine());
			state = new NimState(state,
					Arrays.stream(scan.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray(),
					toTake);
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