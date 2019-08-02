package ua.ihromant.learning.agent;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;

import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.State;

public class NimPlayer extends ConsolePlayer<NimAction> {
	public NimPlayer(Scanner scan) {
		super(scan);
	}

	@Override
	protected void explanation(State<NimAction> state) {
		System.out.println(state.toString());
		System.out.println("Enter your move, firstly number to take, then index (starting from 0)");
	}

	@Override
	protected NimAction getAction(Scanner scan) {
		int toTake = Integer.parseInt(scan.nextLine());
		return new NimAction(Arrays.stream(scan.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray(), toTake);
	}
}