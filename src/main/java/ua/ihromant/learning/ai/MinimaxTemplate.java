package ua.ihromant.learning.ai;

import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MinimaxTemplate<A> implements AITemplate<A> {
    private final Player player;
    public MinimaxTemplate(Player player) {
        this.player = player;
    }

    public State<A> decision(State<A> state) {
        Map<State, Double> values = state.getStates().collect(Collectors.toMap(Function.identity(), this::minValue));
        System.out.println(values);
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
                .mapToDouble(this::maxValue).min().orElseThrow(IllegalStateException::new);
    }
}