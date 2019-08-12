package ua.ihromant.learning.ai;

import java.util.ArrayList;
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
	public static final double CONSERVATIVE = 0.7;
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
		Map<GameResult, Integer> statistics = new EnumMap<>(GameResult.class);
		long time = System.currentTimeMillis();
		long micro = time;
		Player conservativePlayer = Player.X;
		int[][] stat = new int[episodes / STEP / 2][4];
        List<HistoryItem<A>> history = new ArrayList<>();
        List<List<HistoryItem<A>>> conservativeLoses = new ArrayList<>();
		int counter = 0;
		for (int i = 0; i < episodes; i++) {
			if (i % STEP == STEP - 1) {
				System.out.println("Learning " + 100.0 * i / episodes + "% complete, elapsed: " + (System
						.currentTimeMillis() - micro) + " ms, statistics for player " + conservativePlayer + ": " + statistics);
				IntStream.range(0, Math.min(conservativeLoses.size(), 3)).forEach(j -> writeHistory(conservativeLoses.get(j)));
				micro = System.currentTimeMillis();
				if (conservativePlayer == Player.X) {
					stat[counter][1] += statistics.getOrDefault(GameResult.WIN, 0);
					stat[counter][2] += statistics.getOrDefault(GameResult.DRAW, 0);
					stat[counter][3] += statistics.getOrDefault(GameResult.LOSE, 0);
				} else {
					stat[counter][0] = i + 1;
					stat[counter][3] += statistics.getOrDefault(GameResult.WIN, 0);
					stat[counter][2] += statistics.getOrDefault(GameResult.DRAW, 0);
					stat[counter][1] += statistics.getOrDefault(GameResult.LOSE, 0);
					counter++;
				}
				conservativePlayer = conservativePlayer == Player.X ? Player.O : Player.X;
				statistics.clear();
				conservativeLoses.clear();
			}
			State<A> state = baseState;
			history.clear();
			Player player = state.getCurrent();
			while (!state.isTerminal()) {
				state = getNextAction(state, history, player, conservativePlayer);
				player = state.getCurrent();
			}
			qTable.setMultiple(convert(history));
			GameResult finalResult = state.getUtility(conservativePlayer);
			if (finalResult == GameResult.LOSE) {
				conservativeLoses.add(new ArrayList<>(history));
			}
			statistics.put(finalResult, statistics.get(finalResult) == null ? 1 : statistics.get(finalResult) + 1);
		}
		try {
			System.out.println("Extracted statictics: " + mapper.writeValueAsString(stat));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("Learning for " + episodes + " took " + (System.currentTimeMillis() - time) + " ms");
	}

	private State<A> getNextAction(State<A> from, List<HistoryItem<A>> history, Player player,
			Player conservativePlayer) {
		if (history.isEmpty()) {
			State<A> next = randomAction(from);
			history.add(new HistoryItem<>(next, player, false));
			return next;
		}

		if (player == conservativePlayer) {
			State<A> next = decision(from);
			history.add(new HistoryItem<>(next, player, false));
			return next;
		}

		boolean random = ThreadLocalRandom.current().nextDouble() > CONSERVATIVE;

		State<A> next = random ? randomAction(from) : decision(from);
		history.add(new HistoryItem<>(next, player, random));
		return next;
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
	    	double newFactor = item.isRandom() && oldValue > baseValue ? RANDOM_GAMMA : GAMMA;
	    	coeff = coeff * newFactor;
	    	converted.put(item.getState(), linear(oldValue, baseValue, coeff));
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
		Map<State<A>, Double> rewards = getFilteredRewards(state.getStates(), state.getCurrent());
		if (actions.size() != rewards.size()) {
			rewards.putAll(qTable.getMultiple(actions.stream().filter(act -> !rewards.containsKey(act))));
		}
		return rewards.entrySet().stream()
				.max(Comparator.comparingDouble(Map.Entry::getValue))
				.orElseThrow(IllegalStateException::new).getKey();
	}
}
