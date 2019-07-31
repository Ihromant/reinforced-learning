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

    public <A> State<A> decision(State<A> state) {
        return state.getActions().map(act -> state.apply(act))
                .max(Comparator.comparing(this::minValue)).get();
    }

    private <A> double maxValue(State<A> state) {
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getActions()
                .mapToDouble(act -> minValue(state.apply(act)))
                .max().orElseThrow(IllegalStateException::new);
    }

    private <A> double minValue(State<A> state) {
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getActions()
                .mapToDouble(act -> maxValue(state.apply(act)))
                .min().orElseThrow(IllegalStateException::new);
    }
}