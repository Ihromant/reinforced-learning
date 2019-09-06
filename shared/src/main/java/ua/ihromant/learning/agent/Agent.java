package ua.ihromant.learning.agent;

import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.state.State;

import java.util.List;

public interface Agent<A> {
	Decision<A> decision(State<A> from, List<HistoryItem<A>> history);

	class Decision<A> {
		public final A action;
		public boolean random;

		public Decision(A action) {
			this.action = action;
			this.random = false;
		}

		public Decision(A action, boolean random) {
			this.action = action;
			this.random = random;
		}
	}
}
