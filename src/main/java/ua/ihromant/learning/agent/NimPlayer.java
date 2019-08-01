package ua.ihromant.learning.agent;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;

import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.State;

public class NimPlayer implements Function<State<NimAction>, NimAction> {
	private final Scanner scan;

	public NimPlayer(Scanner scan) {
		this.scan = scan;
	}

	@Override
	public NimAction apply(State<NimAction> state) {
		System.out.println(state.toString());
		System.out.println("Enter your move, firstly number to take, then indexes (starting from 0, separated by space)");
		int toTake = Integer.parseInt(scan.nextLine());
		return new NimAction(Arrays.stream(scan.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray(), toTake);
	}
}