package ua.ihromant.learning;

import java.util.stream.Stream;

public interface State {
    Stream<Action> getActions();

    boolean isTerminal();

    double getUtility(Player player);

    Player getCurrent();
}