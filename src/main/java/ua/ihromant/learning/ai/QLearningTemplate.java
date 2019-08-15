package ua.ihromant.learning.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.nd4j.shade.jackson.databind.ObjectMapper;
import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.ai.qtable.QTable;
import ua.ihromant.learning.state.GameResult;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

public class QLearningTemplate<A> implements Agent<A> {
	private static final double GAMMA = 0.95;
	private static final double RANDOM_GAMMA = 0.1;
    private static final int STEP = 1000;
    private static final ObjectMapper mapper = new ObjectMapper();
	private static final double CONSERVATIVE = 0.2;
	private final QTable<A> qTable;
	private final State<A> baseState;
	private final int episodes;
	private final double[] probabilities;

	public QLearningTemplate(State<A> baseState, QTable<A> qTable, int episodes) {
		this.baseState = baseState;
		this.episodes = episodes;
		this.qTable = qTable;
		this.probabilities = initProbabilities(baseState.getMaximumMoves() - 2, CONSERVATIVE);
		init();
	}

	private double[] initProbabilities(int moves, double conservativeRate) {
		double one = (1.0 - conservativeRate) / moves;
		double[] result = new double[moves];
		double multiplier = 1.0;
		for (int i = 0; i < moves; i++) {
			result[i] = one / multiplier;
			multiplier = multiplier * (1 - result[i]);
		}
		return result;
	}

	private void init() {
		Map<GameResult, Integer> statistics = new EnumMap<>(GameResult.class);
		long time = System.currentTimeMillis();
		long micro = time;
		List<int[]> stat = new ArrayList<>(episodes / STEP);;
        List<HistoryItem<A>> history = new ArrayList<>();
        List<List<HistoryItem<A>>> conservativeLoses = new ArrayList<>();
		for (int i = 0; i < episodes; i++) {
			micro = logStats(statistics, micro, stat, history, conservativeLoses, i);
			history.clear();

			State<A> state = baseState;
			Player player = state.getCurrent();
			boolean random = false;
			while (!state.isTerminal()) {
				HistoryItem<A> action = getNextAction(state, history, player, random);
				random = random || action.isRandom();
				state = action.getState();
				player = state.getCurrent();
			}
			qTable.setMultiple(convert(history));
			GameResult finalResult = state.getUtility(Player.X);
			statistics.put(finalResult, statistics.get(finalResult) == null ? 1 : statistics.get(finalResult) + 1);
			if (!random && (finalResult == GameResult.LOSE || finalResult == GameResult.WIN)) {
				conservativeLoses.add(new ArrayList<>(history));
			}
		}
		try {
			System.out.println("Extracted statictics: " + mapper.writeValueAsString(stat));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("Learning for " + episodes + " took " + (System.currentTimeMillis() - time) + " ms");
	}

	private long logStats(Map<GameResult, Integer> statistics, long micro, List<int[]> stat,
			List<HistoryItem<A>> history, List<List<HistoryItem<A>>> conservativeLoses, int i) {
		if (i % STEP == STEP - 1) {
			System.out.println("Learning " + 100.0 * i / episodes + "% complete, elapsed: " + (System
					.currentTimeMillis() - micro) + " ms, statistics for player X: " + statistics + " " +
					", conservative loses size: " + conservativeLoses.size());
			IntStream.range(0, Math.min(conservativeLoses.size(), 3)).forEach(j -> writeHistory(conservativeLoses.get(j)));
			writeHistory(history);
			micro = System.currentTimeMillis();

			stat.add(new int[] {i + 1 ,statistics.getOrDefault(GameResult.WIN, 0), statistics.getOrDefault(GameResult.DRAW, 0),
					statistics.getOrDefault(GameResult.LOSE, 0), conservativeLoses.size()});

			statistics.clear();
			conservativeLoses.clear();
		}
		return micro;
	}

	private HistoryItem<A> getNextAction(State<A> from, List<HistoryItem<A>> history, Player player, boolean random) {
		if (history.isEmpty() || history.size() == probabilities.length + 1) {
			State<A> next = randomAction(from);
			HistoryItem<A> result = new HistoryItem<>(next, player, false);
			history.add(result);
			return result;
		}

		random = !random && ThreadLocalRandom.current().nextDouble() < probabilities[history.size() - 1];

		State<A> next = random ? randomAction(from) : decision(from);
		HistoryItem<A> result = new HistoryItem<>(next, player, random);
		history.add(result);
		return result;
	}

	@Override
	public State<A> randomAction(State<A> from) {
		List<State<A>> states = from.getStates().collect(Collectors.toList());
		Map<State<A>, Double> evals = qTable.getMultiple(states.stream());
		double[] weights = states.stream()
				.mapToDouble(state -> Arrays.stream(GameResult.values())
						.mapToDouble(val -> Math.abs(evals.get(state) - val.toDouble()))
						.min().orElseThrow(RuntimeException::new)).toArray();
		double rand = ThreadLocalRandom.current().nextDouble(Arrays.stream(weights).sum());
		double sum = 0.0;
		for (int i = 0; i < states.size(); i++) {
			sum += weights[i];
			if (sum > rand) {
				return states.get(i);
			}
		}
		return states.get(states.size() - 1);
	}

    private void writeHistory(List<HistoryItem<A>> history) {
        List<String[]> lines = history.stream()
                .map(h -> h.getState().toString())
                .map(s -> s.split("\n")).collect(Collectors.toList());
        Map<State<A>, Double> evals = qTable.getMultiple(history.stream().map(HistoryItem::getState));
        String[] firstLine = lines.get(0);
        for (int i = 0; i < history.size(); i++) {
            System.out.print(String.format("%." + (lines.get(i)[0].length() - 1) + "f", evals.get(history.get(i).getState())) + " ");
        }
        System.out.println();
        for (int i = 0; i < firstLine.length; i++) {
            for (int j = 0; j < history.size(); j++) {
               System.out.print(lines.get(j)[i] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private Map<State<A>, Double> convert(List<HistoryItem<A>> history) {
		Map<State<A>, Double> oldValues = qTable.getMultiple(history.stream().map(HistoryItem::getState));
	    int size = history.size();
	    State<A> last = history.get(size - 1).getState();
	    Player lastMoved = history.get(size - 1).getPlayer();
	    GameResult result = last.getUtility(lastMoved);
	    double coeff = 1.0;
	    Map<State<A>, Double> converted = new HashMap<>();
	    for (int i = history.size() - 1; i >= 0; i--) {
	    	HistoryItem<A> item = history.get(i);
	    	double baseValue;
	    	if (result == GameResult.DRAW) {
	    		baseValue = 0.5;
		    } else {
			    if (result == GameResult.WIN) {
				    baseValue = item.getPlayer() == lastMoved ? getWeightedWin(size) : getWeightedLoss(size);
			    }
			    else {
				    baseValue = item.getPlayer() == lastMoved ? getWeightedLoss(size) : getWeightedWin(size);
			    }
		    }
	    	double oldValue = oldValues.get(item.getState());
	    	converted.put(item.getState(), linear(oldValue, baseValue, coeff));
		    double newFactor = item.isRandom() && oldValue > baseValue ? RANDOM_GAMMA : GAMMA;
		    coeff = coeff * newFactor;
	    }
	    return converted;
	}

	private double linear(double oldValue, double newValue, double coeff) {
		return oldValue * (1 - coeff) + newValue * coeff;
	}

	private double getWeightedWin(int moves) {
		return 1.0 - 0.02 * (moves / 2 - 2);
	}

	private double getWeightedLoss(int moves) {
		return 0.02 * (moves / 2 - 2);
	}

	private Map<State<A>, Double> getFilteredRewards(Stream<State<A>> actions, Player current) {
		return actions.filter(act -> act.getUtility(current) != GameResult.DRAW)
				.collect(Collectors.toMap(Function.identity(), act -> act.getUtility(current).toDouble()));
	}

	@Override
	public State<A> decision(State<A> state) {
		List<State<A>> actions = state.getStates().collect(Collectors.toList());
		if (actions.size() == 1) {
			return actions.get(0);
		}

		Map<State<A>, Double> rewards = getFilteredRewards(state.getStates(), state.getCurrent());
		if (actions.size() != rewards.size()) {
			rewards.putAll(qTable.getMultiple(actions.stream().filter(act -> !rewards.containsKey(act))));
		}
		return rewards.entrySet().stream()
				.max(Comparator.comparingDouble(Map.Entry::getValue))
				.orElseThrow(IllegalStateException::new).getKey();
	}
}
