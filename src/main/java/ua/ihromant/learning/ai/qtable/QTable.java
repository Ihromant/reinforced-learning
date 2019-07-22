package ua.ihromant.learning.ai.qtable;

import java.util.List;
import java.util.Map;

import ua.ihromant.learning.state.Action;

public interface QTable {
	double get(Action action);

	double[] getMultiple(List<Action> actions);

	void set(Action action, double newValue);

	void setMultiple(Map<Action, Double> newValues);
}
