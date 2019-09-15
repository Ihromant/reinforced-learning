package ua.ihromant.learning.agent;

import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Agent<A> {
	Decision<A> decision(State<A> from, List<HistoryItem<A>> history);

	static <A> List<HistoryItem<A>> play(Map<Player, Agent<A>> players, State<A> baseState) {
		List<HistoryItem<A>> history = new ArrayList<>();
		State<A> state = baseState;
		while (state.getResult() == null) {
			Agent<A> currentAgent = players.get(state.getCurrent());
			Agent.Decision<A> dec = currentAgent.decision(state, history);
			HistoryItem<A> item = new HistoryItem<>(state, dec.action, state.apply(dec.action), state.getCurrent(), dec.random);
			history.add(item);
			state = item.getTo();
		}
		return history;
	}

	class Decision<A> {
		public final A action;
		public final boolean random;

		public Decision(A action) {
			this(action, false);
		}

		public Decision(A action, boolean random) {
			this.action = action;
			this.random = random;
		}
	}
}
