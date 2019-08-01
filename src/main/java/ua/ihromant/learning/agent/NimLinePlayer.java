package ua.ihromant.learning.agent;

import java.util.Scanner;
import java.util.function.Function;

import ua.ihromant.learning.state.NimAction;
import ua.ihromant.learning.state.State;

public class NimLinePlayer implements Function<State<NimAction>, NimAction> {
	private final Scanner scan;

	public NimLinePlayer(Scanner scan) {
		this.scan = scan;
	}

	@Override
	public NimAction apply(State<NimAction> state) {
		System.out.println(state.toString());
		System.out.println("Enter your move, firstly number to take, then index (starting from 0)");
		int toTake = Integer.parseInt(scan.nextLine());
		return new NimAction(Integer.parseInt(scan.nextLine()), toTake);
	}
}
