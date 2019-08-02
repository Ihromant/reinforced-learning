package ua.ihromant.learning.ai;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.ai.qtable.*;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QLearningTemplate<A> implements Agent<A> {
    private static final double GAMMA = 1.0;
    private final QTable<A> qTable;
	private final State<A> baseState;
	private final int episodes;
	private final int mtstGames;

	public QLearningTemplate(State<A> baseState, QTable<A> qTable, int episodes, int mtstGames) {
		this.baseState = baseState;
		this.episodes = episodes;
		this.mtstGames = mtstGames;
		this.qTable = qTable;
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
			MonteCarloSearchThree<A> tree = new MapNetworkBackedTable<A>(qTable);
			for (int j = 0; j < mtstGames; j++) {
				State<A> state = baseState;
				while (!state.isTerminal()) {
					State<A> next = eGreedy(state, 0.7);
					double reward = next.getUtility(state.getCurrent());
					double nextBest = getMaxNext(tree, next);
					double newQ = reward - GAMMA * nextBest;
					tree.set(next, newQ);
					state = next;
				}
			}
			qTable.setMultiple(tree.getTree());
		}
		System.out.println("Learning for " + episodes + " took " + (System.currentTimeMillis() - time) + " ms");
	}

	private double getMaxNext(MonteCarloSearchThree<A> tree, State<A> base) {
		Player current = base.getCurrent();
		List<State<A>> actions = base.getStates().collect(Collectors.toList());
		Map<State<A>, Double> rewards = actions.stream()
				.collect(Collectors.toMap(Function.identity(), act -> act.getUtility(current)));
		return tree.getMax(actions, rewards);
	}

	@Override
	public State<A> decision(State<A> state) {
		Player current = state.getCurrent();
		Map<State<A>, Double> rewards = state.getStates()
				.collect(Collectors.toMap(Function.identity(), act -> act.getUtility(current)));
		List<State<A>> list = state.getStates().collect(Collectors.toList());
//		double[] values = qTable.getMultiple(list, rewards);
//		IntStream.range(0, list.size())
//				.forEach(i -> {
//					System.out.println(list.get(i) + " " + values[i]);
//				});
		return qTable.getMaxAction(list, rewards);
	}
}
