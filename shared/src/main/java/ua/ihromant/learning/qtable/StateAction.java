package ua.ihromant.learning.qtable;

import java.io.Serializable;
import java.util.Objects;

import ua.ihromant.learning.state.State;

public class StateAction<A> implements Serializable {
	private final State<A> state;
	private final A action;

	public StateAction(State<A> state, A action) {
		this.state = state;
		this.action = action;
	}

	public <ST extends State<A>> ST getResult() {
		return (ST) state.apply(action);
	}

	public State<A> getState() {
		return state;
	}

	public A getAction() {
		return action;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StateAction<?> that = (StateAction<?>) o;
		return state.equals(that.state) &&
				action.equals(that.action);
	}

	@Override
	public int hashCode() {
		return Objects.hash(state, action);
	}
}
