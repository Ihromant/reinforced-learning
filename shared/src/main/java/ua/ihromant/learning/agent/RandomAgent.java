package ua.ihromant.learning.agent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.state.State;

public class RandomAgent<A> implements Agent<A> {
	@Override
	public Decision<A> decision(State<A> from, List<HistoryItem<A>> history) {
		List<A> actions = from.getActions().collect(Collectors.toList());
		return new Decision<>(actions.get(ThreadLocalRandom.current().nextInt(actions.size())), true);
	}
}
