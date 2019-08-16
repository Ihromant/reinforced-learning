package ua.ihromant.learning.qtable;

import ua.ihromant.learning.state.State;

import java.util.Map;
import java.util.stream.Stream;

public interface QTable<A> {
    double get(State<A> state);

    Map<State<A>, Double> getMultiple(Stream<State<A>> state);
}
