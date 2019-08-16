package ua.ihromant.learning.ai.qtable;

import ua.ihromant.learning.state.State;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface QTable<A> {
	double get(State<A> state);

	Map<State<A>, Double> getMultiple(Stream<State<A>> state);

	void set(State<A> state, double newValue);

	void setMultiple(Map<State<A>, Double> newValues);

	void serialize(String path) throws IOException;
}
