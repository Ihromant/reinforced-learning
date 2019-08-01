package ua.ihromant.learning.state;

import java.util.stream.Stream;

public interface State<A> {
    Stream<A> getActs();

    State<A> apply(A action);

    boolean isTerminal();

    double getUtility(Player player);

    Player getCurrent();

    double[] toModel();

    default double getUtility() { return getUtility(getCurrent()); }

    default Stream<State<A>> getStates() { return getActs().map(this::apply); }
}