package ua.ihromant.learning.agent;

import java.util.List;
import java.util.Scanner;

import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.state.State;

public abstract class ConsolePlayer<A> implements Agent<A> {
	private final Scanner scan;

	protected ConsolePlayer() {
		this.scan = new Scanner(System.in);
	}

	protected abstract void explanation(State<A> state);

	protected abstract A getAction(Scanner scan);

	@Override
	public Decision<A> decision(State<A> from, List<HistoryItem<A>> history) {
		explanation(from);
		return new Decision<>(getAction(scan));
	}
}
