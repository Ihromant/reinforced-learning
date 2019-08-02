package ua.ihromant.learning.ai;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MinimaxTemplate<A> implements Agent<A> {
    private final Player player;
    public MinimaxTemplate(Player player) {
        this.player = player;
    }

    @Override
    public State<A> decision(Stream<State<A>> possible) {
        return possible.max(Comparator.comparing(this::minValue)).orElseThrow(IllegalStateException::new);
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