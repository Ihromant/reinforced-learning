package ua.ihromant.learning.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

public class GamePlayer<A> {
	private final Map<Player, Agent<A>> players;
	private final State<A> baseState;
	public GamePlayer(Map<Player, Agent<A>> players, State<A> baseState) {
		this.players = players;
		this.baseState = baseState;
	}

	public List<HistoryItem<A>> play() {
		List<HistoryItem<A>> history = new ArrayList<>();
		State<A> state = baseState;
		while (!state.isTerminal()) {
			Agent<A> currentAgent = players.get(state.getCurrent());
			A action = currentAgent.decision(state);
			HistoryItem<A> item = new HistoryItem<>(state, action, state.apply(action), state.getCurrent(), false);
			state = item.getTo();
		}
		return history;
	}
}
