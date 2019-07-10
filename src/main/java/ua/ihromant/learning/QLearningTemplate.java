package ua.ihromant.learning;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.Stream;

public class QLearningTemplate implements AITemplate {
    private static final double ALPHA = 0.3;
    private static final double GAMMA = 1.0;
	private final Map<Action, Double> qStates = new HashMap<>();
	private final State baseState;
	private final int episodes;

	private final Function<Stream<Action>, Action> policy = new EGreedyPolicy(qStates, 0.7);
	private final Function<Stream<Action>, Action> greedyPolicy = new GreedyPolicy(qStates);

	public QLearningTemplate(State baseState, int episodes) {
		this.baseState = baseState;
		this.episodes = episodes;
		init();
	}

	private void init() {
		int percentage = 0;
		long time = System.currentTimeMillis();
		for (int i = 0; i < episodes; i++) {
			if (i == episodes / 100 * percentage) {
				percentage++;
				System.out.println("Learning " + percentage + "% complete");
			}

			State state = baseState;
			while (!state.isTerminal()) {
				Action act = policy.apply(state.getActions());
				double reward = act.getReward();
				State next = act.getTo();
				OptionalDouble nextBest = next.getActions()
						.mapToDouble(a -> qStates.getOrDefault(a, 0.0)).max();
				double previousQ = qStates.getOrDefault(act, 0.0);
				double newQ = previousQ + ALPHA * (reward - GAMMA * nextBest.orElse(0.0) - previousQ);
				qStates.put(act, newQ);
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
