package ua.ihromant.learning.ai.qtable;

import ua.ihromant.learning.state.State;

import java.util.List;
import java.util.Map;

public interface QTable<A> {
	double get(State<A> state, double reward);

	double[] getMultiple(List<State<A>> state, Map<State<A>, Double> rewards);

	void set(State<A> state, double newValue);

	void setMultiple(Map<State<A>, Double> newValues);

	double getMax(List<State<A>> states, Map<State<A>, Double> rewards);

	State<A> getMaxAction(List<State<A>> states, Map<State<A>, Double> rewards);
}
