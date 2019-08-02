package ua.ihromant.learning.agent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ua.ihromant.learning.state.State;

public interface Agent<A> {
	default State<A> decision(State<A> from) { return decision(from.getStates()); }

	State<A> decision(Stream<State<A>> possible);

	default State<A> eGreedy(Stream<State<A>> possible, double conservativeRate) {
		double d = ThreadLocalRandom.current().nextDouble();
		if (d < conservativeRate) {
			return decision(possible);
		}

		List<State<A>> actions = possible.collect(Collectors.toList());
		return actions.get(ThreadLocalRandom.current().nextInt(actions.size()));
	}
}
