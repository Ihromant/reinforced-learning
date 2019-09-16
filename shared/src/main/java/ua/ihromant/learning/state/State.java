package ua.ihromant.learning.state;

import java.io.Serializable;
import java.util.stream.Stream;

public interface State<A> extends Serializable {
    Stream<A> getActions();

    State<A> apply(A action);

    Result getResult();

    Player getCurrent();

    int getMaximumMoves();

    default GameResult getExpectedResult(Player pl) { return null; }
}