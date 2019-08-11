package ua.ihromant.learning.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.nd4j.shade.jackson.databind.ObjectMapper;
import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.ai.qtable.MonteCarloSearchThree;
import ua.ihromant.learning.ai.qtable.QTable;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

public class QLearningTemplate<A> implements Agent<A> {
    private static final double GAMMA = 0.95;
    private static final ObjectMapper mapper = new ObjectMapper();
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
		Map<Double, Integer> statistics = new TreeMap<>();
		long time = System.currentTimeMillis();
		long micro = time;
		Player conservativePlayer = Player.X;
		int[][] stat = new int[episodes / 1000 / 2][4];
        Map<State<A>, Player> history = new LinkedHashMap<>();
		int counter = 0;
		for (int i = 0; i < episodes; i++) {
			if (i % 1000 == 999) {
				System.out.println("Learning " + 100.0 * i / episodes + "% complete, elapsed: " + (System
						.currentTimeMillis() - micro) + " ms, statistics for player " + conservativePlayer + ": " + statistics);
				writeHistory(history);
				micro = System.currentTimeMillis();
				if (conservativePlayer == Player.X) {
					stat[counter][1] += statistics.getOrDefault(1.0, 0);
					stat[counter][2] += statistics.getOrDefault(0.5, 0);
					stat[counter][3] += statistics.getOrDefault(0.0, 0);
				} else {
					stat[counter][0] = i + 1;
					stat[counter][3] += statistics.getOrDefault(1.0, 0);
					stat[counter][2] += statistics.getOrDefault(0.5, 0);
					stat[counter][1] += statistics.getOrDefault(0.0, 0);
					counter++;
				}
				conservativePlayer = conservativePlayer == Player.X ? Player.O : Player.X;
				statistics.clear();
			}
			State<A> state = baseState;
			history = new LinkedHashMap<>();
			Player player = state.getCurrent();
			while (!state.isTerminal()) {
				State<A> next = player == conservativePlayer ? decision(state) : eGreedy(state, 0.95);
				history.put(next, player);
				state = next;
				player = state.getCurrent();
			}
			qTable.setMultiple(convert(history, state, player));
			double finalResult = state.getUtility(conservativePlayer);
			statistics.put(finalResult, statistics.get(finalResult) == null ? 1 : statistics.get(finalResult) + 1);
		}
		try {
			System.out.println("Extracted statictics: " + mapper.writeValueAsString(stat));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("Learning for " + episodes + " took " + (System.currentTimeMillis() - time) + " ms");
	}

    private void writeHistory(Map<State<A>, Player> history) {
	    List<State<A>> states = new ArrayList<>(history.keySet());
        List<String[]> lines = states.stream()
                .map(State::toString)
                .map(s -> s.split("\n")).collect(Collectors.toList());
        Map<State<A>, Double> evals = qTable.getMultiple(states.stream());
        String[] firstLine = lines.get(0);
        for (int i = 0; i < states.size(); i++) {
            System.out.print(String.format("%." + (lines.get(i)[0].length() - 1) + "f", evals.get(states.get(i))) + " ");
        }
        System.out.println();
        for (int i = 0; i < firstLine.length; i++) {
            for (int j = 0; j < states.size(); j++) {
               System.out.print(lines.get(j)[i] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private Map<State<A>, Double> convert(Map<State<A>, Player> history, State<A> finalState, Player lastMoved) {
		double finalResult = finalState.getUtility(lastMoved);
		Map<State<A>, Double> oldValues = qTable.getMultiple(history.keySet().stream());
		List<State<A>> states = new ArrayList<>(history.keySet());
		int size = states.size();
		if (finalResult == 0.5) {
			return IntStream.range(0, size).boxed().collect(Collectors.toMap(states::get,
					i -> calculateValue(oldValues.get(states.get(i)), 0.5, size - i - 1)));
		}
		if (finalResult == 1.0) {
			return IntStream.range(0, size).boxed().collect(Collectors.toMap(states::get,
					i -> calculateValue(oldValues.get(states.get(i)), history.get(states.get(i)) == lastMoved
							? getWeightedWin(size) : getWeightedLoss(size), size - i - 1)));
		}
		if (finalResult == 0.0) {
			return IntStream.range(0, size).boxed().collect(Collectors.toMap(states::get,
                    i -> calculateValue(oldValues.get(states.get(i)), history.get(states.get(i)) == lastMoved
							? getWeightedLoss(size) : getWeightedWin(size), size - i - 1)));
		}
		throw new IllegalStateException();
	}

	private double getWeightedWin(int moves) {
		return 1.0 - 0.02 * (moves / 2 - 3);
	}

	private double getWeightedLoss(int moves) {
		return 0.02 * (moves / 2 - 3);
	}

	private double calculateValue(double oldValue, double newValue, int dist) {
        double coeff = Math.pow(GAMMA, dist);
        return oldValue * (1 - coeff) + newValue * coeff;
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
