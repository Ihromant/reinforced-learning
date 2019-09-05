package ua.ihromant.learning.agent;

import java.util.Scanner;

import ua.ihromant.learning.state.State;

public abstract class ConsolePlayer<A> implements Agent<A> {
	private Scanner scan;

	protected ConsolePlayer() {
		this.scan = new Scanner(System.in);
	}

	protected abstract void explanation(State<A> state);

	protected abstract A getAction(Scanner scan);

	@Override
	public A decision(State<A> from) {
		explanation(from);
		return getAction(scan);
	}
}
