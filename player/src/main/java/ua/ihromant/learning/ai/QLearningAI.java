package ua.ihromant.learning.ai;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.qtable.QTable;
import ua.ihromant.learning.qtable.StateAction;
import ua.ihromant.learning.state.GameResult;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QLearningAI<A> implements Agent<A> {
    private final QTable<A> qTable;

    public QLearningAI(QTable<A> qTable) {
        this.qTable = qTable;
    }

    private Map<State<A>, Double> getFilteredRewards(Stream<State<A>> actions, Player current) {
        return actions.filter(act -> act.getUtility(current) != GameResult.DRAW)
                .collect(Collectors.toMap(Function.identity(), act -> act.getUtility(current).toDouble()));
    }

    @Override
    public A decision(State<A> state) {
        List<A> actions = state.getActs().collect(Collectors.toList());
        if (actions.size() == 1) {
            return actions.get(0);
        }

        Map<StateAction<A>, Double> rewards = qTable.getMultiple(
                actions.stream().map(act -> new StateAction<>(state, act)));
        return rewards.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElseThrow(IllegalStateException::new).getKey().getAction();
    }
}
