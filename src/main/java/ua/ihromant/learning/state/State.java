package ua.ihromant.learning.state;

import java.util.stream.Stream;

public interface State<A> {
    Stream<A> getActions();

    State<A> apply(A a);

    boolean isTerminal();

    double getUtility(Player player);

    Player getCurrent();

    double[] toModel();

    default Stream<State> getMoves() { return getActions().map(this::apply); }

    default double getUtility() { return getUtility(getCurrent()); }
}