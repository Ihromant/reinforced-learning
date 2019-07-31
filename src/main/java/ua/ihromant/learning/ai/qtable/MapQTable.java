package ua.ihromant.learning.ai.qtable;

import ua.ihromant.learning.state.State;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapQTable<A> implements MonteCarloSearchThree<A> {
	private final double alpha;
	private final Map<State<A>, Double> qStates = new HashMap<>();

	public MapQTable(double alpha) {
		this.alpha = alpha;
	}

	@Override
	public double get(State<A> state) {
		double reward = state.getUtility();
		if (reward != 0.0) {
			return reward;
		}
		return qStates.getOrDefault(state, reward);
	}

	@Override
	public double[] getMultiple(List<State<A>> actions) {
		return actions.stream().mapToDouble(this::get).toArray();
	}

	@Override
	public void set(State<A> action, double newValue) {
		qStates.compute(action, (act, oldVal) -> {
			oldVal = oldVal != null ? oldVal : 0.0;
			return (1 - alpha) * oldVal + alpha * newValue;
		});
	}

	@Override
	public void setMultiple(Map<State<A>, Double> newValues) {
		newValues.forEach(this::set);
	}

	@Override
	public double getMax(List<State<A>> actions) {
		return actions.stream().mapToDouble(this::get).max().orElse(0.0);
	}

	@Override
	public State<A> getMaxAction(List<State<A>> actions) {
		return actions.stream().max(Comparator.comparingDouble(this::get)).orElseThrow(IllegalStateException::new);
	}

	@Override
	public Map<State<A>, Double> getTree() {
		return qStates;
	}
}
