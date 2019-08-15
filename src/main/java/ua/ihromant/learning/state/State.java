package ua.ihromant.learning.state;

import java.util.stream.Stream;

public interface State<A> {
    Stream<A> getActs();

    State<A> apply(A action);

    boolean isTerminal();

    GameResult getUtility(Player player);

    Player getCurrent();

    double[] toModel();

    int getMaximumMoves();

    default Stream<State<A>> getStates() { return getActs().map(this::apply); }

    default GameResult getExpectedResult(Player pl) { return null; }
}