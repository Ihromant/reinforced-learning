package ua.ihromant.learning.ai.qtable;

import java.util.HashMap;
import java.util.Map;

import ua.ihromant.learning.state.Action;

public class MapQTable implements QTable {
	private final Map<Action, Double> qStates = new HashMap<>(10000000);

	@Override
	public double get(Action action) {
		return qStates.getOrDefault(action, 0.0);
	}

	@Override
	public void set(Action action, double newValue) {
		qStates.put(action, newValue);
	}
}
