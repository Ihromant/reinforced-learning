package ua.ihromant.learning.ai;

import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.Stream;

import ua.ihromant.learning.ai.qtable.*;
import ua.ihromant.learning.state.Action;
import ua.ihromant.learning.state.State;

public class QLearningTemplate implements AITemplate {
    private static final double ALPHA = 0.3;
    private static final double GAMMA = 1.0;
    private QTable qTable = new NetworkQTable();
	private final State baseState;
	private final int episodes;

	private final Function<Stream<Action>, Action> policy = new EGreedyPolicy(qTable, 0.7);
	private final Function<Stream<Action>, Action> greedyPolicy = new GreedyPolicy(qTable);

	public QLearningTemplate(State baseState, int episodes) {
		this.baseState = baseState;
		this.episodes = episodes;
		init();
	}

	private void init() {
		int percentage = 0;
		long time = System.currentTimeMillis();
		long micro = time;
		for (int i = 0; i < episodes; i++) {
			if (i == episodes / 100 * percentage) {
				percentage++;
				System.out.println("Learning " + percentage + "% complete, elapsed: " + (System.currentTimeMillis() - micro) + " ms");
				micro = System.currentTimeMillis();
			}

			State state = baseState;
			while (!state.isTerminal()) {
				Action act = policy.apply(state.getActions());
				double reward = act.getReward();
				State next = act.getTo();
				OptionalDouble nextBest = next.getActions()
						.mapToDouble(a -> qTable.get(a)).max();
				double previousQ = qTable.get(act);
				double newQ = previousQ + ALPHA * (reward - GAMMA * nextBest.orElse(0.0) - previousQ);
				qTable.set(act, newQ);
				state = next;
			}
		}
		System.out.println("Learning for " + episodes + " took " + (System.currentTimeMillis() - time) + " ms");
	}

	@Override
	public Action decision(State state) {
		return greedyPolicy.apply(state.getActions());
	}
}
