package ua.ihromant.learning.ai.qtable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.ihromant.learning.state.Action;

public class MapQTable implements QTable {
	private final Map<Action, Double> qStates = new HashMap<>(10000000);

	@Override
	public double get(Action action) {
		return qStates.getOrDefault(action, 0.0);
	}

	@Override
	public double[] getMultiple(List<Action> actions) {
		return actions.stream().mapToDouble(act -> qStates.getOrDefault(act, 0.0)).toArray();
	}

	@Override
	public void set(Action action, double newValue) {
		qStates.put(action, newValue);
	}

	@Override
	public void setMultiple(Map<Action, Double> newValues) {
		qStates.putAll(newValues);
	}
}
