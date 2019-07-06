package ua.ihromant.learning;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
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
		for (int i = 0; i < episodes; i++) {
			State state = baseState;
			while (!state.isTerminal()) {
				Action act = policy.apply(state.getActions());
				Player currPlayer = act.getPlayer();
				double reward = act.getReward();
				State next = act.getTo();
				BiFunction<Stream<Action>, Comparator, Optional<Action>> choiseFunction = next.getCurrent() == currPlayer ? Stream::max : Stream::min;
				Optional<Action> nextBest = choiseFunction.apply(next.getActions(), Comparator.comparing(a -> (int) Math.signum(qStates.getOrDefault(a, 0.0))));
				double previousQ = qStates.getOrDefault(act, 0.0);
				double newQ = previousQ + ALPHA * (reward + GAMMA * qStates.getOrDefault(nextBest.orElse(null), 0.0) - previousQ);
				qStates.put(act, newQ);
				state = next;
			}
		}
	}

	@Override
	public Action decision(State state) {
		return greedyPolicy.apply(state.getActions());
	}
}
