package ua.ihromant.reinforced.ai.qtable;

import org.nd4j.shade.jackson.databind.ObjectMapper;
import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.GameResult;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.Result;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.util.ProbabilityUtil;
import ua.ihromant.learning.util.WriterUtil;

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
    private final Map<Player, Agent<A>> players;

    public QLearningTemplate(State<A> baseState, TrainableQTable<A> qTable) {
        this.baseState = baseState;
        this.qTable = qTable;
        this.players = Arrays.stream(Player.values()).collect(Collectors.toMap(Function.identity(), p -> this));
    }

    private long logStats(Map<GameResult, Integer> statistics, long micro, List<int[]> stat,
                          List<HistoryItem<A>> history, List<List<HistoryItem<A>>> conservativeWrong, int i, int episodes) {
        if (i % STEP == STEP - 1) {
            long res = System.currentTimeMillis();
            System.out.println("Learning " + 100.0 * i / episodes + "% complete, elapsed: " + (res - micro)
                    + " ms, statistics for player X: " + statistics + ", conservative wrong size: " + conservativeWrong.size());
            IntStream.range(0, Math.min(conservativeWrong.size(), 3)).forEach(j -> WriterUtil.writeHistory(conservativeWrong.get(j), qTable));
            WriterUtil.writeHistory(history, qTable);
            this.exploration = ProbabilityUtil.calculateExploration(conservativeWrong.size(), baseState.getMaximumMoves());

            stat.add(new int[] {i + 1, statistics.getOrDefault(GameResult.WIN, 0), statistics.getOrDefault(GameResult.DRAW, 0),
                    statistics.getOrDefault(GameResult.LOSE, 0), conservativeWrong.size()});

            statistics.clear();
            conservativeWrong.clear();
            return res;
        }
        return micro;
    }

    @Override
    public Decision<A> decision(State<A> from, List<HistoryItem<A>> history) {
        if (history.isEmpty()) {
            List<A> actions = from.getActions().collect(Collectors.toList());
            return new Decision<>(actions.get(ThreadLocalRandom.current().nextInt(actions.size())));
        }
        boolean random = ThreadLocalRandom.current().nextDouble() < exploration;
        return random ? randomAction(from) : algoDecision(from);
    }

    private Decision<A> randomAction(State<A> from) {
        List<StateAction<A>> actions = from.getActions().map(a -> new StateAction<>(from ,a)).collect(Collectors.toList());
        if (actions.size() == 1) {
            return new Decision<>(actions.get(0).getAction());
        }

        Map<StateAction<A>, Double> evals = qTable.getMultiple(actions.stream());
        double[] weights = actions.stream()
                .mapToDouble(state -> Arrays.stream(GameResult.values())
                        .mapToDouble(val -> Math.abs(evals.get(state) - val.toDouble()))
                        .min().orElseThrow(RuntimeException::new)).toArray();
        return new Decision<>(actions.get(ProbabilityUtil.weightedRandom(weights)).getAction(), true);
    }

    private Map<StateAction<A>, Double> convert(List<HistoryItem<A>> history) {
        Map<StateAction<A>, Double> oldValues = qTable.getMultiple(history.stream().map(HistoryItem::getStateAction));
        int size = history.size();
        Result result = history.get(size - 1).getTo().getResult();
        double coeff = 1.0;
        Map<StateAction<A>, Double> converted = new HashMap<>();
        for (int i = history.size() - 1; i >= 0; i--) {
            HistoryItem<A> item = history.get(i);
            double baseValue = result.getUtility(item.getPlayer());
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

    private Decision<A> algoDecision(State<A> state) {
        List<StateAction<A>> actions = state.getActions().map(a -> new StateAction<>(state, a)).collect(Collectors.toList());
        if (actions.size() == 1) {
            return new Decision<>(actions.get(0).getAction());
        }

        Map<StateAction<A>, Double> rewards = qTable.getMultiple(actions.stream());
        return new Decision<>(rewards.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElseThrow(IllegalStateException::new).getKey().getAction());
    }

    @Override
    public void train(int episodes, String target) {
        Map<GameResult, Integer> statistics = new EnumMap<>(GameResult.class);
        long time = System.currentTimeMillis();
        long micro = time;
        List<int[]> stat = new ArrayList<>(episodes / STEP);
        List<HistoryItem<A>> history = new ArrayList<>();
        List<List<HistoryItem<A>>> conservativeWrong = new ArrayList<>();
        for (int i = 0; i < episodes; i++) {
            micro = logStats(statistics, micro, stat, history, conservativeWrong, i, episodes);
            history = Agent.play(players, baseState);
            qTable.setMultiple(convert(history));
            GameResult finalResult = history.get(history.size() - 1).getTo().getResult().getGameResult(Player.X);
            statistics.put(finalResult, statistics.get(finalResult) == null ? 1 : statistics.get(finalResult) + 1);
            if (history.stream().noneMatch(HistoryItem::isRandom) && finalResult != history.get(0).getTo().getExpectedResult(Player.X)) {
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