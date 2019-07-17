package ua.ihromant.learning.ai.qtable;

import ua.ihromant.learning.state.Action;

public interface QTable {
	double get(Action action);

	void set(Action action, double newValue);
}
