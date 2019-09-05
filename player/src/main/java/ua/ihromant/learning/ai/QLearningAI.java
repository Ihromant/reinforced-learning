package ua.ihromant.learning.ai;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.qtable.HistoryItem;
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

    @Override
    public Decision<A> decision(State<A> state, List<HistoryItem<A>> history) {
        List<A> actions = state.getActions().collect(Collectors.toList());
        if (actions.size() == 1) {
            return new Decision<>(actions.get(0));
        }

        Map<StateAction<A>, Double> rewards = qTable.getMultiple(
                actions.stream().map(act -> new StateAction<>(state, act)));
        return new Decision<>(rewards.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElseThrow(IllegalStateException::new).getKey().getAction());
    }
}
