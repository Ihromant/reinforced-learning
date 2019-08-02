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
import java.util.stream.Stream;

public class QLearningTemplate<A> implements Agent<A> {
    private static final double ALPHA = 0.3;
    private static final double GAMMA = 1.0;
    private final QTable<A> qTable;
	private final State<A> baseState;
	private final int episodes;
	private final int mtstGames;
	private final Map<Player, Agent<A>> agents;

	public QLearningTemplate(State<A> baseState, QTable<A> qTable, int episodes,
			int mtstGames) {
		this.baseState = baseState;
		this.episodes = episodes;
		this.mtstGames = mtstGames;
		this.qTable = qTable;
		this.agents = Arrays.stream(Player.values()).collect(Collectors.toMap(Function.identity(), v -> this));
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
			MonteCarloSearchThree<A> tree = new MapNetworkBackedTable<A>(qTable, ALPHA);
			for (int j = 0; j < mtstGames; j++) {
				State<A> state = baseState;
				while (!state.isTerminal()) {
					State<A> next = agents.get(state.getCurrent()).eGreedy(state.getStates(), 0.7);
					double reward = next.getUtility(state.getCurrent());
					double nextBest = tree.getMax(next.getStates().collect(Collectors.toList()));
					double newQ = reward - GAMMA * nextBest;
					tree.set(next, newQ);
					state = next;
				}
			}
			qTable.setMultiple(tree.getTree());
		}
		System.out.println("Learning for " + episodes + " took " + (System.currentTimeMillis() - time) + " ms");
	}

	@Override
	public State<A> decision(State<A> state) {
		return decision(state.getStates());
	}

	@Override
	public State<A> decision(Stream<State<A>> possible) {
		List<State<A>> list = possible.collect(Collectors.toList());
		double[] values = qTable.getMultiple(list);
		IntStream.range(0, list.size())
				.forEach(i -> {
					System.out.println(list.get(i) + " " + values[i]);
				});
		return qTable.getMaxAction(list);
	}
}
