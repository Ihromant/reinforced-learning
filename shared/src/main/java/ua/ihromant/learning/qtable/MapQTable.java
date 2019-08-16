package ua.ihromant.learning.qtable;

import ua.ihromant.learning.state.State;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapQTable<A> implements QTable<A> {
    protected final Map<State<A>, Double> qStates;

    public MapQTable(Map<State<A>, Double> qStates) {
        this.qStates = qStates;
    }

    @Override
    public double get(State<A> state) {
        return qStates.getOrDefault(state, 0.0);
    }

    @Override
    public Map<State<A>, Double> getMultiple(Stream<State<A>> actions) {
        return actions.collect(Collectors.toMap(Function.identity(), this::get));
    }
}