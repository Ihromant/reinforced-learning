package ua.ihromant.learning.state;

import java.util.stream.Stream;

public interface State<A> {
    Stream<A> getActions();

    State<A> apply(A action);

    boolean isTerminal();

    GameResult getUtility(Player player);

    Player getCurrent();

    int getMaximumMoves();

    default GameResult getExpectedResult(Player pl) { return null; }
}