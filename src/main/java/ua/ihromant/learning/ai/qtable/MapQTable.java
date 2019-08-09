package ua.ihromant.learning.ai.qtable;

import ua.ihromant.learning.state.State;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapQTable<A> implements MonteCarloSearchThree<A> {
	private final double alpha;
	private final Map<State<A>, Double> qStates = new HashMap<>();

	public MapQTable(double alpha) {
		this.alpha = alpha;
	}

	@Override
	public double get(State<A> state) {
		return qStates.getOrDefault(state, 0.0);
	}

	@Override
	public Map<State<A>, Double> getMultiple(Stream<State<A>> actions) {
		return actions.collect(Collectors.toMap(Function.identity(), this::get));
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
	public Map<State<A>, Double> getTree() {
		return qStates;
	}
}
