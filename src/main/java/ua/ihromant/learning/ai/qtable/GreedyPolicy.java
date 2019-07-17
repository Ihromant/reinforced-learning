package ua.ihromant.learning.ai.qtable;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import ua.ihromant.learning.state.Action;

public class GreedyPolicy implements Function<Stream<Action>, Action> {
    private final QTable qTable;
    public GreedyPolicy(QTable qTable) {
        this.qTable = qTable;
    }

    @Override
    public Action apply(Stream<Action> actionStream) {
        return actionStream.max(Comparator.comparingDouble(qTable::get)).get();
    }
}
