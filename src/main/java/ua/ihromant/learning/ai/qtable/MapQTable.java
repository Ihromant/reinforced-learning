package ua.ihromant.learning.ai.qtable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.ihromant.learning.state.Action;

public class MapQTable implements MonteCarloSearchThree {
	private final double alpha;
	private final Map<Action, Double> qStates = new HashMap<>();

	public MapQTable(double alpha) {
		this.alpha = alpha;
	}

	@Override
	public double get(Action action) {
		double reward = action.getTo().getUtility();
		if (reward != 0.0) {
			return reward;
		}
		return qStates.getOrDefault(action, reward);
	}

	@Override
	public double[] getMultiple(List<Action> actions) {
		return actions.stream().mapToDouble(this::get).toArray();
	}

	@Override
	public void set(Action action, double newValue) {
		qStates.compute(action, (act, oldVal) -> {
			oldVal = oldVal != null ? oldVal : 0.0;
			return (1 - alpha) * oldVal + alpha * newValue;
		});
	}

	@Override
	public void setMultiple(Map<Action, Double> newValues) {
		newValues.forEach(this::set);
	}

	@Override
	public double getMax(List<Action> actions) {
		return actions.stream().mapToDouble(this::get).max().orElse(0.0);
	}

	@Override
	public Action getMaxAction(List<Action> actions) {
		return actions.stream().max(Comparator.comparingDouble(this::get)).get();
	}

	@Override
	public Map<Action, Double> getTree() {
		return qStates;
	}
}
