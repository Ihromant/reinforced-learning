package ua.ihromant.learning;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class GreedyPolicy implements Function<Stream<Action>, Action> {
    protected final Map<Action, Double> qStates;
    public GreedyPolicy(Map<Action, Double> qStates) {
        this.qStates = qStates;
    }

    @Override
    public Action apply(Stream<Action> actionStream) {
        return actionStream.max(Comparator.comparingDouble(act -> qStates.getOrDefault(act, 0.0))).get();
    }
}
