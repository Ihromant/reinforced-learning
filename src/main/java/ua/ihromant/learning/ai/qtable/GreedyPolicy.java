package ua.ihromant.learning.ai.qtable;

import ua.ihromant.learning.state.State;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GreedyPolicy<A> implements Function<Stream<State<A>>, State<A>> {
    private final QTable<A> qTable;
    public GreedyPolicy(QTable<A> qTable) {
        this.qTable = qTable;
    }

    @Override
    public State<A> apply(Stream<State<A>> actionStream) {
        return qTable.getMaxAction(actionStream.collect(Collectors.toList()));
    }
}
