package ua.ihromant.learning;

import java.util.Comparator;

public final class MinimaxTemplate {

    private MinimaxTemplate() {
    }

    public static State minimaxDecision(State state) {
        return state.getActions().stream()
                .max(Comparator.comparing(MinimaxTemplate::minValue)).get();
    }

    private static double maxValue(State state) {
        if (state.isTerminal()) {
            return state.getUtility();
        }
        return state.getActions().stream()
                .map(MinimaxTemplate::minValue)
                .max(Comparator.comparing(Double::valueOf)).get();
    }

    private static double minValue(State state) {
        if (state.isTerminal()) {
            return state.getUtility();
        }
        return state.getActions().stream()
                .map(MinimaxTemplate::maxValue)
                .min(Comparator.comparing(Double::valueOf)).get();
    }
}