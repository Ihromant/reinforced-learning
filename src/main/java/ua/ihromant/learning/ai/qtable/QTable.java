package ua.ihromant.learning.ai.qtable;

import ua.ihromant.learning.state.State;

import java.util.List;
import java.util.Map;

public interface QTable<A> {
	double get(State<A> state);

	double[] getMultiple(List<State<A>> state);

	void set(State<A> state, double newValue);

	void setMultiple(Map<State<A>, Double> newValues);

	double getMax(List<State<A>> states);

	State<A> getMaxAction(List<State<A>> states);
}
