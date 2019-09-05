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
			Agent.Decision<A> dec = currentAgent.decision(state, history);
			HistoryItem<A> item = new HistoryItem<>(state, dec.action, state.apply(dec.action), state.getCurrent(), dec.random);
			history.add(item);
			state = item.getTo();
		}
		return history;
	}
}
