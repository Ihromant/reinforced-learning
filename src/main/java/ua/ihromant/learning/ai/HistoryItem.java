package ua.ihromant.learning.ai;

import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

public class HistoryItem<A> {
	private final Player player;
	private final boolean random;
	private final State<A> state;

	public HistoryItem(State<A> state, Player player, boolean random) {
		this.state = state;
		this.player = player;
		this.random = random;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isRandom() {
		return random;
	}

	public State<A> getState() {
		return state;
	}
}