package ua.ihromant.learning.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ua.ihromant.learning.ai.qtable.EGreedyPolicy;
import ua.ihromant.learning.ai.qtable.GreedyPolicy;
import ua.ihromant.learning.ai.qtable.MapNetworkBackedTable;
import ua.ihromant.learning.ai.qtable.MonteCarloSearchThree;
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
	private final int mtstGames;

	private final Function<Stream<Action>, Action> policy = new EGreedyPolicy(qTable, 0.7);
	private final Function<Stream<Action>, Action> greedyPolicy = new GreedyPolicy(qTable);

	public QLearningTemplate(State baseState, int episodes, int mtstGames) {
		this.baseState = baseState;
		this.episodes = episodes;
		this.mtstGames = mtstGames;
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
			MonteCarloSearchThree tree = new MapNetworkBackedTable(qTable, ALPHA);
			for (int j = 0; j < mtstGames; j++) {
				State state = baseState;
				while (!state.isTerminal()) {
					Action act = policy.apply(state.getActions());
					double reward = act.getReward();
					State next = act.getTo();
					double nextBest = tree.getMax(next.getActions().collect(Collectors.toList()));
					double newQ = reward - GAMMA * nextBest;
					tree.set(act, newQ);
					state = next;
				}
			}
			qTable.setMultiple(tree.getTree());
		}
		System.out.println("Learning for " + episodes + " took " + (System.currentTimeMillis() - time) + " ms");
	}

	@Override
	public <A> State<A> decision(State<A> state) {
		List<Action> possibleActions = state.getActions().collect(Collectors.toList());
		double[] values = qTable.getMultiple(possibleActions);
		return state.apply(greedyPolicy.apply(state.getActions()));
	}
}
