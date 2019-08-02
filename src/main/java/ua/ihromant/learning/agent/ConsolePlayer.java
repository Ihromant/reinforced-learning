package ua.ihromant.learning.agent;

import java.util.Scanner;
import java.util.stream.Stream;

import ua.ihromant.learning.state.State;

public abstract class ConsolePlayer<A> implements Agent<A> {
	private Scanner scan;

	protected ConsolePlayer(Scanner scan) {
		this.scan = scan;
	}

	protected abstract void explanation(State<A> state);

	protected abstract A getAction(Scanner scan);

	@Override
	public State<A> decision(State<A> from) {
		explanation(from);
		return from.apply(getAction(scan));
	}
}
