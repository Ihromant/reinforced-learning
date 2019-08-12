package ua.ihromant.learning.agent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import ua.ihromant.learning.state.State;

public interface Agent<A> {
	State<A> decision(State<A> from);

	default State<A> randomAction(State<A> from) {
		List<State<A>> actions = from.getStates().collect(Collectors.toList());
		return actions.get(ThreadLocalRandom.current().nextInt(actions.size()));
	}
}
