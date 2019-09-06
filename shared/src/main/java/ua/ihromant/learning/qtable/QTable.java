package ua.ihromant.learning.qtable;

import java.util.Map;
import java.util.stream.Stream;

public interface QTable<A> {
    double get(StateAction<A> stateAction);

    Map<StateAction<A>, Double> getMultiple(Stream<StateAction<A>> state);
}
