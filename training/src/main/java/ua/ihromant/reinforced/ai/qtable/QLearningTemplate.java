package ua.ihromant.reinforced.ai.qtable;

import org.nd4j.shade.jackson.databind.ObjectMapper;
import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.qtable.StateAction;
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

public class QLearningTemplate<A> implements TrainingAgent<A> {
    private static final double GAMMA = 0.8;
    private static final double RANDOM_GAMMA = 0.1;
    private static final int STEP = 1000;
    private double exploration = 0.0;
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
            this.exploration = ProbabilityUtil.calculateExploration(conservativeWrong.size(), baseState.getMaximumMoves());

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
            A next = randomAction(from);
            HistoryItem<A> result = new HistoryItem<>(from, next, from.apply(next), player, false);
            history.add(result);
            return result;
        }

        boolean random = ThreadLocalRandom.current().nextDouble() < exploration;

        A next = random ? randomAction(from) : decision(from);
        HistoryItem<A> result = new HistoryItem<>(from, next, from.apply(next), player, random);
        history.add(result);
        return result;
    }

    private A randomAction(State<A> from) {
        List<StateAction<A>> actions = from.getActions().map(a -> new StateAction<>(from ,a)).collect(Collectors.toList());
        if (actions.size() == 1) {
            return actions.get(0).getAction();
        }

        Map<StateAction<A>, Double> evals = qTable.getMultiple(actions.stream());
        double[] weights = actions.stream()
                .mapToDouble(state -> Arrays.stream(GameResult.values())
                        .mapToDouble(val -> Math.abs(evals.get(state) - val.toDouble()))
                        .min().orElseThrow(RuntimeException::new)).toArray();
        return actions.get(ProbabilityUtil.weightedRandom(weights)).getAction();
    }

    private void writeHistory(List<HistoryItem<A>> history) {
        List<String[]> lines = history.stream()
                .map(h -> h.getTo().toString())
                .map(s -> s.split("\n")).collect(Collectors.toList());
        Map<StateAction<A>, Double> evals = qTable.getMultiple(history.stream().map(HistoryItem::getStateAction));
        String[] firstLine = lines.get(0);
        for (int i = 0; i < history.size(); i++) {
            String format = history.get(i).isRandom()
                    ? "R%." + (lines.get(i)[0].length() - 2) + "f"
                    : "%." + (lines.get(i)[0].length() - 1) + "f";
            System.out.print(String.format(format, evals.get(history.get(i).getStateAction())) + " ");
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

    private Map<StateAction<A>, Double> convert(List<HistoryItem<A>> history) {
        Map<StateAction<A>, Double> oldValues = qTable.getMultiple(history.stream().map(HistoryItem::getStateAction));
        int size = history.size();
        State<A> last = history.get(size - 1).getTo();
        Player lastMoved = history.get(size - 1).getPlayer();
        GameResult result = last.getUtility(lastMoved);
        double coeff = 1.0;
        Map<StateAction<A>, Double> converted = new HashMap<>();
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
            double oldValue = oldValues.get(item.getStateAction());
            converted.put(item.getStateAction(), linear(oldValue, baseValue, coeff));
            double newFactor = item.isRandom() ? oldValue > baseValue ? RANDOM_GAMMA : 1.0 : GAMMA;
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

    private Map<StateAction<A>, Double> getFilteredRewards(List<StateAction<A>> actions, Player current) {
        return actions.stream().filter(act -> act.getResult().getUtility(current) != GameResult.DRAW)
                .collect(Collectors.toMap(Function.identity(), act -> act.getResult().getUtility(current).toDouble()));
    }

    @Override
    public A decision(State<A> state) {
        List<StateAction<A>> actions = state.getActions().map(a -> new StateAction<>(state, a)).collect(Collectors.toList());
        if (actions.size() == 1) {
            return actions.get(0).getAction();
        }

        Map<StateAction<A>, Double> rewards = getFilteredRewards(actions, state.getCurrent());
        if (actions.size() != rewards.size()) {
            rewards.putAll(qTable.getMultiple(actions.stream().filter(act -> !rewards.containsKey(act))));
        }
        double max = rewards.values().stream().mapToDouble(Double::doubleValue).max().orElseThrow(IllegalStateException::new);
        if (max < 0.25) {
            List<StateAction<A>> states = new ArrayList<>(rewards.keySet());
            double[] weights = states.stream().mapToDouble(st -> rewards.get(st) * rewards.get(st)).toArray();
            return states.get(ProbabilityUtil.weightedRandom(weights)).getAction();
        }
        return rewards.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElseThrow(IllegalStateException::new).getKey().getAction();
    }

    @Override
    public void train(int episodes, String target) {
        Map<GameResult, Integer> statistics = new EnumMap<>(GameResult.class);
        long time = System.currentTimeMillis();
        long micro = time;
        List<int[]> stat = new ArrayList<>(episodes / STEP);;
        List<HistoryItem<A>> history = new ArrayList<>();
        List<List<HistoryItem<A>>> conservativeWrong = new ArrayList<>();
        for (int i = 0; i < episodes; i++) {
            micro = logStats(statistics, micro, stat, history, conservativeWrong, i, episodes);
            history.clear();

            State<A> state = baseState;
            Player player = state.getCurrent();
            boolean random = false;
            while (!state.isTerminal()) {
                HistoryItem<A> action = getNextAction(state, history, player);
                random = random || action.isRandom();
                state = action.getTo();
                player = state.getCurrent();
            }
            qTable.setMultiple(convert(history));
            GameResult finalResult = state.getUtility(Player.X);
            statistics.put(finalResult, statistics.get(finalResult) == null ? 1 : statistics.get(finalResult) + 1);
            if (!random && finalResult != history.get(0).getTo().getExpectedResult(Player.X)) {
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