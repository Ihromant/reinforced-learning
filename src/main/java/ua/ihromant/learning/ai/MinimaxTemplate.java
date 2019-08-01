package ua.ihromant.learning.ai;

import java.util.Comparator;

import ua.ihromant.learning.state.Action;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

public final class MinimaxTemplate implements AITemplate {
    private final Player player;
    public MinimaxTemplate(Player player) {
        this.player = player;
    }

    public Action decision(State state) {
        return new Action(player, state, state.getActions().map(Action::getTo)
                .max(Comparator.comparing(this::minValue)).get());
    }

    private double maxValue(State state) {
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getActions()
                .map(Action::getTo)
                .map(this::minValue)
                .max(Comparator.comparing(Double::valueOf)).get();
    }

    private double minValue(State state) {
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getActions()
                .map(Action::getTo)
                .map(this::maxValue)
                .min(Comparator.comparing(Double::valueOf)).get();
    }
}