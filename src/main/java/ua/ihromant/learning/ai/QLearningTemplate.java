package ua.ihromant.learning.ai;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.ai.qtable.*;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
			MonteCarloSearchThree<A> tree = new MapNetworkBackedTable<>(qTable);
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
		List<State<A>> actions = base.getStates().collect(Collectors.toList());
		Map<State<A>, Double> rewards = getFilteredRewards(base.getStates(), base.getCurrent());
		rewards.putAll(tree.getMultiple(actions.stream().filter(act -> !rewards.containsKey(act))));
		return rewards.entrySet().stream().mapToDouble(Map.Entry::getValue).max().orElse(0.0);
	}

	private Map<State<A>, Double> getFilteredRewards(Stream<State<A>> actions, Player current) {
		return actions.filter(act -> act.getUtility(current) != 0.0)
				.collect(Collectors.toMap(Function.identity(), act -> act.getUtility(current)));
	}

	@Override
	public State<A> decision(State<A> state) {
		List<State<A>> actions = state.getStates().collect(Collectors.toList());
		Map<State<A>, Double> rewards = getFilteredRewards(state.getStates(), state.getCurrent());
		rewards.putAll(qTable.getMultiple(actions.stream().filter(act -> !rewards.containsKey(act))));
//		double[] values = qTable.getMultiple(list, rewards);
//		IntStream.range(0, list.size())
//				.forEach(i -> {
//					System.out.println(list.get(i) + " " + values[i]);
//				});
		return rewards.entrySet().stream()
				.max(Comparator.comparingDouble(Map.Entry::getValue))
				.orElseThrow(IllegalStateException::new).getKey();
	}
}
