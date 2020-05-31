package ua.ihromant.reinforced.ai.qtable;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.logger.TrainingLogger;
import ua.ihromant.learning.qtable.HistoryItem;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.Result;
import ua.ihromant.learning.state.State;
import ua.ihromant.learning.util.ProbabilityUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QLearningTemplate<A> implements TrainingAgent<A> {
    private static final double GAMMA = 0.95;
    private static final double RANDOM_GAMMA = 0.1;
    private static final int STEP = 1000;
    private static final double exploration = 0.1;
    private final TrainableQTable<A> qTable;
    private TrainingLogger<A> logger;

    public QLearningTemplate(TrainableQTable<A> qTable) {
        this.qTable = qTable;
    }

    @Override
    public Decision<A> decision(State<A> from, List<HistoryItem<A>> history) {
        if (history.isEmpty()) {
            List<A> actions = from.getActions().collect(Collectors.toList());
            double[] weights = actions.stream()
                    .mapToDouble(a -> logger.getPrev().getOrDefault(new StateAction<>(from, a), 1L)).toArray();
            return new Decision<>(actions.get(ProbabilityUtil.weightedRandom(weights)));
        }
        int maximumMoves = history.get(0).getFrom().getMaximumMoves();
        double expl = history.size() < Math.sqrt(maximumMoves) ? exploration : exploration / Math.sqrt(maximumMoves);
        boolean random = ThreadLocalRandom.current().nextDouble() < expl;
        return random ? randomAction(from) : algoDecision(from);
    }

    private Decision<A> randomAction(State<A> from) {
        List<StateAction<A>> actions = from.getActions().map(a -> new StateAction<>(from ,a)).collect(Collectors.toList());
        if (actions.size() == 1) {
            return new Decision<>(actions.get(0).getAction());
        }

        return new Decision<>(actions.get(ThreadLocalRandom.current().nextInt(actions.size())).getAction(), true);
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
            double newFactor = item.isRandom() ? oldValue > baseValue ? RANDOM_GAMMA : 1.0 : GAMMA;
            coeff = coeff * newFactor;
            converted.put(item.getStateAction(), linear(oldValue, baseValue, coeff));
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
    public void train(State<A> baseState, int episodes, String target) {
        this.logger = new TrainingLogger<>(episodes, STEP);
        Map<Player, Agent<A>> players = Arrays.stream(Player.values()).collect(Collectors.toMap(Function.identity(), p -> this));
        for (int i = 0; i < episodes; i++) {
            List<HistoryItem<A>> history = Agent.play(players, baseState);
            logger.writeHistory(history, qTable, i);
            qTable.setMultiple(convert(history));
        }
        System.out.println("Serializing to: " + target);
        qTable.serialize(target);
    }
}