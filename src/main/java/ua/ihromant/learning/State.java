package ua.ihromant.learning;

import java.util.Collection;

public interface State {
    Collection<State> getActions();

    boolean isTerminal();

    double getUtility();
}