package ua.ihromant.learning;

import java.util.Objects;

public class Action {
	private final State from;
	private final State to;

	public Action(State from, State to) {
		this.from = from;
		this.to = to;
	}

	public State getFrom() {
		return from;
	}

	public State getTo() {
		return to;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Action action = (Action) o;
		return from.equals(action.from) &&
				to.equals(action.to);
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}

	@Override
	public String toString() {
		return "Action{" +
				"from=" + from +
				", to=" + to +
				'}';
	}
}
