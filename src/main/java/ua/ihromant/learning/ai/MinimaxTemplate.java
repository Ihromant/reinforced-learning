package ua.ihromant.learning.ai;

import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

import java.util.Comparator;

public final class MinimaxTemplate<A> implements AITemplate<A> {
    private final Player player;
    public MinimaxTemplate(Player player) {
        this.player = player;
    }

    public State<A> decision(State<A> state) {
        return state.getStates()
                .max(Comparator.comparing(this::minValue)).orElseThrow(IllegalStateException::new);
    }

    private double maxValue(State<A> state) {
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getStates()
                .mapToDouble(this::minValue).max().orElseThrow(IllegalStateException::new);
    }

    private double minValue(State<A> state) {
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getStates()
                .mapToDouble(this::maxValue).max().orElseThrow(IllegalStateException::new);
    }
}