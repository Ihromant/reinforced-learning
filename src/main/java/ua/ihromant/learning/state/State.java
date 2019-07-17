package ua.ihromant.learning.state;

import java.util.stream.Stream;

public interface State {
    Stream<Action> getActions();

    boolean isTerminal();

    double getUtility(Player player);

    Player getCurrent();
}