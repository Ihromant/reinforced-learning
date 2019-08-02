package ua.ihromant.learning.ai.qtable;

import ua.ihromant.learning.state.State;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapNetworkBackedTable<A> implements MonteCarloSearchThree<A> {
	private final Map<State<A>, Double> qStates = new HashMap<>();
	private final QTable<A> backed;

	public MapNetworkBackedTable(QTable<A> backed) {
		this.backed = backed;
	}

	@Override
	public double get(State<A> state, double reward) {
		if (reward != 0.0) {
			return reward;
		}
		return qStates.getOrDefault(state, backed.get(state, reward));
	}

	@Override
	public double[] getMultiple(List<State<A>> states, Map<State<A>, Double> rewards) {
		Double[] result = new Double[states.size()];
		for (int i = 0; i < states.size(); i++) {
			result[i] = qStates.get(states.get(i));
		}
		int[] missing = IntStream.range(0, result.length).filter(i -> result[i] == null).toArray();
		if (missing.length != 0) {
			double[] computed = backed.getMultiple(
					Arrays.stream(missing).mapToObj(states::get).collect(Collectors.toList()), rewards);
			IntStream.range(0, missing.length).forEach(i -> {
				int missingIdx = missing[i];
				qStates.put(states.get(missingIdx), computed[i]);
				result[missingIdx] = computed[i];
			});
		}
		return Arrays.stream(result).mapToDouble(Double::doubleValue).toArray();
	}

	@Override
	public void set(State<A> state, double newValue) {
		qStates.put(state, newValue);
	}

	@Override
	public void setMultiple(Map<State<A>, Double> newValues) {
		qStates.putAll(newValues);
	}

	@Override
	public double getMax(List<State<A>> actions, Map<State<A>, Double> rewards) {
		double[] evals = getMultiple(actions, rewards);
		int maxIndex = IntStream.range(0, evals.length)
				.reduce((a, b) -> evals[a] < evals[b] ? b : a)
				.orElse(-1);
		return maxIndex == -1 ? 0.0 : evals[maxIndex];
	}

	@Override
	public State<A> getMaxAction(List<State<A>> states, Map<State<A>, Double> rewards) {
		double[] results = getMultiple(states, rewards);
		return states.get(IntStream.range(0, results.length)
				.reduce((a, b) -> results[a] < results[b] ? b : a).orElse(0));
	}

	@Override
	public Map<State<A>, Double> getTree() {
		return qStates;
	}
}
