package ua.ihromant.learning;

import java.util.HashMap;
import java.util.Map;

public class QLearningTemplate implements AITemplate {
	private final Map<Action, Double> qStates = new HashMap<>();
	private final State baseState;
	private final int episodes;

	public QLearningTemplate(State baseState, int episodes) {
		this.baseState = baseState;
		this.episodes = episodes;
		init();
	}

	private void init() {

	}

	@Override
	public Action decision(State state) {
		return null;
	}
}
