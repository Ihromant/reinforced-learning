package ua.ihromant.learning.qtable;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapQTable<A> implements QTable<A> {
    protected final Map<StateAction<A>, Double> qStates;

    public MapQTable(Map<StateAction<A>, Double> qStates) {
        this.qStates = qStates;
    }

    @Override
    public double get(StateAction<A> stateAction) {
        return qStates.getOrDefault(stateAction, 0.5);
    }

    @Override
    public Map<StateAction<A>, Double> getMultiple(Stream<StateAction<A>> actions) {
        return actions.collect(Collectors.toMap(Function.identity(), this::get));
    }
}