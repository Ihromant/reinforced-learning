package ua.ihromant.reinforced.ai.qtable;

import org.nd4j.shade.jackson.databind.ObjectMapper;
import ua.ihromant.learning.state.GameResult;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.util.ProbabilityUtil;

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

public class QLearningTemplate<A> implements TrainingAgent<A> {
    private static final double GAMMA = 0.95;
    private static final double RANDOM_GAMMA = 0.1;
    private static final int STEP = 1000;
    private static final double CONSERVATIVE = 0.8;
    private final TrainableQTable<A> qTable;
    private final State<A> baseState;

    public QLearningTemplate(State<A> baseState, TrainableQTable<A> qTable) {
        this.baseState = baseState;
        this.qTable = qTable;
    }

    private long logStats(Map<GameResult, Integer> statistics, long micro, List<int[]> stat,
                          List<HistoryItem<A>> history, List<List<HistoryItem<A>>> conservativeWrong, int i, int episodes) {
        if (i % STEP == STEP - 1) {
            long res = System.currentTimeMillis();
            System.out.println("Learning " + 100.0 * i / episodes + "% complete, elapsed: " + (res - micro) + " ms, " +
                    "statistics for player X: " + statistics + ", conservative wrong size: " + conservativeWrong.size());
            IntStream.range(0, Math.min(conservativeWrong.size(), 3)).forEach(j -> writeHistory(conservativeWrong.get(j)));
            writeHistory(history);

            stat.add(new int[] {i + 1, statistics.getOrDefault(GameResult.WIN, 0), statistics.getOrDefault(GameResult.DRAW, 0),
                    statistics.getOrDefault(GameResult.LOSE, 0), conservativeWrong.size()});

            statistics.clear();
            conservativeWrong.clear();
            return res;
        }
        return micro;
    }

    private HistoryItem<A> getNextAction(State<A> from, List<HistoryItem<A>> history, Player player) {
        if (history.isEmpty()) {
            State<A> next = randomAction(from);
            HistoryItem<A> result = new HistoryItem<>(next, player, false);
            history.add(result);
            return result;
        }

        boolean random = ThreadLocalRandom.current().nextDouble() > CONSERVATIVE;

        State<A> next = random ? randomAction(from) : decision(from);
        HistoryItem<A> result = new HistoryItem<>(next, player, random);
        history.add(result);
        return result;
    }

    private State<A> randomAction(State<A> from) {
        List<State<A>> states = from.getStates().collect(Collectors.toList());
        if (states.size() == 1) {
            return states.get(0);
        }

        Map<State<A>, Double> evals = qTable.getMultiple(states.stream());
        double[] weights = states.stream()
                .mapToDouble(state -> Arrays.stream(GameResult.values())
                        .mapToDouble(val -> Math.abs(evals.get(state) - val.toDouble()))
                        .min().orElseThrow(RuntimeException::new)).toArray();
        return states.get(ProbabilityUtil.weightedRandom(weights));
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

    @Override
    public void train(int episodes, String target) {
        Map<GameResult, Integer> statistics = new EnumMap<>(GameResult.class);
        long time = System.currentTimeMillis();
        long micro = time;
        List<int[]> stat = new ArrayList<>(episodes / STEP);;
        List<HistoryItem<A>> history = new ArrayList<>();
        List<List<HistoryItem<A>>> conservativeWrong = new ArrayList<>();
        GameResult expectedResult = baseState.getExpectedResult(Player.X);
        for (int i = 0; i < episodes; i++) {
            micro = logStats(statistics, micro, stat, history, conservativeWrong, i, episodes);
            history.clear();

            State<A> state = baseState;
            Player player = state.getCurrent();
            boolean random = false;
            while (!state.isTerminal()) {
                HistoryItem<A> action = getNextAction(state, history, player);
                random = random || action.isRandom();
                state = action.getState();
                player = state.getCurrent();
            }
            qTable.setMultiple(convert(history));
            GameResult finalResult = state.getUtility(Player.X);
            statistics.put(finalResult, statistics.get(finalResult) == null ? 1 : statistics.get(finalResult) + 1);
            if (!random && finalResult != expectedResult) {
                conservativeWrong.add(new ArrayList<>(history));
            }
        }
        try {
            System.out.println("Extracted statictics: " + new ObjectMapper().writeValueAsString(stat));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Learning for " + episodes + " took " + (System.currentTimeMillis() - time) + " ms");
        System.out.println("Serializing to: " + target);
        qTable.serialize(target);
    }
}