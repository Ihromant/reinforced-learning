package ua.ihromant.learning.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ua.ihromant.learning.ai.qtable.EGreedyPolicy;
import ua.ihromant.learning.ai.qtable.GreedyPolicy;
import ua.ihromant.learning.ai.qtable.NetworkQTable;
import ua.ihromant.learning.ai.qtable.QTable;
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
				System.out.println("Learning " + percentage++ + "% complete, elapsed: " + (System.currentTimeMillis() - micro) + " ms");
				micro = System.currentTimeMillis();
			}

			State state = baseState;
			Map<Action, Double> updates = new HashMap<>();
			while (!state.isTerminal()) {
				Action act = policy.apply(state.getActions());
				double reward = act.getReward();
				State next = act.getTo();
				List<Action> toEvaluate = new ArrayList<>();
				toEvaluate.add(act);
				next.getActions().forEach(toEvaluate::add);
				double[] evals = qTable.getMultiple(toEvaluate);
				int maxIndex = IntStream.range(1, evals.length)
						.reduce((a, b) -> evals[a] < evals[b] ? b : a)
						.orElse(0);
				double nextBest = maxIndex == 0 ? 0.0 : evals[maxIndex];
				double previousQ = evals[0];
				double newQ = previousQ + ALPHA * (reward - GAMMA * nextBest - previousQ);
				updates.put(act, newQ);
				state = next;
			}
			qTable.setMultiple(updates);
		}
		System.out.println("Learning for " + episodes + " took " + (System.currentTimeMillis() - time) + " ms");
	}

	@Override
	public Action decision(State state) {
		List<Action> possibleActions = state.getActions().collect(Collectors.toList());
		double[] values = qTable.getMultiple(possibleActions);
		IntStream.range(0, possibleActions.size())
				.forEach(i -> {
					System.out.println(possibleActions.get(i) + " " + values[i]);
				});
		return greedyPolicy.apply(state.getActions());
	}
}
