package ua.ihromant.learning.ai.qtable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ua.ihromant.learning.state.Action;

public class MapNetworkBackedTable implements MonteCarloSearchThree {
	private final Map<Action, Double> qStates = new HashMap<>();
	private final QTable backed;
	private final double alpha;

	public MapNetworkBackedTable(QTable backed, double alpha) {
		this.backed = backed;
		this.alpha = alpha;
	}

	@Override
	public double get(Action action) {
		return qStates.getOrDefault(action, backed.get(action));
	}

	@Override
	public double[] getMultiple(List<Action> actions) {
		Double[] result = new Double[actions.size()];
		for (int i = 0; i < actions.size(); i++) {
			result[i] = qStates.get(actions.get(i));
		}
		int[] missing = IntStream.range(0, result.length).filter(i -> result[i] == null).toArray();
		if (missing.length != 0) {
			double[] computed = backed.getMultiple(
					Arrays.stream(missing).mapToObj(actions::get).collect(Collectors.toList()));
			IntStream.range(0, missing.length).forEach(i -> {
				int missingIdx = missing[i];
				qStates.put(actions.get(missingIdx), computed[i]);
				result[missingIdx] = computed[i];
			});
		}
		return Arrays.stream(result).mapToDouble(Double::doubleValue).toArray();
	}

	@Override
	public void set(Action action, double newValue) {
		qStates.put(action, newValue);
	}

	@Override
	public void setMultiple(Map<Action, Double> newValues) {
		qStates.putAll(newValues);
	}

	@Override
	public double getMax(List<Action> actions) {
		double rewardMax = actions.stream().mapToDouble(act -> act.getTo().getUtility()).max().orElse(0.0);
		if (rewardMax > 0.0) {
			return rewardMax;
		}
		double[] evals = getMultiple(actions);
		int maxIndex = IntStream.range(0, evals.length)
				.reduce((a, b) -> evals[a] < evals[b] ? b : a)
				.orElse(-1);
		return maxIndex == -1 ? 0.0 : evals[maxIndex];
	}

	@Override
	public Action getMaxAction(List<Action> actions) {
		double[] results = getMultiple(actions);
		return actions.get(IntStream.range(0, results.length)
				.reduce((a, b) -> results[a] < results[b] ? b : a).orElse(0));
	}

	@Override
	public Map<Action, Double> getTree() {
		List<Action> existing = new ArrayList<>(qStates.keySet());
		double[] evaluations = backed.getMultiple(existing);
		return IntStream.range(0, existing.size()).filter(i -> qStates.get(existing.get(i)) != evaluations[i])
				.boxed()
				.collect(Collectors.toMap(existing::get, i -> qStates.get(existing.get(i))));
	}
}
