package ua.ihromant.learning.ai;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.ai.qtable.MonteCarloSearchThree;
import ua.ihromant.learning.ai.qtable.QTable;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

public class QLearningTemplate<A> implements Agent<A> {
    private static final double GAMMA = 1.0;
    private final QTable<A> qTable;
	private final State<A> baseState;
	private final int episodes;

	public QLearningTemplate(State<A> baseState, QTable<A> qTable, int episodes) {
		this.baseState = baseState;
		this.episodes = episodes;
		this.qTable = qTable;
		init();
	}

	private void init() {
		int percentage = 0;
		long time = System.currentTimeMillis();
		long micro = time;
		for (int i = 0; i < episodes; i++) {
			if (i == episodes / 100 * percentage) {
				System.out.println("Learning " + percentage++ + "% complete, elapsed: " + (System
						.currentTimeMillis() - micro) + " ms");
				micro = System.currentTimeMillis();
			}
			State<A> state = baseState;
			Map<State<A>, Player> history = new HashMap<>();
			Player player = state.getCurrent();
			while (!state.isTerminal()) {
				State<A> next = eGreedy(state, 0.7 + 0.3 * i / episodes);
				history.put(next, player);
				state = next;
				player = state.getCurrent();
			}
			qTable.setMultiple(convert(history, state, player));
		}
		System.out.println("Learning for " + episodes + " took " + (System.currentTimeMillis() - time) + " ms");
	}

	private Map<State<A>, Double> convert(Map<State<A>, Player> history, State<A> finalState, Player lastMoved) {
		double finalResult = finalState.getUtility(lastMoved);
		if (finalResult == 0.5) {
			return history.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
					e -> 0.5));
		}
		if (finalResult == 1.0) {
			return history.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
					e -> e.getValue() == lastMoved ? 1.0 : 0.0));
		}
		if (finalResult == 0.0) {
			return history.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
					e -> e.getValue() == lastMoved ? 0.0 : 1.0));
		}
		throw new IllegalStateException();
	}

	private double getMaxNext(MonteCarloSearchThree<A> tree, State<A> base) {
		List<State<A>> actions = base.getStates().collect(Collectors.toList());
		Map<State<A>, Double> rewards = getFilteredRewards(base.getStates(), base.getCurrent());
		if (actions.size() != rewards.size()) {
			rewards.putAll(tree.getMultiple(actions.stream().filter(act -> !rewards.containsKey(act))));
		}
		return rewards.entrySet().stream().mapToDouble(Map.Entry::getValue).max().orElse(0.0);
	}

	private Map<State<A>, Double> getFilteredRewards(Stream<State<A>> actions, Player current) {
		return actions.filter(act -> act.getUtility(current) != 0.5)
				.collect(Collectors.toMap(Function.identity(), act -> act.getUtility(current)));
	}

	@Override
	public State<A> decision(State<A> state) {
		List<State<A>> actions = state.getStates().collect(Collectors.toList());
		Map<State<A>, Double> rewards = getFilteredRewards(state.getStates(), state.getCurrent());
		if (actions.size() != rewards.size()) {
			rewards.putAll(qTable.getMultiple(actions.stream().filter(act -> !rewards.containsKey(act))));
		}
		return rewards.entrySet().stream()
				.max(Comparator.comparingDouble(Map.Entry::getValue))
				.orElseThrow(IllegalStateException::new).getKey();
	}
}
