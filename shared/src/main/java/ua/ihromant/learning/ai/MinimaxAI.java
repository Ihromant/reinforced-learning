package ua.ihromant.learning.ai;

import ua.ihromant.learning.agent.Agent;
import ua.ihromant.learning.state.GameResult;
import ua.ihromant.learning.state.Player;
import ua.ihromant.learning.state.State;

import java.util.Comparator;

public final class MinimaxAI<A> implements Agent<A> {
    private final Player player;
    public MinimaxAI(Player player) {
        this.player = player;
    }

    @Override
    public State<A> decision(State<A> from) {
        return from.getStates().max(Comparator.comparing(this::minValue)).orElseThrow(IllegalStateException::new);
    }

    private GameResult maxValue(State<A> state) {
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getStates()
                .map(this::minValue).max(Comparator.naturalOrder()).orElseThrow(IllegalStateException::new);
    }

    private GameResult minValue(State<A> state) {
        if (state.isTerminal()) {
            return state.getUtility(player);
        }
        return state.getStates()
                .map(this::maxValue).min(Comparator.naturalOrder()).orElseThrow(IllegalStateException::new);
    }
}